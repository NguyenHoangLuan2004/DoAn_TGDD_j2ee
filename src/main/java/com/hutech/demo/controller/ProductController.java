package com.hutech.demo.controller;

import com.hutech.demo.model.Category;
import com.hutech.demo.model.Product;
import com.hutech.demo.service.CategoryService;
import com.hutech.demo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
        List<Product> products = productService.getByCategory(id);
        model.addAttribute("category", category);
        model.addAttribute("products", products);
        model.addAttribute("availableCount", products.stream().filter(Product::isInStock).count());
        return "products/category";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

        List<Product> relatedProducts = productService.getByCategory(product.getCategory().getId()).stream()
                .filter(item -> !item.getId().equals(product.getId()))
                .limit(8)
                .collect(Collectors.toList());

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("inStock", product.isInStock());
        model.addAttribute("availableStock", product.getSafeStock());
        return "products/detail";
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "q", required = false) String keyword, Model model) {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        List<Product> products = productService.search(safeKeyword);
        model.addAttribute("keyword", safeKeyword);
        model.addAttribute("products", products);
        model.addAttribute("resultCount", products.size());
        return "products/search";
    }
}
