/* package com.example.chatbot;

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

    private final String ollamaUrl;
    private final String model;
    private final RestTemplate restTemplate;

    public OllamaClient(@Value("${ollama.url}") String ollamaUrl, @Value("${ollama.model:llama3}") String model) {
        this.ollamaUrl = ollamaUrl;
        this.model = model;
        this.restTemplate = new RestTemplate();
        if (ollamaUrl == null || ollamaUrl.isEmpty()) {
            logger.error("OLLAMA_URL is not set");
            throw new IllegalStateException("OLLAMA_URL is not set");
        }
    }

    public String chat(List<Message> conversation) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            OllamaRequest request = new OllamaRequest(model, conversation);
            HttpEntity<OllamaRequest> entity = new HttpEntity<>(request, headers);

            logger.debug("Sending request to Ollama at {}/api/chat with model {} and {} messages",
                    ollamaUrl, model, conversation.size());

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
            logger.error("Failed to communicate with Ollama at {}/api/chat: {}", ollamaUrl, e.getMessage());
            throw new RuntimeException("Ollama API call failed: " + e.getMessage(), e);
        }
    }
} */


package com.example.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity; // Added import
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class OllamaClient {
    private static final Logger logger = LoggerFactory.getLogger(OllamaClient.class);

    private final String ollamaUrl;
    private final String model;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public OllamaClient(@Value("${ollama.url}") String ollamaUrl, @Value("${ollama.model:llama3}") String model) {
        this.ollamaUrl = ollamaUrl;
        this.model = model;
        this.webClient = WebClient.builder().baseUrl(ollamaUrl).build();
        this.objectMapper = new ObjectMapper();
        if (ollamaUrl == null || ollamaUrl.isEmpty()) {
            logger.error("OLLAMA_URL is not set");
            throw new IllegalStateException("OLLAMA_URL is not set");
        }
    }

    public String generateResponse(String prompt) {
    Map<String, String> request = new HashMap<>();
    request.put("model", model);
    request.put("prompt", prompt);

    logger.debug("Sending request to Ollama at {}/api/generate with model {} and prompt: {}",
            ollamaUrl, model, prompt);

    return webClient.post()
            .uri("/api/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToFlux(String.class)
            .filter(line -> !line.trim().isEmpty())
            .map(line -> {
                try {
                    JsonNode node = objectMapper.readTree(line);
                    return node.get("response").asText();
                } catch (Exception e) {
                    logger.error("Failed to parse response line: {}", line, e);
                    return "";
                }
            })
            .collectList()
            .map(list -> String.join("", list))
            .block();
}
}