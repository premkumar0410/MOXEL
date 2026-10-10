package com.moxel.store.service;

import com.moxel.store.cart.Cart;
import com.moxel.store.dto.CartResult;
import com.moxel.store.dto.CartView;
import com.moxel.store.model.Category;
import com.moxel.store.model.Product;
import com.moxel.store.model.ProductVariant;
import com.moxel.store.repository.ProductVariantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    ProductVariantRepository variantRepository;

    @InjectMocks
    CartService cartService;

    private ProductVariant variant(long id, String price, int stock) {
        Category category = new Category("Round Neck", "round-neck");
        Product product = new Product(category, "Test Tee", "test-tee", "d", "Cotton", 180);
        ProductVariant v = new ProductVariant("M", "Black", "SKU-" + id,
                new BigDecimal(price), stock);
        product.addVariant(v);
        ReflectionTestUtils.setField(v, "id", id);
        return v;
    }

    @Test
    void addsItemWhenInStock() {
        when(variantRepository.findActiveById(1L))
                .thenReturn(Optional.of(variant(1L, "399.00", 10)));
        Cart cart = new Cart();

        CartResult result = cartService.add(cart, 1L, 2);

        assertTrue(result.success());
        assertEquals(2, cart.quantityOf(1L));
    }

    @Test
    void rejectsQuantityBeyondStock() {
        when(variantRepository.findActiveById(1L))
                .thenReturn(Optional.of(variant(1L, "399.00", 3)));
        Cart cart = new Cart();

        CartResult result = cartService.add(cart, 1L, 5);

        assertFalse(result.success());
        assertEquals(0, cart.quantityOf(1L));
    }

    @Test
    void rejectsQuantityAbovePerLineLimitWithoutTouchingDatabase() {
        Cart cart = new Cart();

        CartResult result = cartService.add(cart, 1L, 21);

        assertFalse(result.success());
        verifyNoInteractions(variantRepository);
    }

    @Test
    void rejectsNegativeQuantity() {
        Cart cart = new Cart();

        CartResult result = cartService.add(cart, 1L, -5);

        assertFalse(result.success());
        verifyNoInteractions(variantRepository);
    }

    @Test
    void rejectsUnknownOrInactiveVariant() {
        when(variantRepository.findActiveById(99L)).thenReturn(Optional.empty());
        Cart cart = new Cart();

        CartResult result = cartService.add(cart, 99L, 1);

        assertFalse(result.success());
        assertEquals(0, cart.quantityOf(99L));
    }

    @Test
    void addingTwiceStillRespectsStock() {
        when(variantRepository.findActiveById(1L))
                .thenReturn(Optional.of(variant(1L, "399.00", 5)));
        Cart cart = new Cart();

        assertTrue(cartService.add(cart, 1L, 3).success());
        assertFalse(cartService.add(cart, 1L, 3).success());
        assertEquals(3, cart.quantityOf(1L));
    }

    @Test
    void updatingToZeroRemovesTheLine() {
        Cart cart = new Cart();
        cart.set(1L, 2);

        CartResult result = cartService.update(cart, 1L, 0);

        assertTrue(result.success());
        assertEquals(0, cart.quantityOf(1L));
    }

    @Test
    void viewUsesDatabasePricesAndDropsUnavailableLines() {
        Cart cart = new Cart();
        cart.set(1L, 2);
        cart.set(2L, 1);
        when(variantRepository.findActiveByIdIn(any()))
                .thenReturn(List.of(variant(1L, "399.00", 10)));

        CartView view = cartService.view(cart);

        assertEquals(1, view.lines().size());
        assertEquals(new BigDecimal("798.00"), view.subtotal());
        assertTrue(view.removedUnavailable());
        assertEquals(0, cart.quantityOf(2L));
    }
}