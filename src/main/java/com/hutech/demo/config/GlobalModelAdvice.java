package com.hutech.demo.config;

import com.hutech.demo.model.AppUser;
import com.hutech.demo.service.AuthService;
import com.hutech.demo.service.CartService;
import com.hutech.demo.service.CategoryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final CategoryService categoryService;
    private final AuthService authService;
    private final CartService cartService;

    public GlobalModelAdvice(CategoryService categoryService, AuthService authService, CartService cartService) {
        this.categoryService = categoryService;
        this.authService = authService;
        this.cartService = cartService;
    }

    @ModelAttribute("navCategories")
    public Object navCategories() {
        return categoryService.getAllCategories();
    }

    @ModelAttribute("cartCount")
    public int cartCount() {
        return cartService.getCartCount();
    }

    @ModelAttribute("cartSubtotal")
    public double cartSubtotal() {
        return cartService.getSubtotal();
    }

    @ModelAttribute("currentUser")
    public AppUser currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String email = authentication.getName();
        if (email == null || email.isBlank() || "anonymousUser".equalsIgnoreCase(email)) {
            return null;
        }

        return authService.getByEmail(email).orElse(null);
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        AppUser user = currentUser(authentication);
        return user != null && user.isAdmin();
    }

    @ModelAttribute("isManager")
    public boolean isManager(Authentication authentication) {
        AppUser user = currentUser(authentication);
        return user != null && user.isManager();
    }

    @ModelAttribute("isUser")
    public boolean isUser(Authentication authentication) {
        AppUser user = currentUser(authentication);
        return user != null && user.isUser();
    }

    @ModelAttribute("isLoggedIn")
    public boolean isLoggedIn(Authentication authentication) {
        return currentUser(authentication) != null;
    }
}
