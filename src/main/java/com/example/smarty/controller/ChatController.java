package com.example.smarty.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ChatController {

	private SimpMessagingTemplate messagingTemplate;

	public ChatController(SimpMessagingTemplate messagingTemplate) {

		this.messagingTemplate = messagingTemplate;
	}

	private final HttpClient httpClient = HttpClient.newHttpClient();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@MessageMapping("/chat")
	public void handlePrompt(String prompt) {
		String url = "http://localhost:11434/api/generate";

		// Send user message
		try {
			messagingTemplate.convertAndSend("/topic/stream", Map.of("sender", "You", "text", prompt));
			 //messagingTemplate.convertAndSend("/topic/stream", Map.of("sender", "AI",
			 //"text", ""));

			// Build Ollama request
			String requestBody = objectMapper
					.writeValueAsString(Map.of("model", "tinyllama", "prompt", prompt, "stream", true));

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
					.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(requestBody))
					.build();

			// Stream response
			httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofLines()).thenAccept(response -> {
				response.body().forEach(line -> {
					try {
						if (line != null && !line.isBlank()) {
							JsonNode node = objectMapper.readTree(line);
							String token = node.get("response").asText();

							messagingTemplate.convertAndSend("/topic/stream", Map.of("sender", "AI", "text", token));
						}
					} catch (Exception e) {
						messagingTemplate.convertAndSend("/topic/stream",
								Map.of("sender", "System", "text", "[error]"));
					}
				});
			});

		} catch (Exception e) {
			messagingTemplate.convertAndSend("/topic/stream", Map.of("sender", "System", "text", "[internal error]"));
		}
	}
}

//server application

/*
 * @MessageMapping("/send") // /app/send// clients send message to the server
 * 
 * @SendTo("/topic/messages")//server sends message back public ChatMessage
 * send(ChatMessage message) throws Exception{
 * System.out.println("message recieved from client" + message.getContent());
 * return new ChatMessage(message.getContent()); // Broadcasts to all clients }
 * }
 */