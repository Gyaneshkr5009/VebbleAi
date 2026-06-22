package com.server.vebbleAi.service;

import com.server.vebbleAi.dto.ChatRequest;
import com.server.vebbleAi.repository.VebbleAiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VebbleAiService {

    private final VebbleAiRepository vebbleAiRepository;

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

        return vebbleAiRepository.getResponse(requestBody);
    }

}
