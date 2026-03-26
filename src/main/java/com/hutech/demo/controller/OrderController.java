package com.hutech.demo.controller;

import com.hutech.demo.model.AppUser;
import com.hutech.demo.model.CustomerOrder;
import com.hutech.demo.model.PointRedemption;
import com.hutech.demo.service.AuthService;
import com.hutech.demo.service.CartService;
import com.hutech.demo.service.OrderService;
import com.hutech.demo.service.RewardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final AuthService authService;
    private final RewardService rewardService;

    public OrderController(OrderService orderService,
                           CartService cartService,
                           AuthService authService,
                           RewardService rewardService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.authService = authService;
        this.rewardService = rewardService;
    }

    @GetMapping("/checkout")
    public String checkout(Authentication authentication,
                           @RequestParam(required = false) Long redemptionId,
                           @RequestParam(required = false, defaultValue = CustomerOrder.PAYMENT_METHOD_COD) String paymentMethod,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        AppUser user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login?redirect=/order/checkout";
        }

        if (cartService.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        try {
            cartService.validateCartStock();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("cartError", ex.getMessage());
            return "redirect:/cart";
        }

        double subtotal = cartService.getSubtotal();
        int totalQuantity = cartService.getTotalQuantity();
        double shippingFee = orderService.calculateShippingFee(subtotal, totalQuantity);
        List<PointRedemption> verifiedRedemptions = rewardService.getHistory(user.getId()).stream()
                .filter(PointRedemption::isVerified)
                .collect(Collectors.toList());

        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("availablePoints", user.getPoints());
        model.addAttribute("user", user);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("verifiedRedemptions", verifiedRedemptions);
        model.addAttribute("selectedRedemptionId", redemptionId);
        model.addAttribute("selectedPaymentMethod", paymentMethod);

        if (redemptionId != null) {
            try {
                PointRedemption selectedRedemption = rewardService.getVerifiedRedemptionForCheckout(user, redemptionId);
                model.addAttribute("selectedRedemption", selectedRedemption);
            } catch (IllegalArgumentException ex) {
                model.addAttribute("voucherError", ex.getMessage());
            }
        }

        model.addAttribute("estimatedGrandTotal", subtotal + shippingFee);
        return "cart/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(Authentication authentication,
                              @RequestParam String fullName,
                              @RequestParam String phone,
                              @RequestParam String address,
                              @RequestParam(required = false) String note,
                              @RequestParam(defaultValue = "0") int pointsUsed,
                              @RequestParam(required = false) Long redemptionId,
                              @RequestParam(defaultValue = CustomerOrder.PAYMENT_METHOD_COD) String paymentMethod,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        AppUser user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login?redirect=/order/checkout";
        }

        if (cartService.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        try {
            cartService.validateCartStock();
            double shippingFee = orderService.calculateShippingFee(cartService.getSubtotal(), cartService.getTotalQuantity());

            CustomerOrder order = orderService.placeOrder(
                    user,
                    fullName,
                    phone,
                    address,
                    note,
                    pointsUsed,
                    cartService.getItems(),
                    shippingFee,
                    redemptionId,
                    paymentMethod
            );

            cartService.clear();

            if (CustomerOrder.PAYMENT_METHOD_VNPAY.equalsIgnoreCase(order.getPaymentMethod())) {
                return "redirect:/payment/vnpay/create?orderCode=" + order.getOrderCode();
            }

            AppUser refreshedUser = authService.getById(user.getId()).orElse(user);
            model.addAttribute("order", order);
            model.addAttribute("currentUser", refreshedUser);
            return "cart/order-confirmation";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("checkoutError", ex.getMessage());
            String suffix = redemptionId != null ? "?redemptionId=" + redemptionId + "&paymentMethod=" + paymentMethod
                    : "?paymentMethod=" + paymentMethod;
            return "redirect:/order/checkout" + suffix;
        }
    }

    private AppUser getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String email = authentication.getName();
        if (email == null || email.isBlank() || "anonymousUser".equalsIgnoreCase(email)) {
            return null;
        }

        return authService.getByEmail(email).orElse(null);
    }
}
