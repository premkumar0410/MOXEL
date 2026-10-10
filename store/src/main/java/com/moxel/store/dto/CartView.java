package com.moxel.store.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartView(
        List<CartLineView> lines,
        int itemCount,
        BigDecimal subtotal,
        boolean hasStockProblem,
        boolean removedUnavailable) { }