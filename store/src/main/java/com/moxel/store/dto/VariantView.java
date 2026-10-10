package com.moxel.store.dto;

import java.math.BigDecimal;

public record VariantView(
        Long id,
        String size,
        String color,
        String sku,
        BigDecimal price,
        boolean inStock) { }