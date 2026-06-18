package com.server.vebbleAi.controller.translate;

import com.server.vebbleAi.dto.TransRequest;
import com.server.vebbleAi.service.translator.TranslatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/translate")
@CrossOrigin("*")
@RequiredArgsConstructor
public class Translator {
    private final TranslatorService translatorService;

    @PostMapping
    public ResponseEntity<String> translateContent(@RequestBody TransRequest transRequest){
        return ResponseEntity.ok(translatorService.getTranslated(transRequest));
    }
}
