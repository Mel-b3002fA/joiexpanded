package com.example.chatbot.model;

import java.util.List;

public class OllamaRequest {
    private String model;
    private List<Message> messages;

    public OllamaRequest() {}

    public OllamaRequest(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}