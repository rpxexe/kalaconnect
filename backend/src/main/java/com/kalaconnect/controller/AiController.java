package com.kalaconnect.controller;

import com.kalaconnect.dto.AiProductContentRequestDto;
import com.kalaconnect.dto.AiProductContentResponseDto;
import com.kalaconnect.service.GeminiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class AiController {

    private final GeminiService geminiService;

    public AiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping({"/api/ai/product-content", "/api/v1/ai/product-content"})
    public ResponseEntity<AiProductContentResponseDto> generateProductContent(
            @Valid @RequestBody AiProductContentRequestDto request) {
        AiProductContentResponseDto response = geminiService.generateProductContent(request);
        return ResponseEntity.ok(response);
    }
}
