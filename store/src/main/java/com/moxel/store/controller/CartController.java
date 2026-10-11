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

import java.util.regex.Pattern;

@Controller
@RequestMapping("/cart")
public class CartController {

    // Only these local paths are accepted as a "go back to" target.
    private static final Pattern SAFE_RETURN =
            Pattern.compile("^/products(/[a-z0-9-]{1,220})?$");

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
                      @RequestParam(required = false) String returnTo,
                      HttpSession session, RedirectAttributes redirect) {
        CartResult result = cartService.add(cartStore.getOrCreate(session), variantId, quantity);
        flash(redirect, result);
        if (result.success()) {
            redirect.addFlashAttribute("flashCartLink", true);
        }
        return "redirect:" + safeReturnPath(returnTo);
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

    private String safeReturnPath(String returnTo) {
        if (returnTo != null && SAFE_RETURN.matcher(returnTo).matches()) {
            return returnTo;
        }
        return "/products";
    }

    private void flash(RedirectAttributes redirect, CartResult result) {
        redirect.addFlashAttribute(
                result.success() ? "flashSuccess" : "flashError", result.message());
    }
}