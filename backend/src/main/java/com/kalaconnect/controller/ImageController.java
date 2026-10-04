package com.kalaconnect.controller;

import com.kalaconnect.dto.ApiResponse;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ImageController {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ImageController(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping(value = "/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file,
            jakarta.servlet.http.HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Please provide an image file"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            contentType = "image/jpeg";
        }

        try {
            byte[] bytes = file.getBytes();
            String id = UUID.randomUUID().toString().replace("-", "");

            String sql = "INSERT INTO uploaded_images (id, content_type, data) VALUES (:id, :contentType, :data)";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("contentType", contentType)
                    .addValue("data", bytes);

            jdbcTemplate.update(sql, params);

            String host = request != null ? request.getHeader("Host") : null;
            String scheme = request != null ? request.getScheme() : "http";
            String serverBase = (host != null && !host.isEmpty())
                    ? (scheme + "://" + host)
                    : "http://localhost:8080";

            String relativePath = "/api/images/" + id;
            String imageUrl = serverBase + relativePath;

            Map<String, String> data = new HashMap<>();
            data.put("id", id);
            data.put("url", imageUrl);
            data.put("relativePath", relativePath);

            return new ResponseEntity<>(ApiResponse.success("Image uploaded successfully", data), HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to read image data: " + e.getMessage()));
        }
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
        String sql = "SELECT content_type, data FROM uploaded_images WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);

        try {
            return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                String contentType = rs.getString("content_type");
                byte[] data = rs.getBytes("data");

                MediaType mediaType;
                try {
                    mediaType = MediaType.parseMediaType(contentType);
                } catch (Exception e) {
                    mediaType = MediaType.IMAGE_JPEG;
                }

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
                        .body(data);
            });
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
