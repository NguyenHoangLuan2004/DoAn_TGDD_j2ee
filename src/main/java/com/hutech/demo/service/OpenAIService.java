package com.hutech.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String OPENAI_URL =
            "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate = new RestTemplate();

    public String askGPT(String message) {

        try {

            // HEADER
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // SYSTEM MESSAGE
            Map<String,String> systemMessage = new HashMap<>();
            systemMessage.put("role","system");
            systemMessage.put(
                    "content",
                    "Bạn là trợ lý AI của Thế Giới Di Động. " +
                            "Hãy tư vấn sản phẩm công nghệ bằng tiếng Việt, ngắn gọn dễ hiểu."
            );

            // USER MESSAGE
            Map<String,String> userMessage = new HashMap<>();
            userMessage.put("role","user");
            userMessage.put("content",message);

            List<Map<String,String>> messages = new ArrayList<>();
            messages.add(systemMessage);
            messages.add(userMessage);

            // REQUEST BODY
            Map<String,Object> body = new HashMap<>();
            body.put("model","gpt-4o-mini");
            body.put("messages",messages);
            body.put("temperature",0.7);

            HttpEntity<Map<String,Object>> request =
                    new HttpEntity<>(body,headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(
                            OPENAI_URL,
                            request,
                            Map.class
                    );

            Map responseBody = response.getBody();

            if(responseBody == null)
                return "AI không phản hồi.";

            List choices = (List) responseBody.get("choices");

            if(choices == null || choices.isEmpty())
                return "AI không có phản hồi.";

            Map firstChoice = (Map) choices.get(0);

            Map msg = (Map) firstChoice.get("message");

            if(msg == null)
                return "AI không có nội dung trả lời.";

            return msg.get("content").toString().trim();

        } catch (Exception e) {

            System.out.println("OpenAI Error: " + e.getMessage());

            return "AI hiện đang bận 🤖. Vui lòng thử lại.";

        }

    }
}