package com.moxel.store.service;

import com.moxel.store.dto.ShopProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ShopInfoService {

    private final ShopProfile profile;

    public ShopInfoService(@Value("${shop.name}") String name,
                           @Value("${shop.tagline}") String tagline,
                           @Value("${shop.email}") String email,
                           @Value("${shop.phone}") String phone,
                           @Value("${shop.address}") String address) {
        this.profile = new ShopProfile(name, tagline, email, phone, address);
    }

    public ShopProfile getProfile() {
        return profile;
    }
}