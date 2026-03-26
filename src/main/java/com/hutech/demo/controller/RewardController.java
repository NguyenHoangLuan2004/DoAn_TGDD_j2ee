package com.hutech.demo.controller;

import com.hutech.demo.model.AppUser;
import com.hutech.demo.model.PointRedemption;
import com.hutech.demo.service.AuthService;
import com.hutech.demo.service.RewardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rewards")
public class RewardController {

    private final RewardService rewardService;
    private final AuthService authService;

    public RewardController(RewardService rewardService, AuthService authService) {
        this.rewardService = rewardService;
        this.authService = authService;
    }

    @GetMapping
    public String rewardsPage(Authentication authentication, Model model) {
        AppUser user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login?redirect=/rewards";
        }

        List<PointRedemption> histories = rewardService.getHistory(user.getId());
        model.addAttribute("currentUser", user);
        model.addAttribute("vouchers", rewardService.getActiveVouchers());
        model.addAttribute("histories", histories);
        model.addAttribute("readyToUseVouchers", histories.stream()
                .filter(PointRedemption::isVerified)
                .collect(Collectors.toList()));
        return "rewards/index";
    }

    @PostMapping("/redeem")
    public String redeem(Authentication authentication,
                         @RequestParam Long voucherId,
                         Model model) {
        AppUser user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login?redirect=/rewards";
        }

        try {
            PointRedemption redemption = rewardService.startRedemption(user, voucherId);
            return "redirect:/rewards/verify/" + redemption.getId();
        } catch (IllegalArgumentException ex) {
            List<PointRedemption> histories = rewardService.getHistory(user.getId());
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("currentUser", user);
            model.addAttribute("vouchers", rewardService.getActiveVouchers());
            model.addAttribute("histories", histories);
            model.addAttribute("readyToUseVouchers", histories.stream()
                    .filter(PointRedemption::isVerified)
                    .collect(Collectors.toList()));
            return "rewards/index";
        }
    }

    @GetMapping("/verify/{id}")
    public String verifyPage(Authentication authentication,
                             @PathVariable Long id,
                             Model model) {
        AppUser user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login?redirect=/rewards";
        }

        PointRedemption redemption = rewardService.getById(id);
        if (!redemption.getUserId().equals(user.getId())) {
            return "redirect:/access-denied";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("redemption", redemption);
        model.addAttribute("demoOtp", redemption.getOtpCode());
        return "rewards/verify-otp";
    }

    @PostMapping("/verify")
    public String verifyOtp(Authentication authentication,
                            @RequestParam Long redemptionId,
                            @RequestParam String otp,
                            Model model) {
        AppUser user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login?redirect=/rewards";
        }

        PointRedemption redemption = rewardService.getById(redemptionId);
        if (!redemption.getUserId().equals(user.getId())) {
            return "redirect:/access-denied";
        }

        try {
            boolean success = rewardService.verifyOtp(user, redemptionId, otp);
            if (!success) {
                model.addAttribute("error", "Mã OTP không đúng");
                model.addAttribute("currentUser", user);
                model.addAttribute("redemption", redemption);
                model.addAttribute("demoOtp", redemption.getOtpCode());
                return "rewards/verify-otp";
            }
            return "redirect:/rewards/result/" + redemptionId;
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("currentUser", user);
            model.addAttribute("redemption", redemption);
            model.addAttribute("demoOtp", redemption.getOtpCode());
            return "rewards/verify-otp";
        }
    }

    @GetMapping("/result/{id}")
    public String resultPage(Authentication authentication,
                             @PathVariable Long id,
                             Model model) {
        AppUser user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login?redirect=/rewards";
        }

        PointRedemption redemption = rewardService.getById(id);
        if (!redemption.getUserId().equals(user.getId())) {
            return "redirect:/access-denied";
        }

        AppUser refreshedUser = authService.getById(user.getId()).orElse(user);

        model.addAttribute("currentUser", refreshedUser);
        model.addAttribute("redemption", redemption);
        model.addAttribute("canUseAtCheckout", redemption.isVerified());
        return "rewards/result";
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
