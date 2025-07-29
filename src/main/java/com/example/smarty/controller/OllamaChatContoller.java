package com.example.smarty.controller;



import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.smarty.model.ChatMessage;
import com.example.smarty.service.OllamaChatService;

import reactor.core.publisher.Flux;

@RestController
public class OllamaChatContoller {
	


	    private final ChatClient chatClient;

	    // Spring AI will auto-configure and inject ChatClient.Builder
	    public OllamaChatContoller(ChatClient.Builder chatClientBuilder) {
	        this.chatClient = chatClientBuilder.build();
	    }

	    // --- Simple Blocking Chat Endpoint ---
	    @GetMapping("/chat")
	    public ChatMessage chat( ChatMessage message) {
	        // Send a simple user prompt and get a single String response
	    	ChatMessage response = (ChatMessage) chatClient.prompt().user(message).call().
			return response;
	    		
		
	    			

	        
	}
}