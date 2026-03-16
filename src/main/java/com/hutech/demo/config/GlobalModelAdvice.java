package com.hutech.demo.config;

import com.hutech.demo.model.AppUser;
import com.hutech.demo.service.AuthService;
import com.hutech.demo.service.CartService;
import com.hutech.demo.service.CategoryService;
import jakarta.servlet.http.HttpSession;
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

    @ModelAttribute("currentUser")
    public AppUser currentUser(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId instanceof Long id) {
            return authService.getById(id).orElse(null);
        }
        return null;
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(HttpSession session) {
        AppUser user = currentUser(session);
        return user != null && user.isAdmin();
    }
}
