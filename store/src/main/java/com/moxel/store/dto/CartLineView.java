package com.moxel.store.dto;

import java.math.BigDecimal;

public record CartLineView(
        Long variantId,
        String productName,
        String productSlug,
        String color,
        String size,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal,
        boolean stockOk) { }