package com.moxel.store.dto;

import java.math.BigDecimal;

public record ProductSummary(
        Long id,
        String name,
        String slug,
        String categoryName,
        String fabric,
        Integer gsm,
        BigDecimal fromPrice) { }