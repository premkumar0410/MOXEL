package com.moxel.store.service;

import com.moxel.store.dto.ProductDetail;
import com.moxel.store.dto.ProductSummary;
import com.moxel.store.dto.VariantView;
import com.moxel.store.model.Product;
import com.moxel.store.model.ProductVariant;
import com.moxel.store.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {

    private final ProductRepository productRepository;

    public CatalogService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductSummary> listActiveProducts() {
        return productRepository.findAllActiveWithDetails().stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ProductDetail> findActiveProduct(String slug) {
        return productRepository.findActiveBySlugWithDetails(slug)
                .map(this::toDetail);
    }

    private ProductSummary toSummary(Product p) {
        BigDecimal fromPrice = p.getVariants().stream()
                .map(ProductVariant::getPrice)
                .min(Comparator.naturalOrder())
                .orElse(null);
        return new ProductSummary(p.getId(), p.getName(), p.getSlug(),
                p.getCategory().getName(), p.getFabric(), p.getGsm(), fromPrice);
    }
    private static final List<String> SIZE_ORDER =
            List.of("XS", "S", "M", "L", "XL", "XXL", "3XL");

    private int sizeRank(String size) {
        int i = SIZE_ORDER.indexOf(size);
        return i >= 0 ? i : Integer.MAX_VALUE;
    }

    private ProductDetail toDetail(Product p) {
        List<VariantView> variants = p.getVariants().stream()
                .sorted(Comparator.comparing(ProductVariant::getColor)
                        .thenComparingInt(v -> sizeRank(v.getSize())))
                .map(v -> new VariantView(v.getId(), v.getSize(), v.getColor(),
                        v.getSku(), v.getPrice(), v.getStock() > 0))
                .toList();
        return new ProductDetail(p.getId(), p.getName(), p.getSlug(),
                p.getDescription(), p.getCategory().getName(),
                p.getFabric(), p.getGsm(), variants);
    }
}