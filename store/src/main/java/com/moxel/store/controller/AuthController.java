package com.moxel.store.controller;

import com.moxel.store.dto.RegistrationForm;
import com.moxel.store.service.CustomerService;
import com.moxel.store.service.EmailAlreadyRegisteredException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Controller
public class AuthController {

    private final CustomerService customerService;

    public AuthController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/login")
    public String login(Principal principal) {
        return principal != null ? "redirect:/account" : "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Principal principal, Model model) {
        if (principal != null) {
            return "redirect:/account";
        }
        model.addAttribute("form", new RegistrationForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegistrationForm form,
                           BindingResult result) {
        if (!result.hasFieldErrors("password")
                && form.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            result.rejectValue("password", "tooLong", "Password is too long");
        }
        if (!result.hasFieldErrors("confirmPassword")
                && !result.hasFieldErrors("password")
                && !form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
        }
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            customerService.register(form.getFullName(), form.getEmail(),
                    form.getPhone(), form.getPassword());
        } catch (EmailAlreadyRegisteredException e) {
            result.rejectValue("email", "duplicate",
                    "An account with this email already exists. Try logging in.");
            return "auth/register";
        }
        return "redirect:/login?registered";
    }
}