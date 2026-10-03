package com.kalaconnect.service;

import com.kalaconnect.dto.AuthResponse;
import com.kalaconnect.dto.LoginRequest;
import com.kalaconnect.dto.RegisterRequest;
import com.kalaconnect.exception.BadRequestException;
import com.kalaconnect.exception.UnauthorizedException;
import com.kalaconnect.model.User;
import com.kalaconnect.model.UserRole;
import com.kalaconnect.repository.UserRepository;
import com.kalaconnect.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private AuthService authService;

    @BeforeEach
    public void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtTokenProvider);
    }

    @Test
    public void testRegister_RejectsNgoAdminSelfRegistration() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Fraudulent Admin");
        request.setEmail("hacker@ngo.org");
        request.setPassword("adminPass123");
        request.setRole(UserRole.NGO_ADMIN);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            authService.register(request);
        });

        assertTrue(ex.getMessage().contains("NGO_ADMIN is strictly restricted"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegister_AllowsCustomerRegistration() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Rahul Sharma");
        request.setEmail("rahul@buyer.org");
        request.setPassword("customerPass123");
        request.setRole(UserRole.CUSTOMER);

        when(userRepository.existsByEmail("rahul@buyer.org")).thenReturn(false);
        when(passwordEncoder.encode("customerPass123")).thenReturn("hashedPassword");

        User savedUser = new User();
        savedUser.setId(201L);
        savedUser.setName("Rahul Sharma");
        savedUser.setEmail("rahul@buyer.org");
        savedUser.setRole(UserRole.CUSTOMER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtTokenProvider.generateToken(eq(201L), eq("rahul@buyer.org"), eq(UserRole.CUSTOMER)))
                .thenReturn("token_customer_jwt");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("token_customer_jwt", response.getAccessToken());
        assertEquals("Rahul Sharma", response.getName());
        assertEquals(UserRole.CUSTOMER, response.getRole());
    }

    @Test
    public void testLogin_InvalidPassword_ThrowsUnauthorized() {
        LoginRequest request = new LoginRequest();
        request.setEmail("artisan@test.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setId(202L);
        user.setEmail("artisan@test.com");
        user.setPasswordHash("hashedActualPassword");

        when(userRepository.findByEmail("artisan@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedActualPassword")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> {
            authService.login(request);
        });
    }
}
