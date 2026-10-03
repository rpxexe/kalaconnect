package com.kalaconnect.controller;

import com.kalaconnect.dto.ApiResponse;
import com.kalaconnect.dto.AuthResponse;
import com.kalaconnect.dto.LoginRequest;
import com.kalaconnect.dto.RegisterRequest;
import com.kalaconnect.dto.UserResponse;
import com.kalaconnect.exception.BadRequestException;
import com.kalaconnect.exception.UnauthorizedException;
import com.kalaconnect.model.UserRole;
import com.kalaconnect.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    private AuthController authController;

    @BeforeEach
    public void setUp() {
        authController = new AuthController(authService);
    }

    @Test
    public void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setEmail("artisan@kala.org");
        request.setPassword("securePass123");

        AuthResponse authResponse = new AuthResponse("jwt.sample.token", 101L, "Ramesh Kumar", "artisan@kala.org", UserRole.ARTISAN);
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("jwt.sample.token", response.getBody().getData().getAccessToken());
        assertEquals("Ramesh Kumar", response.getBody().getData().getName());
        assertEquals(UserRole.ARTISAN, response.getBody().getData().getRole());
    }

    @Test
    public void testRegisterSuccess_Artisan() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Meera Devi");
        request.setEmail("meera@shg.org");
        request.setPassword("craftPass123");
        request.setRole(UserRole.ARTISAN);

        AuthResponse authResponse = new AuthResponse("jwt.sample.token", 102L, "Meera Devi", "meera@shg.org", UserRole.ARTISAN);
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Meera Devi", response.getBody().getData().getName());
    }

    @Test
    public void testGetCurrentUser_Authenticated() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user@test.org", null, Collections.emptyList())
        );

        UserResponse userResponse = new UserResponse();
        userResponse.setId(103L);
        userResponse.setName("Ananya Roy");
        userResponse.setEmail("user@test.org");
        userResponse.setRole(UserRole.CUSTOMER);

        when(authService.getCurrentUser("user@test.org")).thenReturn(userResponse);

        ResponseEntity<ApiResponse<UserResponse>> response = authController.getCurrentUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ananya Roy", response.getBody().getData().getName());

        SecurityContextHolder.clearContext();
    }
}
