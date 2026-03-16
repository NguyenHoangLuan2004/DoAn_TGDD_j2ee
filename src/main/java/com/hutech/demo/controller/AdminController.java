package com.hutech.demo.controller;

import com.hutech.demo.model.AppUser;
import com.hutech.demo.model.Category;
import com.hutech.demo.model.Product;
import com.hutech.demo.service.AuthService;
import com.hutech.demo.service.CategoryService;
import com.hutech.demo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final AuthService authService;

    public AdminController(ProductService productService, CategoryService categoryService, AuthService authService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.authService = authService;
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        if (!checkAdmin(session)) {
            return "redirect:/login?redirect=/admin";
        }
        model.addAttribute("productCount", productService.getAllProducts().size());
        model.addAttribute("categoryCount", categoryService.getAllCategories().size());
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String products(HttpSession session, Model model) {
        if (!checkAdmin(session)) {
            return "redirect:/login?redirect=/admin/products";
        }
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products-list";
    }

    @GetMapping("/products/add")
    public String addProductForm(HttpSession session, Model model) {
        if (!checkAdmin(session)) {
            return "redirect:/login?redirect=/admin/products/add";
        }
        Product product = new Product();
        product.setCategory(new Category());
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(HttpSession session,
                              @Valid @ModelAttribute("product") Product product,
                              BindingResult result,
                              Model model) {
        if (!checkAdmin(session)) {
            return "redirect:/login?redirect=/admin/products";
        }
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/product-form";
        }
        productService.save(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(HttpSession session, @PathVariable Long id, Model model) {
        if (!checkAdmin(session)) {
            return "redirect:/login?redirect=/admin/products";
        }
        Product product = productService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/product-form";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(HttpSession session, @PathVariable Long id) {
        if (!checkAdmin(session)) {
            return "redirect:/login?redirect=/admin/products";
        }
        productService.delete(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/categories")
    public String categories(HttpSession session, Model model) {
        if (!checkAdmin(session)) {
            return "redirect:/login?redirect=/admin/categories";
        }
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories-list";
    }

    @GetMapping("/categories/add")
    public String addCategoryForm(HttpSession session, Model model) {
        if (!checkAdmin(session)) {
            return "redirect:/login?redirect=/admin/categories";
        }
        model.addAttribute("category", new Category());
        return "admin/category-form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(HttpSession session,
                               @Valid @ModelAttribute("category") Category category,
                               BindingResult result) {
        if (!checkAdmin(session)) {
            return "redirect:/login?redirect=/admin/categories";
        }
        if (result.hasErrors()) {
            return "admin/category-form";
        }
        categoryService.save(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(HttpSession session, @PathVariable Long id, Model model) {
        if (!checkAdmin(session)) {
            return "redirect:/login?redirect=/admin/categories";
        }
        Category category = categoryService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục"));
        model.addAttribute("category", category);
        return "admin/category-form";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(HttpSession session, @PathVariable Long id) {
        if (!checkAdmin(session)) {
            return "redirect:/login?redirect=/admin/categories";
        }
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }

    private boolean checkAdmin(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId instanceof Long id) {
            AppUser user = authService.getById(id).orElse(null);
            return user != null && user.isAdmin();
        }
        return false;
    }
}
