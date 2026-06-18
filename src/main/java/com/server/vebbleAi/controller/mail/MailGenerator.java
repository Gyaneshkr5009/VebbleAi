package com.server.vebbleAi.controller.mail;

import com.server.vebbleAi.dto.EmailRequest;
import com.server.vebbleAi.service.mail.MailGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/email")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MailGenerator {

    private final MailGeneratorService mailGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest){
        String response = mailGeneratorService.generateEmailReply(emailRequest);
        return ResponseEntity.ok(response);
    }
}

