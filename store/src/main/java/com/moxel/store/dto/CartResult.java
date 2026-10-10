package com.moxel.store.dto;

public record CartResult(boolean success, String message) {

    public static CartResult ok(String message) {
        return new CartResult(true, message);
    }

    public static CartResult fail(String message) {
        return new CartResult(false, message);
    }
}