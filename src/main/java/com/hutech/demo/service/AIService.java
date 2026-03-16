package com.hutech.demo.service;

import com.hutech.demo.model.Product;
import com.hutech.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AIService {

    private final ProductRepository productRepository;
    private final OpenAIService openAIService;

    public AIService(ProductRepository productRepository,
                     OpenAIService openAIService) {

        this.productRepository = productRepository;
        this.openAIService = openAIService;
    }

    public Map<String,Object> handle(String message){

        Map<String,Object> response = new HashMap<>();

        if(message == null || message.trim().isEmpty()){
            response.put("type","text");
            response.put("reply","Bạn cần hỗ trợ gì?");
            return response;
        }

        String msg = message.toLowerCase();


        // ===== CHÀO =====
        if(msg.contains("xin chào") || msg.contains("hello") || msg.contains("hi")){
            response.put("type","text");
            response.put("reply",
                    "Xin chào 👋 Tôi là trợ lý AI của Thế Giới Di Động. Bạn cần tìm sản phẩm gì?");
            return response;
        }


        // ===== LAPTOP =====
        if(msg.contains("laptop") || msg.contains("macbook")){
            return buildProductResponse("💻 Một số laptop nổi bật:", "laptop", "macbook");
        }


        // ===== IPHONE =====
        if(msg.contains("iphone")){
            return buildProductResponse("📱 Một số iPhone đang bán:", "iphone");
        }


        // ===== SAMSUNG =====
        if(msg.contains("samsung")){
            return buildProductResponse("📱 Điện thoại Samsung nổi bật:", "samsung");
        }


        // ===== TƯ VẤN =====
        if(msg.contains("tư vấn")){
            response.put("type","text");
            response.put("reply",
                    """
                    Bạn muốn tư vấn sản phẩm nào?

                    • Laptop
                    • Điện thoại
                    • Đồng hồ
                    • Tai nghe

                    Ví dụ: laptop dưới 20 triệu
                    """);
            return response;
        }


        // ===== CHATGPT =====
        String aiReply = openAIService.askGPT(message);

        response.put("type","text");
        response.put("reply", aiReply);

        return response;
    }



    // ===============================
    // BUILD PRODUCT RESPONSE
    // ===============================

    private Map<String,Object> buildProductResponse(String title, String... keywords){

        Map<String,Object> response = new HashMap<>();

        List<Product> products = productRepository.findAll();

        StringBuilder text = new StringBuilder(title + "\n\n");

        int count = 0;

        for(Product p : products){

            String name = Optional.ofNullable(p.getName()).orElse("").toLowerCase();

            for(String keyword : keywords){

                if(name.contains(keyword)){

                    text.append("• ")
                            .append(p.getName())
                            .append(" - ")
                            .append(String.format("%,.0f",p.getPrice()))
                            .append("đ\n");

                    count++;
                    break;
                }
            }

            if(count == 3) break;
        }

        if(count == 0){
            text.append("Hiện chưa có sản phẩm phù hợp.");
        }

        response.put("type","text");
        response.put("reply", text.toString());

        return response;
    }

}