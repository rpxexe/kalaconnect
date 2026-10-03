package com.kalaconnect.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.kalaconnect.models.AuthResponse;
import com.kalaconnect.models.UserRole;

public class SessionManager {

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveAuthSession(AuthResponse authResponse) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_AUTH_TOKEN, authResponse.getToken());
        if (authResponse.getUserId() != null) {
            editor.putLong(Constants.KEY_USER_ID, authResponse.getUserId());
        }
        editor.putString(Constants.KEY_USER_EMAIL, authResponse.getEmail());
        editor.putString(Constants.KEY_USER_NAME, authResponse.getFullName());
        if (authResponse.getRole() != null) {
            editor.putString(Constants.KEY_USER_ROLE, authResponse.getRole().name());
        }
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false) && getAuthToken() != null;
    }

    public String getAuthToken() {
        return prefs.getString(Constants.KEY_AUTH_TOKEN, null);
    }

    public UserRole getUserRole() {
        String roleStr = prefs.getString(Constants.KEY_USER_ROLE, null);
        return UserRole.fromString(roleStr);
    }

    public String getUserName() {
        return prefs.getString(Constants.KEY_USER_NAME, "User");
    }

    public String getUserEmail() {
        return prefs.getString(Constants.KEY_USER_EMAIL, "");
    }

    public boolean isOnboardingCompleted() {
        return prefs.getBoolean(Constants.KEY_ONBOARDING_COMPLETED, false);
    }

    public void setOnboardingCompleted(boolean completed) {
        prefs.edit().putBoolean(Constants.KEY_ONBOARDING_COMPLETED, completed).apply();
    }

    public void clearSession() {
        boolean onboardingDone = isOnboardingCompleted();
        prefs.edit().clear().apply();
        // Retain onboarding completion flag so returning users don't see intro repeatedly
        setOnboardingCompleted(onboardingDone);
    }
}
