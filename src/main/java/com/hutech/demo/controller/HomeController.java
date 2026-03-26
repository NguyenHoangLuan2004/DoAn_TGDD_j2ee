package com.hutech.demo.controller;

import com.hutech.demo.model.Product;
import com.hutech.demo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Product> featuredProducts = productService.getFeaturedProducts();
        List<Product> latestProducts = productService.getLatestProducts();

        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("latestProducts", latestProducts);
        model.addAttribute("flashSaleProducts", featuredProducts);
        model.addAttribute("featuredCount", featuredProducts.size());
        model.addAttribute("latestCount", latestProducts.size());
        model.addAttribute("popularKeywords", new String[]{
                "laptop", "tai nghe", "đồng hồ", "macbook", "iphone 15", "iphone 16e",
                "loa bluetooth", "tablet", "smartwatch", "garmin", "máy in", "điện thoại xiaomi"
        });
        return "home/home";
    }
}
