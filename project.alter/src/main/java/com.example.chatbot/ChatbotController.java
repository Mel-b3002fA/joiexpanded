package com.example.chatbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class ChatbotController {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotController.class);

    @Autowired
    private ChatbotService chatbotService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/tutorial")
    public String tutorial() {
        return "tutorial";
    }

    @PostMapping("/chat")
    @ResponseBody
    public ResponseEntity<Map<String, String>> processChat(@RequestBody Map<String, String> request) {
        if (!request.containsKey("message")) {
            logger.error("Invalid message received");
            return ResponseEntity.badRequest().body(Map.of("reply", "Invalid message"));
        }

        String userMessage = request.get("message");
        logger.info("User said: {}", userMessage);

        try {
            String reply = chatbotService.getResponse(userMessage);
            logger.info("Joi replied: {}", reply);
            return ResponseEntity.ok(Map.of("reply", reply));
        } catch (Exception e) {
            logger.error("Error processing chat: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("reply", "Sorry, something went wrong connecting to the model."));
        }
    }
}