package com.example.smarty.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.smarty.model.ChatMessage;

@Controller
public class ChatController {

	// server application

	@MessageMapping("/send") // /app/send// clients send message to the server
	@SendTo("/topic/messages")//server sends message back
	public ChatMessage send(ChatMessage message) throws Exception{
		System.out.println("message recieved from client" + message.getContent());
		return new ChatMessage(message.getContent()); // Broadcasts to all clients
	}
}