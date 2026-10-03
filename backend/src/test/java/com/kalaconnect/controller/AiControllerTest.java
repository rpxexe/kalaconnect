package com.kalaconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalaconnect.dto.AiProductContentRequestDto;
import com.kalaconnect.dto.AiProductContentResponseDto;
import com.kalaconnect.exception.ApiException;
import com.kalaconnect.security.JwtAuthenticationFilter;
import com.kalaconnect.security.JwtTokenProvider;
import com.kalaconnect.service.GeminiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeminiService geminiService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGenerateProductContent_Success() throws Exception {
        AiProductContentRequestDto request = new AiProductContentRequestDto(
                "Terracotta Peacock Vase",
                "Natural Clay",
                "Pottery",
                "Bishnupur, Bankura",
                "Hand-etched plumage, kiln-fired finish"
        );

        AiProductContentResponseDto response = new AiProductContentResponseDto(
                "Meticulously shaped from earthen clay, this Terracotta Peacock Vase showcases exquisite handcrafted feathers.",
                "Adorn your space with authentic Bishnupur terracotta heritage. ✨",
                Arrays.asList("#TerracottaArt", "#IndianHandicrafts", "#BishnupurCraft", "#HandmadeInIndia")
        );

        when(geminiService.generateProductContent(any(AiProductContentRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/ai/product-content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(response.getDescription()))
                .andExpect(jsonPath("$.caption").value(response.getCaption()))
                .andExpect(jsonPath("$.hashtags[0]").value("#TerracottaArt"))
                .andExpect(jsonPath("$.hashtags[1]").value("#IndianHandicrafts"));
    }

    @Test
    public void testGenerateProductContent_MissingProductName() throws Exception {
        AiProductContentRequestDto request = new AiProductContentRequestDto(
                "",
                "Natural Clay",
                "Pottery",
                "Bishnupur",
                "Hand-etched"
        );

        mockMvc.perform(post("/api/ai/product-content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    public void testGenerateProductContent_TimeoutHandled() throws Exception {
        AiProductContentRequestDto request = new AiProductContentRequestDto(
                "Madhubani Painting",
                "Handmade Paper",
                "Painting",
                "Madhubani, Bihar",
                "Fish and lotus motifs"
        );

        when(geminiService.generateProductContent(any(AiProductContentRequestDto.class)))
                .thenThrow(new ApiException("Gemini AI service timed out while generating content. Please retry.", HttpStatus.GATEWAY_TIMEOUT));

        mockMvc.perform(post("/api/ai/product-content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isGatewayTimeout())
                .andExpect(jsonPath("$.status").value(504))
                .andExpect(jsonPath("$.message").value("Gemini AI service timed out while generating content. Please retry."));
    }

    @Test
    public void testGenerateProductContent_RateLimitHandled() throws Exception {
        AiProductContentRequestDto request = new AiProductContentRequestDto(
                "Madhubani Painting",
                "Handmade Paper",
                "Painting",
                "Madhubani, Bihar",
                "Fish and lotus motifs"
        );

        when(geminiService.generateProductContent(any(AiProductContentRequestDto.class)))
                .thenThrow(new ApiException("Gemini AI rate limit reached. Please wait a moment and retry.", HttpStatus.TOO_MANY_REQUESTS));

        mockMvc.perform(post("/api/ai/product-content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.message").value("Gemini AI rate limit reached. Please wait a moment and retry."));
    }
}
