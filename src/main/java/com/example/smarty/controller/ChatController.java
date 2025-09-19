package com.example.smarty.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void handlePrompt(String prompt) {
        // 1️⃣ Send user message immediately
        messagingTemplate.convertAndSend("/topic/stream", Map.of("sender", "You", "text", prompt));

        // 2️⃣ Run the HTTP call and streaming asynchronously
        CompletableFuture.runAsync(() -> streamFromRagServer(prompt));
    }

    private void streamFromRagServer(String prompt) {
        String url = "http://localhost:8000/rag_query_stream";

        try {
            RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> body = Map.of("query", prompt);

            // Stream response line by line
            restTemplate.execute(url, org.springframework.http.HttpMethod.POST,
                (RequestCallback) request -> {
                    headers.forEach((key, value) -> request.getHeaders().put(key, value));
                    objectMapper.writeValue(request.getBody(), body);
                },
                (ResponseExtractor<Void>) response -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            if (line.isEmpty()) continue;

                            try {
                                JsonNode node = objectMapper.readTree(line);
                                if (node.has("response")) {
                                    String token = node.get("response").asText();
                                    // Send each token asynchronously to WebSocket
                                    messagingTemplate.convertAndSend("/topic/stream",
                                            Map.of("sender", "AI", "text", token));
                                }
                            } catch (Exception e) {
                                System.out.println("Skipping invalid JSON line: [" + line + "]");
                            }
                        }
                    }
                    return null;
                });
        } catch (Exception e) {
            e.printStackTrace();
            messagingTemplate.convertAndSend("/topic/stream",
                    Map.of("sender", "System", "text", "[internal streaming error]"));
        }
    }
}
