package com.hutech.demo.controller;

import com.hutech.demo.service.AIService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AIService aiService;

    public ChatController(AIService aiService) {
        this.aiService = aiService;
    }

    /**
     * ==============================
     * API CHAT CHÍNH
     * Frontend gọi: POST /api/chat
     * ==============================
     */
    @PostMapping
    public Map<String,Object> chat(@RequestBody Map<String,String> body){

        Map<String,Object> result = new HashMap<>();

        try {

            String message = body.getOrDefault("message","").trim();

            if(message.isEmpty()){
                result.put("type","text");
                result.put("reply","Bạn hãy nhập câu hỏi nhé.");
                return result;
            }

            // Gọi AI service xử lý
            return aiService.handle(message);

        } catch (Exception e){

            System.out.println("ChatController ERROR:");
            e.printStackTrace();

            result.put("type","text");
            result.put("reply","Xin lỗi, hệ thống AI đang bận. Bạn thử lại sau.");

            return result;
        }
    }


    /**
     * ==============================
     * API kiểm tra server AI
     * GET /api/chat/ping
     * ==============================
     */
    @GetMapping("/ping")
    public Map<String,String> ping(){

        Map<String,String> result = new HashMap<>();

        result.put("status","ok");
        result.put("message","Chat AI đang hoạt động");

        return result;
    }

}
