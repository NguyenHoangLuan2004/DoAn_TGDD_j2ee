package com.hutech.demo.controller;

import com.hutech.demo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        model.addAttribute("latestProducts", productService.getLatestProducts());
        model.addAttribute("flashSaleProducts", productService.getFeaturedProducts());
        model.addAttribute("popularKeywords", new String[]{
                "laptop", "tai nghe", "đồng hồ", "macbook", "iphone 15", "iphone 16e",
                "loa bluetooth", "tablet", "smartwatch", "garmin", "máy in", "điện thoại xiaomi"
        });
        return "home/home";
    }
}
