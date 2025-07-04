package com.example.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatbotService {
    private final OllamaClient ollamaClient;

    @Autowired
    public ChatbotService(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    public String getResponse(String message) {
        try {
            return ollamaClient.generateResponse(message);
        } catch (Exception e) {
            return "Sorry, something went wrong connecting to the model.";
        }
    }
}