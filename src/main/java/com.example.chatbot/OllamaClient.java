/* package com.example.chatbot;

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
} */



package com.example.chatbot;

import com.example.chatbot.model.Message;
import com.example.chatbot.model.OllamaRequest;
import com.example.chatbot.model.OllamaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class OllamaClient {

    private static final Logger logger = LoggerFactory.getLogger(OllamaClient.class);

    @Value("${ollama.url}")
    private String ollamaUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(List<Message> conversation) {
        if (ollamaUrl == null || ollamaUrl.isEmpty()) {
            logger.error("OLLAMA_URL is not set");
            throw new IllegalStateException("OLLAMA_URL is not set");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            OllamaRequest request = new OllamaRequest("llama3", conversation);
            HttpEntity<OllamaRequest> entity = new HttpEntity<>(request, headers);

            logger.debug("Sending request to Ollama at {} with model llama3 and {} messages",
                    ollamaUrl, conversation.size());

            ResponseEntity<OllamaResponse> response = restTemplate.postForEntity(
                    ollamaUrl + "/api/chat", entity, OllamaResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                    && response.getBody().getMessage() != null) {
                String content = response.getBody().getMessage().getContent();
                logger.debug("Received response from Ollama: {}", content);
                return content;
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