package com.kalaconnect.models;

import java.io.Serializable;

public enum UserRole implements Serializable {
    ARTISAN,
    NGO_ADMIN,
    CUSTOMER;

    public static UserRole fromString(String roleStr) {
        if (roleStr == null) {
            return null;
        }
        for (UserRole role : UserRole.values()) {
            if (role.name().equalsIgnoreCase(roleStr.trim())) {
                return role;
            }
        }
        return CUSTOMER;
    }
}
