package com.moxel.store.controller;

import com.moxel.store.service.CatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ProductController {

    private final CatalogService catalogService;

    public ProductController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/products")
    public String list(Model model) {
        model.addAttribute("products", catalogService.listActiveProducts());
        return "products/list";
    }

    @GetMapping("/products/{slug}")
    public String detail(@PathVariable String slug, Model model) {
        var product = catalogService.findActiveProduct(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("product", product);
        return "products/detail";
    }
}