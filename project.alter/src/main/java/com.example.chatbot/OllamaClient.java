package com.example.chatbot;

import com.example.chatbot.model.Message;
import com.example.chatbot.model.OllamaRequest;
import com.example.chatbot.model.OllamaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class OllamaClient {

    @Value("${ollama.url}")
    private String ollamaUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(List<Message> conversation) {
        if (ollamaUrl == null || ollamaUrl.isEmpty()) {
            throw new IllegalStateException("OLLAMA_URL is not set");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        OllamaRequest request = new OllamaRequest("llama3", conversation);
        HttpEntity<OllamaRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<OllamaResponse> response = restTemplate.postForEntity(
                ollamaUrl + "/api/chat", entity, OllamaResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                && response.getBody().getMessage() != null) {
            return response.getBody().getMessage().getContent();
        } else {
            throw new RuntimeException("Unexpected response from Ollama: " + response.getBody());
        }
    }
}