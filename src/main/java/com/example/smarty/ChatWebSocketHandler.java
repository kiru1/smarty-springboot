package com.example.smarty;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.smarty.service.OllamaChatService;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

	private final OllamaChatService ollamaChatService;

	public ChatWebSocketHandler(OllamaChatService ollamaChatService) {
		this.ollamaChatService = ollamaChatService;
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

		try {
			String userInput = message.getPayload();

			// Send prompt to Ollama and get response
			String ollamaResponse = ollamaChatService.queryOllama(userInput);
		
			// Send response back to frontend
			if (session.isOpen()) {
				System.out.println("Response from Ollama: " + ollamaResponse);
				session.sendMessage(new TextMessage(ollamaResponse));
			} else {
				System.out.println("WebSocket session is closed.");
			}
		} catch (Exception e) {
			e.printStackTrace(); // or use logger
		}

	}
}
