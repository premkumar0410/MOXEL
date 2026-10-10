package com.moxel.store.service;

import com.moxel.store.cart.Cart;
import com.moxel.store.dto.CartLineView;
import com.moxel.store.dto.CartResult;
import com.moxel.store.dto.CartView;
import com.moxel.store.model.ProductVariant;
import com.moxel.store.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CartService {

    public static final int MAX_QUANTITY_PER_LINE = 20;
    public static final int MAX_LINES = 30;

    private final ProductVariantRepository variantRepository;

    public CartService(ProductVariantRepository variantRepository) {
        this.variantRepository = variantRepository;
    }

    @Transactional(readOnly = true)
    public CartResult add(Cart cart, Long variantId, int quantity) {
        if (quantity < 1 || quantity > MAX_QUANTITY_PER_LINE) {
            return CartResult.fail("Please choose a quantity between 1 and "
                    + MAX_QUANTITY_PER_LINE + ".");
        }
        Optional<ProductVariant> found = variantRepository.findActiveById(variantId);
        if (found.isEmpty()) {
            return CartResult.fail("Sorry, that item is no longer available.");
        }
        ProductVariant variant = found.get();

        int current = cart.quantityOf(variantId);
        if (current == 0 && cart.lineCount() >= MAX_LINES) {
            return CartResult.fail("Your cart is full. Please check out or remove an item first.");
        }
        int wanted = current + quantity;
        if (wanted > MAX_QUANTITY_PER_LINE) {
            return CartResult.fail("You can order up to " + MAX_QUANTITY_PER_LINE
                    + " of one item online. For larger orders, please contact us for a bulk quote.");
        }
        if (variant.getStock() <= 0) {
            return CartResult.fail("Sorry, that item is out of stock.");
        }
        if (variant.getStock() < wanted) {
            return CartResult.fail("Sorry, we don't have that many in stock.");
        }
        cart.set(variantId, wanted);
        return CartResult.ok("Added to your cart.");
    }

    @Transactional(readOnly = true)
    public CartResult update(Cart cart, Long variantId, int quantity) {
        if (cart.quantityOf(variantId) == 0) {
            return CartResult.fail("That item is not in your cart.");
        }
        if (quantity <= 0) {
            cart.remove(variantId);
            return CartResult.ok("Item removed.");
        }
        if (quantity > MAX_QUANTITY_PER_LINE) {
            return CartResult.fail("You can order up to " + MAX_QUANTITY_PER_LINE
                    + " of one item online. For larger orders, please contact us for a bulk quote.");
        }
        Optional<ProductVariant> found = variantRepository.findActiveById(variantId);
        if (found.isEmpty()) {
            cart.remove(variantId);
            return CartResult.fail("Sorry, that item is no longer available and was removed.");
        }
        if (found.get().getStock() < quantity) {
            return CartResult.fail("Sorry, we don't have that many in stock.");
        }
        cart.set(variantId, quantity);
        return CartResult.ok("Cart updated.");
    }

    public void remove(Cart cart, Long variantId) {
        cart.remove(variantId);
    }

    @Transactional(readOnly = true)
    public CartView view(Cart cart) {
        Map<Long, Integer> lines = cart.lines();
        if (lines.isEmpty()) {
            return new CartView(List.of(), 0, BigDecimal.ZERO, false, false);
        }

        Map<Long, ProductVariant> byId = variantRepository
                .findActiveByIdIn(lines.keySet()).stream()
                .collect(Collectors.toMap(ProductVariant::getId, Function.identity()));

        List<CartLineView> views = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int itemCount = 0;
        boolean stockProblem = false;
        boolean removedAny = false;

        for (Map.Entry<Long, Integer> line : lines.entrySet()) {
            ProductVariant v = byId.get(line.getKey());
            if (v == null) {
                cart.remove(line.getKey());
                removedAny = true;
                continue;
            }
            int qty = line.getValue();
            boolean stockOk = v.getStock() >= qty;
            if (!stockOk) {
                stockProblem = true;
            }
            BigDecimal lineTotal = v.getPrice().multiply(BigDecimal.valueOf(qty));
            subtotal = subtotal.add(lineTotal);
            itemCount += qty;
            views.add(new CartLineView(v.getId(), v.getProduct().getName(),
                    v.getProduct().getSlug(), v.getColor(), v.getSize(),
                    v.getPrice(), qty, lineTotal, stockOk));
        }
        return new CartView(views, itemCount, subtotal, stockProblem, removedAny);
    }
}