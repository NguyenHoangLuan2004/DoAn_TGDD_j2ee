package com.hutech.demo.controller;

import com.hutech.demo.model.AppUser;
import com.hutech.demo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(name = "redirect", required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect == null ? "/" : redirect);
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        @RequestParam(defaultValue = "/") String redirect,
                        HttpSession session,
                        Model model) {
        return authService.login(email, password)
                .map(user -> {
                    session.setAttribute("userId", user.getId());
                    return "redirect:" + redirect;
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Sai email hoặc mật khẩu");
                    model.addAttribute("redirect", redirect);
                    return "auth/login";
                });
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
