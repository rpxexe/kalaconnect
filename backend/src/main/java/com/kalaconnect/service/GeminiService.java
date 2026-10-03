package com.kalaconnect.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalaconnect.dto.AiProductContentRequestDto;
import com.kalaconnect.dto.AiProductContentResponseDto;
import com.kalaconnect.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model:gemini-3.8-flash}")
    private String model;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String baseUrl;

    @Value("${gemini.timeout-seconds:25}")
    private int timeoutSeconds;

    public GeminiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000); // 10s connect timeout
        requestFactory.setReadTimeout(25000);    // 25s read timeout
        this.restTemplate = new RestTemplate(requestFactory);
    }

    // // Constructor for testing / dependency injection
    // public GeminiService(RestTemplate restTemplate, ObjectMapper objectMapper, String apiKey, String model) {
    //     this.restTemplate = restTemplate;
    //     this.objectMapper = objectMapper;
    //     this.apiKey = apiKey;
    //     this.model = model;
    //     this.baseUrl = "https://generativelanguage.googleapis.com/v1beta/models";
    //     this.timeoutSeconds = 25;
    // }

    public AiProductContentResponseDto generateProductContent(AiProductContentRequestDto request) {
        if (request == null || !StringUtils.hasText(request.getProductName())) {
            throw new ApiException("Product name is required for AI content generation", HttpStatus.BAD_REQUEST);
        }

        // If no API key is configured on the backend, check if we should fall back gracefully or report missing key
        if (!StringUtils.hasText(apiKey)) {
            log.warn("GEMINI_API_KEY is not configured on the backend. Returning realistic handcrafted content fallback.");
            return generateArtisanFallbackContent(request);
        }

        String prompt = buildPrompt(request);
        Map<String, Object> requestPayload = buildGeminiRequestBody(prompt);

        String endpointUrl = String.format("%s/%s:generateContent?key=%s", baseUrl, model, apiKey.trim());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestPayload, headers);

        int maxRetries = 2; // total attempts: 3
        long backoffDelayMs = 1200;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        endpointUrl,
                        HttpMethod.POST,
                        entity,
                        String.class
                );

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return parseGeminiResponse(response.getBody(), request);
                } else {
                    throw new ApiException("Gemini AI service returned status: " + response.getStatusCode(),
                            (HttpStatus) response.getStatusCode());
                }

            } catch (HttpClientErrorException.TooManyRequests e) {
                log.warn("Gemini API rate limit exceeded (HTTP 429). Attempt {}/{}", attempt + 1, maxRetries + 1);
                if (attempt < maxRetries) {
                    sleep(backoffDelayMs);
                    backoffDelayMs *= 2;
                    continue;
                }
                throw new ApiException("Gemini AI rate limit reached. Please wait a moment and retry.", HttpStatus.TOO_MANY_REQUESTS);

            } catch (ResourceAccessException e) {
                // Connection or Socket timeout
                log.warn("Gemini API connection/read timeout. Attempt {}/{}: {}", attempt + 1, maxRetries + 1, e.getMessage());
                if (attempt < maxRetries) {
                    sleep(backoffDelayMs);
                    backoffDelayMs *= 2;
                    continue;
                }
                throw new ApiException("Gemini AI service timed out while generating content. Please retry.", HttpStatus.GATEWAY_TIMEOUT);

            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    log.warn("Gemini API rate limit (429). Attempt {}/{}", attempt + 1, maxRetries + 1);
                    if (attempt < maxRetries) {
                        sleep(backoffDelayMs);
                        backoffDelayMs *= 2;
                        continue;
                    }
                    throw new ApiException("Gemini AI rate limit reached. Please wait a moment and retry.", HttpStatus.TOO_MANY_REQUESTS);
                }

                log.error("Gemini API error status {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
                throw new ApiException("Gemini AI error (" + e.getStatusCode().value() + "): " + extractErrorMessage(e.getResponseBodyAsString()),
                        (HttpStatus) e.getStatusCode());

            } catch (ApiException e) {
                throw e;

            } catch (Exception e) {
                log.error("Unexpected error invoking Gemini API: {}", e.getMessage(), e);
                if (attempt < maxRetries) {
                    sleep(backoffDelayMs);
                    continue;
                }
                throw new ApiException("Failed to generate product content: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        throw new ApiException("Failed to generate product content after retries", HttpStatus.GATEWAY_TIMEOUT);
    }

    private String buildPrompt(AiProductContentRequestDto request) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a professional Indian handicraft cultural storyteller and e-commerce marketing specialist for KalaConnect, ");
        sb.append("a platform empowering rural Self-Help Groups (SHGs) and traditional master artisans.\n\n");
        sb.append("Generate compelling, culturally evocative, and authentic promotional marketing content for the following handcrafted creation:\n");
        sb.append("- Product Name: ").append(request.getProductName()).append("\n");
        if (StringUtils.hasText(request.getMaterial())) {
            sb.append("- Material: ").append(request.getMaterial()).append("\n");
        }
        if (StringUtils.hasText(request.getCraftType())) {
            sb.append("- Craft Type / Technique: ").append(request.getCraftType()).append("\n");
        }
        if (StringUtils.hasText(request.getLocation())) {
            sb.append("- Artisan Origin / Location: ").append(request.getLocation()).append("\n");
        }
        if (StringUtils.hasText(request.getFeatures())) {
            sb.append("- Special Features / Motifs: ").append(request.getFeatures()).append("\n");
        }

        sb.append("\nRequirements:\n");
        sb.append("1. 'description': An evocative, authentic 2 to 3 paragraph narrative highlighting the artisan heritage, craftsmanship, tactile beauty, and aesthetic value. Avoid hyperbole; celebrate genuine craftsmanship.\n");
        sb.append("2. 'caption': A catchy, engaging social media promotional caption (for Instagram and WhatsApp catalogs) inviting conscious buyers to appreciate this craft.\n");
        sb.append("3. 'hashtags': An array of 5 to 8 relevant, popular craft hashtags (each starting with '#', e.g. #HandmadeInIndia, #VocalForLocal, #IndianHandicrafts, craft-specific tags).\n\n");
        sb.append("Respond STRICTLY with a valid JSON object matching this schema without any markdown formatting or commentary:\n");
        sb.append("{\n");
        sb.append("  \"description\": \"string\",\n");
        sb.append("  \"caption\": \"string\",\n");
        sb.append("  \"hashtags\": [\"string\"]\n");
        sb.append("}\n");

        return sb.toString();
    }

    private Map<String, Object> buildGeminiRequestBody(String prompt) {
        Map<String, Object> payload = new HashMap<>();

        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(part));

        payload.put("contents", Collections.singletonList(content));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("responseMimeType", "application/json");
        generationConfig.put("temperature", 0.7);

        payload.put("generationConfig", generationConfig);
        return payload;
    }

    private AiProductContentResponseDto parseGeminiResponse(String responseJson, AiProductContentRequestDto request) {
        if (!StringUtils.hasText(responseJson)) {
            throw new ApiException("Gemini AI returned an empty response. Please retry.", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode candidates = root.path("candidates");

            if (!candidates.isArray() || candidates.isEmpty()) {
                JsonNode promptFeedback = root.path("promptFeedback");
                if (promptFeedback.has("blockReason")) {
                    throw new ApiException("AI content generation blocked: " + promptFeedback.path("blockReason").asText(),
                            HttpStatus.BAD_REQUEST);
                }
                throw new ApiException("Gemini AI returned an empty response (no candidates). Please adjust product details and retry.",
                        HttpStatus.UNPROCESSABLE_ENTITY);
            }

            JsonNode candidate = candidates.get(0);
            JsonNode parts = candidate.path("content").path("parts");

            if (!parts.isArray() || parts.isEmpty()) {
                throw new ApiException("Gemini AI returned an empty response part. Please retry.", HttpStatus.UNPROCESSABLE_ENTITY);
            }

            String text = parts.get(0).path("text").asText();
            if (!StringUtils.hasText(text)) {
                throw new ApiException("Gemini AI generated empty text content. Please retry.", HttpStatus.UNPROCESSABLE_ENTITY);
            }

            // Strip possible markdown fences
            String cleanedJson = cleanJsonText(text);

            JsonNode contentNode = objectMapper.readTree(cleanedJson);
            String description = contentNode.path("description").asText("");
            String caption = contentNode.path("caption").asText("");

            List<String> hashtags = new ArrayList<>();
            JsonNode hashtagsNode = contentNode.path("hashtags");
            if (hashtagsNode.isArray()) {
                for (JsonNode tag : hashtagsNode) {
                    String tagText = tag.asText("").trim();
                    if (!tagText.isEmpty()) {
                        if (!tagText.startsWith("#")) {
                            tagText = "#" + tagText;
                        }
                        hashtags.add(tagText);
                    }
                }
            }

            if (!StringUtils.hasText(description) && !StringUtils.hasText(caption)) {
                throw new ApiException("AI response was missing required description and caption. Please retry.",
                        HttpStatus.UNPROCESSABLE_ENTITY);
            }

            if (hashtags.isEmpty()) {
                hashtags = generateDefaultHashtags(request);
            }

            return new AiProductContentResponseDto(description, caption, hashtags);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse Gemini JSON payload: {}", e.getMessage());
            throw new ApiException("Failed to parse AI-generated structured content. Please retry.", HttpStatus.BAD_GATEWAY);
        }
    }

    private String cleanJsonText(String text) {
        String trimmed = text.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.trim();
    }

    private String extractErrorMessage(String body) {
        if (!StringUtils.hasText(body)) return "Unknown service error";
        try {
            JsonNode root = objectMapper.readTree(body);
            if (root.has("error") && root.get("error").has("message")) {
                return root.get("error").get("message").asText();
            }
        } catch (Exception ignored) {}
        return body.length() > 150 ? body.substring(0, 150) + "..." : body;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private AiProductContentResponseDto generateArtisanFallbackContent(AiProductContentRequestDto req) {
        String craft = StringUtils.hasText(req.getCraftType()) ? req.getCraftType() : "traditional handcrafted";
        String material = StringUtils.hasText(req.getMaterial()) ? req.getMaterial() : "natural artisan materials";
        String location = StringUtils.hasText(req.getLocation()) ? req.getLocation() : "rural craft clusters of India";
        String name = req.getProductName();
        String features = StringUtils.hasText(req.getFeatures()) ? req.getFeatures() : "intricate indigenous motifs";

        String description = String.format(
                "Handcrafted with devotion and heritage techniques, the %s exemplifies timeless artisanal mastery. Sculpted using authentic %s by skilled collective artisans in %s, each contour reflects generations of cultural knowledge.\n\n" +
                "Featuring %s, this piece carries the distinctive touch of the maker's hands. Designed to bring warmth, heritage, and purposeful aesthetics into your living space, it represents the sustainable spirit of Indian craft collectives.",
                name, material, location, features
        );

        String caption = String.format(
                "Celebrate authentic artistry with this handcrafted %s. Masterfully created using %s by skilled artisans in %s. Bring home a piece of living heritage! ✨",
                name, material, location
        );

        List<String> hashtags = generateDefaultHashtags(req);

        return new AiProductContentResponseDto(description, caption, hashtags);
    }

    private List<String> generateDefaultHashtags(AiProductContentRequestDto req) {
        List<String> tags = new ArrayList<>();
        tags.add("#HandmadeInIndia");
        tags.add("#VocalForLocal");
        tags.add("#IndianHandicrafts");
        tags.add("#ArtisanMade");

        if (StringUtils.hasText(req.getCraftType())) {
            tags.add("#" + req.getCraftType().replaceAll("\\s+", ""));
        }
        if (StringUtils.hasText(req.getMaterial())) {
            tags.add("#" + req.getMaterial().replaceAll("\\s+", ""));
        }
        tags.add("#KalaConnect");
        return tags;
    }
}
