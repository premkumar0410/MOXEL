package com.moxel.store.service;

import com.moxel.store.dto.ProductSummary;
import com.moxel.store.model.Category;
import com.moxel.store.model.Product;
import com.moxel.store.model.ProductVariant;
import com.moxel.store.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    CatalogService catalogService;

    @Test
    void summaryShowsLowestVariantPrice() {
        Category category = new Category("Round Neck", "round-neck");
        Product product = new Product(category, "Test Tee", "test-tee",
                "desc", "Cotton", 180);
        product.addVariant(new ProductVariant("M", "Black", "T-M-B",
                new BigDecimal("499.00"), 5));
        product.addVariant(new ProductVariant("S", "Black", "T-S-B",
                new BigDecimal("449.00"), 5));

        when(productRepository.findAllActiveWithDetails())
                .thenReturn(List.of(product));

        List<ProductSummary> result = catalogService.listActiveProducts();

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("449.00"), result.get(0).fromPrice());
    }
}