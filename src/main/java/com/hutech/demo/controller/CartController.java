package com.hutech.demo.controller;

import com.hutech.demo.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

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
                            @RequestParam(defaultValue = "/") String redirectUrl,
                            RedirectAttributes redirectAttributes) {
        try {
            cartService.add(productId, quantity);
            redirectAttributes.addFlashAttribute("cartSuccess", "Đã thêm sản phẩm vào giỏ hàng");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("cartError", ex.getMessage());
        }
        return "redirect:" + redirectUrl;
    }

    @GetMapping
    public String cartPage(Model model) {
        cartService.refreshProductInfo();
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("subtotal", cartService.getSubtotal());
        model.addAttribute("cartCount", cartService.getCartCount());
        model.addAttribute("totalQuantity", cartService.getTotalQuantity());
        return "cart/cart";
    }

    @PostMapping("/update/{productId}")
    public String update(@PathVariable Long productId,
                         @RequestParam int quantity,
                         RedirectAttributes redirectAttributes) {
        try {
            cartService.update(productId, quantity);
            redirectAttributes.addFlashAttribute("cartSuccess", "Đã cập nhật số lượng sản phẩm");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("cartError", ex.getMessage());
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove/{productId}")
    public String remove(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        cartService.remove(productId);
        redirectAttributes.addFlashAttribute("cartSuccess", "Đã xóa sản phẩm khỏi giỏ hàng");
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clear(RedirectAttributes redirectAttributes) {
        cartService.clear();
        redirectAttributes.addFlashAttribute("cartSuccess", "Đã xóa toàn bộ giỏ hàng");
        return "redirect:/cart";
    }

    @PostMapping("/api/add/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCartApi(@PathVariable Long productId,
                                                            @RequestParam(defaultValue = "1") int quantity) {
        try {
            cartService.add(productId, quantity);
            return ResponseEntity.ok(buildCartResponse(true, "Đã thêm vào giỏ hàng"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildCartResponse(false, ex.getMessage()));
        }
    }

    @PostMapping("/api/update/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateApi(@PathVariable Long productId,
                                                         @RequestParam int quantity) {
        try {
            cartService.update(productId, quantity);
            return ResponseEntity.ok(buildCartResponse(true, "Đã cập nhật giỏ hàng"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildCartResponse(false, ex.getMessage()));
        }
    }

    @PostMapping("/api/remove/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeApi(@PathVariable Long productId) {
        cartService.remove(productId);
        return ResponseEntity.ok(buildCartResponse(true, "Đã xóa sản phẩm"));
    }

    @GetMapping("/api/summary")
    @ResponseBody
    public Map<String, Object> summaryApi() {
        return buildCartResponse(true, "OK");
    }

    private Map<String, Object> buildCartResponse(boolean success, String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("cartCount", cartService.getCartCount());
        response.put("totalQuantity", cartService.getTotalQuantity());
        response.put("subtotal", cartService.getSubtotal());
        response.put("empty", cartService.isEmpty());
        return response;
    }
}
