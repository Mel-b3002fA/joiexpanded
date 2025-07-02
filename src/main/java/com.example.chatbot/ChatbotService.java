package com.example.chatbot;

import com.example.chatbot.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatbotService {
    @Autowired
    private OllamaClient ollamaClient;

    private final List<Message> conversation = new ArrayList<>();

    public String getResponse(String userMessage) {
        conversation.add(new Message("user", userMessage));
        String reply = ollamaClient.chat(conversation);
        conversation.add(new Message("assistant", reply));
        return reply;
    }
}