package com.example.chatbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatbotController {
    private static final Logger logger = LoggerFactory.getLogger(ChatbotController.class);
    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        if (!request.containsKey("message")) {
            logger.error("Invalid message received");
            return ResponseEntity.badRequest().body(Map.of("reply", "Invalid message"));
        }

        String message = request.get("message");
        logger.info("User said: {}", message);

        try {
            String reply = chatbotService.getResponse(message);
            logger.info("Joi replied: {}", reply);
            return ResponseEntity.ok(Map.of("reply", reply));
        } catch (Exception e) {
            logger.error("Error processing chat: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("reply", "Sorry, something went wrong connecting to the model."));
        }
    }
}