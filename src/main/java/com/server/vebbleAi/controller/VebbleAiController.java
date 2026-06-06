package com.server.vebbleAi.controller;

import com.server.vebbleAi.dto.ChatRequest;
import com.server.vebbleAi.service.VebbleAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vebble-ai")
@RequiredArgsConstructor
public class VebbleAiController {
    private final VebbleAiService vebbleAiService;

    @PostMapping("/reply")
    public String generateReply(@RequestBody ChatRequest chatRequest){
        return vebbleAiService.getReply(chatRequest);
    }
}
