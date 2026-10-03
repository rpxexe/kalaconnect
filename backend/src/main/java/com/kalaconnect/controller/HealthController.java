package com.kalaconnect.controller;

import com.kalaconnect.service.DatabaseMigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    private final JdbcTemplate jdbcTemplate;
    private final DatabaseMigrationService migrationService;

    public HealthController(JdbcTemplate jdbcTemplate, DatabaseMigrationService migrationService) {
        this.jdbcTemplate = jdbcTemplate;
        this.migrationService = migrationService;
    }

    @GetMapping(value = {"/api/health", "/api/v1/health"})
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("status", "UP");
        status.put("service", "KalaConnect Backend");
        status.put("version", "1.0.0");
        return ResponseEntity.ok(status);
    }

    /**
     * Database connectivity verification endpoint.
     * Verifies that the Spring Boot backend can successfully communicate with Aiven PostgreSQL.
     */
    @GetMapping(value = {"/api/health/db", "/api/v1/health/db"})
    public ResponseEntity<Map<String, String>> checkDatabaseHealth() {
        try {
            Integer probeResult = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (probeResult != null && probeResult == 1) {
                // Ensure migration schema is verified
                migrationService.applyMigrations();

                Map<String, String> response = new LinkedHashMap<>();
                response.put("status", "ok");
                response.put("database", "connected");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            logger.warn("Database connectivity health check failed: {}", e.getClass().getSimpleName());
        }

        Map<String, String> errorResponse = new LinkedHashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("database", "disconnected");
        errorResponse.put("message", "Unable to establish connection to PostgreSQL database. Please verify DB_URL, DB_USERNAME, and DB_PASSWORD environment variables.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
}
