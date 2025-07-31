package com.example.smarty.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.boot.web.client.RestTemplateBuilder;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class OllamaChatService {
//	/synchronous HTTP client
	private final RestTemplate restTemplate;

	//configure and create RestTemplate instance
	public OllamaChatService(RestTemplateBuilder builder) {
		this.restTemplate = builder.build();
	}

	public String queryOllama(String prompt) {
		String url = "http://localhost:11434/api/generate";

		Map<String, Object> body = Map.of("model", "tinyllama", "prompt", prompt, "stream", false); // disable streaming
																						// for simplicity
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

		org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(url, entity,
				String.class);

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(response.getBody());

			return jsonNode.get("response").asText();
		} catch (Exception e) {
			return "Error parsing Ollama response.";
		}

	}
}