package com.moxel.store.controller;

import com.moxel.store.cart.CartStore;
import com.moxel.store.dto.CartResult;
import com.moxel.store.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartStore cartStore;
    private final CartService cartService;

    public CartController(CartStore cartStore, CartService cartService) {
        this.cartStore = cartStore;
        this.cartService = cartService;
    }

    @GetMapping
    public String view(HttpSession session, Model model) {
        model.addAttribute("cart", cartService.view(cartStore.getOrCreate(session)));
        return "cart/view";
    }

    @PostMapping("/add")
    public String add(@RequestParam Long variantId,
                      @RequestParam(defaultValue = "1") int quantity,
                      HttpSession session, RedirectAttributes redirect) {
        flash(redirect, cartService.add(cartStore.getOrCreate(session), variantId, quantity));
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String update(@RequestParam Long variantId,
                         @RequestParam int quantity,
                         HttpSession session, RedirectAttributes redirect) {
        flash(redirect, cartService.update(cartStore.getOrCreate(session), variantId, quantity));
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam Long variantId,
                         HttpSession session, RedirectAttributes redirect) {
        cartService.remove(cartStore.getOrCreate(session), variantId);
        redirect.addFlashAttribute("flashSuccess", "Item removed.");
        return "redirect:/cart";
    }

    private void flash(RedirectAttributes redirect, CartResult result) {
        redirect.addFlashAttribute(
                result.success() ? "flashSuccess" : "flashError", result.message());
    }
}