package com.kalaconnect.service;

import com.kalaconnect.dto.AuthResponse;
import com.kalaconnect.dto.LoginRequest;
import com.kalaconnect.dto.RegisterRequest;
import com.kalaconnect.dto.UserResponse;
import com.kalaconnect.exception.BadRequestException;
import com.kalaconnect.exception.ResourceNotFoundException;
import com.kalaconnect.exception.UnauthorizedException;
import com.kalaconnect.model.ArtisanProfile;
import com.kalaconnect.model.User;
import com.kalaconnect.model.UserRole;
import com.kalaconnect.repository.UserRepository;
import com.kalaconnect.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email address is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        User user = userRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail() != null ? request.getEmail().toLowerCase().trim() : "";
        if (email.isBlank()) {
            throw new BadRequestException("Email is required");
        }

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("An account with this email already exists: " + email);
        }

        String name = request.getName();
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Full name is required");
        }

        // Security check: Never allow arbitrary users to self-register as NGO_ADMIN
        if (request.getRole() == UserRole.NGO_ADMIN) {
            throw new BadRequestException("Self-registration as NGO_ADMIN is strictly restricted. NGO Administrator accounts must be provisioned and approved through institutional verification.");
        }

        if (request.getRole() == null) {
            throw new BadRequestException("User role must be specified (ARTISAN or CUSTOMER)");
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(email);
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setLanguage("en");
        user.setVerified(false);

        User savedUser = userRepository.save(user);

        // If registering as Artisan, initialize artisan profile
        if (savedUser.getRole() == UserRole.ARTISAN) {
            ArtisanProfile artisanProfile = new ArtisanProfile();
            artisanProfile.setUserId(savedUser.getId());
            artisanProfile.setArtisanName(savedUser.getName());
            artisanProfile.setShgName(request.getShgName() != null ? request.getShgName() : "");
            artisanProfile.setCraftType(request.getCraftType() != null ? request.getCraftType() : "Handicraft");
            artisanProfile.setApprovalStatus("PENDING");
            userRepository.createArtisanProfile(artisanProfile);
        }

        String token = jwtTokenProvider.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole());
        return new AuthResponse(token, savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole());
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return UserResponse.fromEntity(user);
    }

    public UserResponse getCurrentUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return UserResponse.fromEntity(user);
    }
}
