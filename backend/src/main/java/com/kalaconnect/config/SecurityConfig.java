package com.kalaconnect.config;

import com.kalaconnect.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/v1/auth/register").permitAll()
                        .requestMatchers("/api/auth/login", "/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/health/**", "/api/v1/health/**").permitAll()
                        .requestMatchers("/api/ai/**", "/api/v1/ai/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/products").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/products/{id:[0-9]+}").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/artisans").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/artisans/{id:[0-9]+}").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/artisans/skills").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/enquiries").permitAll()
                        .requestMatchers("/api/admin/**", "/api/v1/admin/**").hasRole("NGO_ADMIN")
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
