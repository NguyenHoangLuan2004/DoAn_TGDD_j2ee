package com.hutech.demo.controller;

import com.hutech.demo.model.Category;
import com.hutech.demo.model.CustomerOrder;
import com.hutech.demo.model.Product;
import com.hutech.demo.repository.AppUserRepository;
import com.hutech.demo.repository.CustomerOrderRepository;
import com.hutech.demo.service.CategoryService;
import com.hutech.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final AppUserRepository appUserRepository;
    private final CustomerOrderRepository customerOrderRepository;

    public AdminController(ProductService productService,
                           CategoryService categoryService,
                           AppUserRepository appUserRepository,
                           CustomerOrderRepository customerOrderRepository) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.appUserRepository = appUserRepository;
        this.customerOrderRepository = customerOrderRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String dashboard(Model model) {
        List<Product> products = productService.getAllProducts();
        List<Category> categories = categoryService.getAllCategories();
        List<CustomerOrder> orders = customerOrderRepository.findAll();

        long productCount = products.size();
        long categoryCount = categories.size();
        long userCount = appUserRepository.count();
        long orderCount = orders.size();
        long outOfStockCount = products.stream().filter(p -> p.getSafeStock() <= 0).count();
        long lowStockCount = products.stream().filter(p -> p.getSafeStock() > 0 && p.getSafeStock() <= 5).count();
        double totalRevenue = orders.stream()
                .mapToDouble(CustomerOrder::getGrandTotal)
                .sum();

        List<CustomerOrder> recentOrders = orders.stream()
                .sorted(Comparator.comparing(CustomerOrder::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(4)
                .toList();

        model.addAttribute("productCount", productCount);
        model.addAttribute("categoryCount", categoryCount);
        model.addAttribute("userCount", userCount);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("outOfStockCount", outOfStockCount);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("revenueText", formatCurrency(totalRevenue));
        model.addAttribute("revenueCompact", formatCompactCurrency(totalRevenue));
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("topKeywords", buildTopKeywords(products, categories));
        model.addAttribute("categoryOverview", buildCategoryOverview(categories));
        return "admin/dashboard";
    }

    @GetMapping("/products")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String products(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products-list";
    }

    @GetMapping("/products/add")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String addProductForm(Model model) {
        Product product = new Product();
        product.setCategory(new Category());
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/product-form";
        }

        if (product.getCategory() == null || product.getCategory().getId() == null) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("error", "Vui lòng chọn danh mục");
            return "admin/product-form";
        }

        productService.save(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/product-form";
    }

    @GetMapping("/products/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public String categories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories-list";
    }

    @GetMapping("/categories/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category-form";
    }

    @PostMapping("/categories/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveCategory(@Valid @ModelAttribute("category") Category category,
                               BindingResult result) {
        if (result.hasErrors()) {
            return "admin/category-form";
        }

        categoryService.save(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editCategory(@PathVariable Long id, Model model) {
        Category category = categoryService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục"));

        model.addAttribute("category", category);
        return "admin/category-form";
    }

    @GetMapping("/categories/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }

    private String formatCurrency(double value) {
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        return numberFormat.format(Math.round(value)) + "₫";
    }

    private String formatCompactCurrency(double value) {
        if (value >= 1_000_000_000) {
            return String.format(Locale.US, "%.1f tỷ", value / 1_000_000_000d).replace('.', ',');
        }
        if (value >= 1_000_000) {
            return String.format(Locale.US, "%.1f triệu", value / 1_000_000d).replace('.', ',');
        }
        if (value <= 0) {
            return "0đ";
        }
        return formatCurrency(value);
    }

    private List<String> buildTopKeywords(List<Product> products, List<Category> categories) {
        Set<String> keywords = new LinkedHashSet<>();

        categories.stream()
                .map(Category::getName)
                .filter(name -> name != null && !name.isBlank())
                .map(String::trim)
                .forEach(keywords::add);

        products.stream()
                .map(Product::getBrand)
                .filter(brand -> brand != null && !brand.isBlank())
                .map(String::trim)
                .forEach(keywords::add);

        products.stream()
                .map(Product::getName)
                .filter(name -> name != null && !name.isBlank())
                .map(name -> name.length() > 18 ? name.substring(0, 18).trim() : name.trim())
                .forEach(keywords::add);

        keywords.addAll(Arrays.asList(
                "laptop gaming", "điện thoại", "tai nghe", "tablet",
                "phụ kiện", "smartwatch", "khuyến mãi", "giảm giá"
        ));

        return new ArrayList<>(keywords).stream().limit(18).toList();
    }

    private List<String> buildCategoryOverview(List<Category> categories) {
        List<String> overview = categories.stream()
                .map(Category::getName)
                .filter(name -> name != null && !name.isBlank())
                .limit(4)
                .toList();

        if (overview.size() >= 4) {
            return overview;
        }

        List<String> fallback = new ArrayList<>(overview);
        List<String> defaults = List.of("Điện thoại", "Laptop", "Tablet", "Phụ kiện");
        for (String item : defaults) {
            if (fallback.size() >= 4) break;
            if (!fallback.contains(item)) {
                fallback.add(item);
            }
        }
        return fallback;
    }
}
