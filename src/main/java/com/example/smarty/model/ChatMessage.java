package com.example.smarty.model;

public class ChatMessage {

	public ChatMessage() {
		super();
	}

	public ChatMessage(String content) {
		this.content = content;
	}

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
