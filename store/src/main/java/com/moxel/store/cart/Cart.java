package com.moxel.store.cart;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.Serial;

public class Cart implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<Long, Integer> lines = new LinkedHashMap<>();

    public synchronized Map<Long, Integer> lines() {
        return new LinkedHashMap<>(lines);
    }

    public synchronized int quantityOf(Long variantId) {
        return lines.getOrDefault(variantId, 0);
    }

    public synchronized int lineCount() {
        return lines.size();
    }

    public synchronized int itemCount() {
        return lines.values().stream().mapToInt(Integer::intValue).sum();
    }

    public synchronized void set(Long variantId, int quantity) {
        lines.put(variantId, quantity);
    }

    public synchronized void remove(Long variantId) {
        lines.remove(variantId);
    }
}