package com.hutech.demo.controller;

import com.hutech.demo.model.Category;
import com.hutech.demo.model.Product;
import com.hutech.demo.service.CategoryService;
import com.hutech.demo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products/category/{id}")
    public String categoryPage(@PathVariable Long id, Model model) {
        Category category = categoryService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục"));
        model.addAttribute("category", category);
        model.addAttribute("products", productService.getByCategory(id));
        return "products/category";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", productService.getByCategory(product.getCategory().getId()));
        return "products/detail";
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "q", required = false) String keyword, Model model) {
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("products", productService.search(keyword == null ? "" : keyword));
        return "products/search";
    }
}
