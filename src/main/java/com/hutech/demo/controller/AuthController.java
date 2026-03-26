package com.hutech.demo.controller;

import com.hutech.demo.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(name = "redirect", required = false) String redirect,
                            @RequestParam(name = "error", required = false) String error,
                            @RequestParam(name = "logout", required = false) String logout,
                            @RequestParam(name = "registered", required = false) String registered,
                            Model model) {

        model.addAttribute("redirect", redirect == null || redirect.isBlank() ? "/" : redirect);

        if (error != null) {
            model.addAttribute("error", "Sai email hoặc mật khẩu");
        }

        if (logout != null) {
            model.addAttribute("message", "Bạn đã đăng xuất");
        }

        if (registered != null) {
            model.addAttribute("message", "Đăng ký thành công, vui lòng đăng nhập");
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String phone,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        model.addAttribute("fullName", fullName);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);

        if (fullName == null || fullName.isBlank()) {
            model.addAttribute("error", "Họ tên không được để trống");
            return "auth/register";
        }

        if (email == null || email.isBlank()) {
            model.addAttribute("error", "Email không được để trống");
            return "auth/register";
        }

        if (phone == null || phone.isBlank()) {
            model.addAttribute("error", "Số điện thoại không được để trống");
            return "auth/register";
        }

        if (password == null || password.isBlank()) {
            model.addAttribute("error", "Mật khẩu không được để trống");
            return "auth/register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp");
            return "auth/register";
        }

        try {
            authService.register(fullName, email, phone, password);
            redirectAttributes.addAttribute("registered", "true");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        } catch (Exception e) {
            model.addAttribute("error", "Đăng ký thất bại, vui lòng thử lại");
            return "auth/register";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}