package com.moxel.store.controller;

import com.moxel.store.cart.CartStore;
import com.moxel.store.dto.ShopProfile;
import com.moxel.store.service.ShopInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final ShopInfoService shopInfoService;
    private final CartStore cartStore;

    public GlobalModelAttributes(ShopInfoService shopInfoService, CartStore cartStore) {
        this.shopInfoService = shopInfoService;
        this.cartStore = cartStore;
    }

    @ModelAttribute("shop")
    public ShopProfile shop() {
        return shopInfoService.getProfile();
    }

    @ModelAttribute("cartCount")
    public int cartCount(HttpServletRequest request) {
        return cartStore.itemCount(request);
    }
}