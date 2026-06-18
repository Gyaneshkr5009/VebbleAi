package com.server.vebbleAi.service.translator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.vebbleAi.dto.TransRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class TranslatorService {

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper(); // Reuse instance for performance

    public TranslatorService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.build();
    }

    @Value("${sarvam.api.url}")
    private String sarvamApiUrl;

    @Value("${sarvam.api.key}")
    private String sarvamApiKey;

    public String getTranslated(TransRequest transRequest){
        String prompt = buildPrompt(transRequest);

        Map<String, Object> requestBody = Map.of(
                "model", "sarvam-105b",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        try {
            String response = webClient.post()
                    .uri(sarvamApiUrl)
                    .header("api-subscription-key", sarvamApiKey) // Verified header
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractResponseContent(response);

        } catch (WebClientResponseException e) {
            return "API Error (" + e.getStatusCode() + "): " + e.getResponseBodyAsString();
        } catch (Exception e) {
            return "Connection or internal error: " + e.getMessage();
        }
    }

    private String extractResponseContent(String response) {
        if (response == null || response.isBlank()) {
            return "Error: Received empty response payload from server.";
        }
        try {
            JsonNode read = mapper.readTree(response);
            JsonNode choices = read.path("choices");

            if (choices.isArray() && !choices.isEmpty()) {
                return choices.get(0)
                        .path("message")
                        .path("content")
                        .asText("Error: 'content' field is missing.");
            }

            return "Error: Path 'choices' not found or empty in JSON structure. Raw: " + response;

        } catch (Exception e) {
            return "Error parsing JSON payload: " + e.getMessage();
        }
    }

    private String buildPrompt(TransRequest transRequest){
        StringBuilder prompt = new StringBuilder();
        prompt.append("Translate the following text into a natural ");
        if(transRequest.getTone() != null && !transRequest.getTone().isEmpty()){
            prompt.append(transRequest.getTone());
        } else {
            prompt.append("human-friendly");
        }
        prompt.append(" style while keeping the original meaning unchanged. ");
        prompt.append("Return only the translated text.\n\n");
        prompt.append("TEXT:\n");
        prompt.append(transRequest.getContent());
        return prompt.toString();
    }
}
