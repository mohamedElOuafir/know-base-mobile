package com.servicebackend.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RagService {

    @Value("${rag-service.url}")
    private String BOT_API_URL ;

    @Autowired
    private RestTemplate restTemplate;

    public String generate(String userMessage) {
        Map<String, Object> request = new HashMap<>();
        request.put("query", userMessage);


        ResponseEntity<Map> response = restTemplate.postForEntity(BOT_API_URL, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return (String) response.getBody().get("response");
        }

        throw new RuntimeException("Bot API failed");
    }
}
