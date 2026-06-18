package com.server.vebbleAi.service.mail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.vebbleAi.dto.EmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class MailGeneratorService {
    private final WebClient webClient;

    public MailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    //@Value annotation is used to inject the values from application.properties or .yml file
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key1}")
    private String geminiApiKey;

    public String generateEmailReply(EmailRequest emailRequest){
        //Build the prompt
        String prompt = buildPrompt(emailRequest);
        //Craft a request
        Map<String , Object> requestBody = Map.of(
                "contents" , new Object[] {
                        Map.of("parts" , new Object[]{
                                Map.of("text" , prompt)
                        })
                }
        );
        //Do request and get Response
        String response = webClient.post()
                .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent")
                .header("x-goog-api-key" , geminiApiKey)
                .header("Content-Type" , "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        // Extract response and Return
        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode read = mapper.readTree(response);

            return read.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText(); // it returns the value of the JsonNode value;
        }catch (Exception e){
            return "Error processing request : " + e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest){
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a Professional email reply for the following email content. Please don't generate a subject line");
        if(emailRequest.getTone() != null && emailRequest.getTone().isEmpty()){
            prompt.append("Use a ").append(emailRequest.getTone()).append("tone.");
        }
        prompt.append("\nOriginal Email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}
