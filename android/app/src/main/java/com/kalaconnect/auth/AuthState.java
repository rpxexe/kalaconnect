package com.kalaconnect.auth;

import com.kalaconnect.models.UserRole;

public class AuthState {

    public enum Type {
        AUTHENTICATED,
        UNAUTHENTICATED,
        ONBOARDING_REQUIRED
    }

    private final Type type;
    private final UserRole userRole;

    public AuthState(Type type, UserRole userRole) {
        this.type = type;
        this.userRole = userRole;
    }

    public static AuthState authenticated(UserRole role) {
        return new AuthState(Type.AUTHENTICATED, role);
    }

    public static AuthState unauthenticated() {
        return new AuthState(Type.UNAUTHENTICATED, null);
    }

    public static AuthState onboardingRequired() {
        return new AuthState(Type.ONBOARDING_REQUIRED, null);
    }

    public Type getType() {
        return type;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public boolean isAuthenticated() {
        return type == Type.AUTHENTICATED;
    }

    public boolean isOnboardingRequired() {
        return type == Type.ONBOARDING_REQUIRED;
    }
}
