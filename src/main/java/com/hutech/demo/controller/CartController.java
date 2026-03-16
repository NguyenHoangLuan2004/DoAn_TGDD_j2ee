package com.hutech.demo.controller;

import com.hutech.demo.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            @RequestParam(defaultValue = "/") String redirectUrl) {
        cartService.add(productId, quantity);
        return "redirect:" + redirectUrl;
    }

    @GetMapping
    public String cartPage(Model model) {
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("subtotal", cartService.getSubtotal());
        return "cart/cart";
    }

    @PostMapping("/update/{productId}")
    public String update(@PathVariable Long productId, @RequestParam int quantity) {
        cartService.update(productId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{productId}")
    public String remove(@PathVariable Long productId) {
        cartService.remove(productId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clear() {
        cartService.clear();
        return "redirect:/cart";
    }
}
