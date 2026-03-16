package com.hutech.demo.controller;

import com.hutech.demo.model.AppUser;
import com.hutech.demo.model.CustomerOrder;
import com.hutech.demo.service.AuthService;
import com.hutech.demo.service.CartService;
import com.hutech.demo.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final AuthService authService;

    public OrderController(OrderService orderService, CartService cartService, AuthService authService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.authService = authService;
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        AppUser user = getCurrentUser(session);
        if (user == null) {
            return "redirect:/login?redirect=/order/checkout";
        }
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("subtotal", cartService.getSubtotal());
        model.addAttribute("shippingFee", 0);
        model.addAttribute("availablePoints", user.getPoints());
        model.addAttribute("user", user);
        return "cart/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(HttpSession session,
                              @RequestParam String fullName,
                              @RequestParam String phone,
                              @RequestParam String address,
                              @RequestParam(required = false) String note,
                              @RequestParam(defaultValue = "0") int pointsUsed,
                              Model model) {
        AppUser user = getCurrentUser(session);
        if (user == null) {
            return "redirect:/login?redirect=/order/checkout";
        }
        if (cartService.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        CustomerOrder order = orderService.placeOrder(user, fullName, phone, address, note,
                pointsUsed, cartService.getItems(), 0);
        cartService.clear();
        model.addAttribute("order", order);
        model.addAttribute("currentUser", user);
        return "cart/order-confirmation";
    }

    private AppUser getCurrentUser(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId instanceof Long id) {
            return authService.getById(id).orElse(null);
        }
        return null;
    }
}
