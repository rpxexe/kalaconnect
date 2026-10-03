package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AuthResponse implements Serializable {

    @SerializedName(value = "token", alternate = {"accessToken"})
    private String token;

    @SerializedName("tokenType")
    private String tokenType = "Bearer";

    @SerializedName(value = "userId", alternate = {"id"})
    private Long userId;

    @SerializedName(value = "name", alternate = {"fullName"})
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private UserRole role;

    public AuthResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return name;
    }

    public void setFullName(String fullName) {
        this.name = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
