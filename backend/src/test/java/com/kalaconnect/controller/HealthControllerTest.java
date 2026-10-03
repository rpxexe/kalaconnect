package com.kalaconnect.controller;

import com.kalaconnect.service.DatabaseMigrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HealthControllerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private DatabaseMigrationService migrationService;

    private HealthController healthController;

    @BeforeEach
    public void setUp() {
        healthController = new HealthController(jdbcTemplate, migrationService);
    }

    @Test
    public void testDatabaseHealthSuccess() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);

        ResponseEntity<Map<String, String>> response = healthController.checkDatabaseHealth();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ok", response.getBody().get("status"));
        assertEquals("connected", response.getBody().get("database"));
    }

    @Test
    public void testDatabaseHealthFailure_DoesNotExposeCredentials() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenThrow(new DataAccessResourceFailureException("Connection refused to private-db-host:5432 with secret_pass"));

        ResponseEntity<Map<String, String>> response = healthController.checkDatabaseHealth();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("disconnected", response.getBody().get("database"));
        assertEquals("Unable to establish connection to PostgreSQL database. Please verify DB_URL, DB_USERNAME, and DB_PASSWORD environment variables.", response.getBody().get("message"));
    }
}
