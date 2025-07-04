package com.example.chatbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Component
public class OllamaClient {

    private static final Logger logger = LoggerFactory.getLogger(OllamaClient.class);

    private final String ollamaUrl;
    private final RestTemplate restTemplate;

    public OllamaClient(@Value("${ollama.url}") String ollamaUrl) {
        this.ollamaUrl = ollamaUrl;
        this.restTemplate = new RestTemplate();
        if (ollamaUrl == null || ollamaUrl.isEmpty()) {
            logger.error("OLLAMA_URL is not set");
            throw new IllegalStateException("OLLAMA_URL is not set");
        }
    }

    public String generateResponse(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> request = new HashMap<>();
            request.put("model", "llama3");
            request.put("prompt", prompt);

            logger.debug("Sending request to Ollama at {}/api/generate with model llama3 and prompt: {}",
                    ollamaUrl, prompt);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    ollamaUrl + "/api/generate", new HttpEntity<>(request, headers), String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.debug("Received response from Ollama: {}", response.getBody());
                return response.getBody();
            } else {
                logger.warn("Unexpected response from Ollama: status={}, body={}",
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException("Unexpected response from Ollama: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            logger.error("Failed to communicate with Ollama at {}: {}", ollamaUrl, e.getMessage());
            throw new RuntimeException("Ollama API call failed: " + e.getMessage(), e);
        }
    }
}