package com.server.vebbleAi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.vebbleAi.dto.ChatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class VebbleAiService {

    private final WebClient webClient;
    VebbleAiService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.build();
    }

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public String getReply(ChatRequest chatRequest){

        if (chatRequest == null || chatRequest.getContent() == null) {
            return "Error Processing Request: Request content cannot be empty.";
        }
        //craft a request
        Map<String , Object> requestBody = Map.of(
                "contents" , new Object[] {
                        Map.of("parts" , new Object[]{
                                Map.of("text" , chatRequest.getContent())
                        })
                }
        );

        // reaching the api through webClient
        String response = webClient.post()
                .uri(geminiApiUrl)
                .header("x-goog-api-key" , geminiApiKey)
                .header("Content-Type" , "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(reactor.util.retry.Retry.backoff(2, java.time.Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof org.springframework.web.reactive.function.client.WebClientResponseException.TooManyRequests)
                )
                .block();

        return extractResponseContent(response);
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
