import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class OllamaClient {
    private final String ollamaUrl;
    private final WebClient webClient;

    public OllamaClient(@Value("${ollama.url}") String ollamaUrl) {
        this.ollamaUrl = ollamaUrl;
        this.webClient = WebClient.builder().baseUrl(ollamaUrl).build();
        if (ollamaUrl == null || ollamaUrl.isEmpty()) {
            logger.error("OLLAMA_URL is not set");
            throw new IllegalStateException("OLLAMA_URL is not set");
        }
    }

    public String generateResponse(String prompt) {
        Map<String, String> request = new HashMap<>();
        request.put("model", "llama3");
        request.put("prompt", prompt);

        logger.debug("Sending request to Ollama at {}/api/generate with model llama3 and prompt: {}",
                ollamaUrl, prompt);

        return webClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
                .map(responses -> {
                    // Combine streaming responses
                    StringBuilder combined = new StringBuilder();
                    for (String response : responses) {
                        // Parse each JSON object and extract "response" field
                        // This is a simplified example; use a JSON parser like Jackson
                        if (response.contains("\"response\"")) {
                            String content = response.split("\"response\":\"")[1].split("\"")[0];
                            combined.append(content);
                        }
                    }
                    return combined.toString();
                })
                .block();
    }
}