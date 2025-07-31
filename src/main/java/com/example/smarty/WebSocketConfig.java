package com.example.smarty;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{
	 private final ChatWebSocketHandler handler;

	    public WebSocketConfig(ChatWebSocketHandler handler) {
	        this.handler = handler;
	    }
	 @Override
	    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
	        registry.addHandler(handler, "/chat").setAllowedOrigins("*");
	    }
	/*@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic"); // Topic for broadcasts
		config.setApplicationDestinationPrefixes("/app"); // Prefix for sending
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/chat").setAllowedOriginPatterns("*").withSockJS();
		
				
	}*/

	
}
