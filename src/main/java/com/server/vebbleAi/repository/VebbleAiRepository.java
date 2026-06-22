package com.server.vebbleAi.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Repository
public class VebbleAiRepository {
    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key1}")
    private String geminiApiKey1;

    @Value("${gemini.api.key2}")
    private String geminiApiKey2;

    @Value("${gemini.api.key3}")
    private String geminiApiKey3;

    VebbleAiRepository(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.build();
    }

    public String getResponse(Map<String , Object> requestBody){
        List<String> keys = Stream.of(geminiApiKey1, geminiApiKey2, geminiApiKey3)
                .filter(Objects::nonNull)
                .filter(key -> !key.isBlank())
                .toList();
        if (keys.isEmpty()) {
            return "Error Processing Request: No valid Vebble API keys configured.";
        }

        for (String api : keys){
            try{
                String response = webClient.post()
                        .uri(geminiApiUrl)
                        .header("x-goog-api-key" , api)
                        .header("Content-Type" , "application/json")
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                return extractResponseContent(response);
            }catch (WebClientResponseException.TooManyRequests e) {
                System.err.println("Gemini Key rate limited (429). Trying next key...");
            } catch (Exception e) {
                System.err.println("Gemini API call failed: " + e.getMessage() + ". Trying next key...");
            }
        }
        return "Error Processing Request: All api options exhausted.";
    }

    private String extractResponseContent(String response) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode reader = mapper.readTree(response);

            return reader.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        }catch (Exception e){
            return "Error Processing Request :" + e.getMessage();
        }
    }
}
