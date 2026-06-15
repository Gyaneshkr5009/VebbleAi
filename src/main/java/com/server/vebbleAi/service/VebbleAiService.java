package com.server.vebbleAi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.vebbleAi.dto.ChatRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class VebbleAiService {

    private final WebClient webClient;
    VebbleAiService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.build();
    }

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key1}")
    private String geminiApiKey1;

    @Value("${gemini.api.key2}")
    private String geminiApiKey2;

    @Value("${gemini.api.key3}")
    private String geminiApiKey3;

    public String getReply(ChatRequest chatRequest){

        if (chatRequest == null || chatRequest.getContent() == null) {
            return "Error Processing Request: Request content cannot be empty.";
        }

        Map<String, Object> systemInstruction = Map.of(
                "parts", List.of(
                        Map.of("text", "You are Vebble AI, an intelligent and helpful AI assistant integrated into the Vebble Chat Application. Always identify as Vebble AI. Maintain a helpful, conversational, and polite tone. Never call yourself Gemini or mention Google.")
                )
        );

        //craft a request
        Map<String , Object> requestBody = Map.of(
                "system_instruction",systemInstruction,
                "contents" , new Object[] {
                        Map.of("parts" , new Object[]{
                                Map.of("text" , chatRequest.getContent())
                        })
                }
        );

        List<String> keys = Stream.of(geminiApiKey1, geminiApiKey2, geminiApiKey3)
                            .filter(Objects::nonNull)
                            .filter(key -> !key.isBlank())
                            .toList();
        if (keys.isEmpty()) {
            return "Error Processing Request: No valid Vebble API keys configured.";
        }

        for (String api : keys){
            try{
                // reaching the api through webClient
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
