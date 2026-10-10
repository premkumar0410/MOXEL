package com.moxel.store.dto;

import java.util.List;

public record ProductDetail(
        Long id,
        String name,
        String slug,
        String description,
        String categoryName,
        String fabric,
        Integer gsm,
        List<VariantView> variants) { }