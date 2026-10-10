package com.moxel.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/privacy-policy")
    public String privacy() { return "pages/privacy-policy"; }

    @GetMapping("/terms")
    public String terms() { return "pages/terms"; }

    @GetMapping("/refund-policy")
    public String refund() { return "pages/refund-policy"; }

    @GetMapping("/shipping-policy")
    public String shipping() { return "pages/shipping-policy"; }

    @GetMapping("/contact")
    public String contact() { return "pages/contact"; }
}