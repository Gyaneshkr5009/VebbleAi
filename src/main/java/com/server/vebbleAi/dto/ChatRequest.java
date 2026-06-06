package com.server.vebbleAi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRequest {
    String content;
    String tone;
}
