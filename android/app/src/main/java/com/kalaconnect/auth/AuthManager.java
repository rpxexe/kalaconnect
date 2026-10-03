package com.kalaconnect.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.kalaconnect.models.AuthResponse;
import com.kalaconnect.models.UserRole;
import com.kalaconnect.utils.Constants;

import java.util.concurrent.CopyOnWriteArrayList;

public class AuthManager {

    private static volatile AuthManager instance;
    private final Context context;
    private final SharedPreferences prefs;
    private final CopyOnWriteArrayList<AuthListener> listeners = new CopyOnWriteArrayList<>();

    public interface AuthListener {
        void onLoginSuccess(UserRole role);
        void onLogout();
        void onTokenExpired();
    }

    private AuthManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public static AuthManager getInstance(Context context) {
        if (instance == null) {
            synchronized (AuthManager.class) {
                if (instance == null) {
                    instance = new AuthManager(context);
                }
            }
        }
        return instance;
    }

    public void addAuthListener(AuthListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeAuthListener(AuthListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    public synchronized void saveAuthSession(String token, Long userId, String name, String email, UserRole role) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_AUTH_TOKEN, token);
        if (userId != null) {
            editor.putLong(Constants.KEY_USER_ID, userId);
        }
        editor.putString(Constants.KEY_USER_NAME, name);
        editor.putString(Constants.KEY_USER_EMAIL, email);
        if (role != null) {
            editor.putString(Constants.KEY_USER_ROLE, role.name());
        }
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.apply();

        for (AuthListener listener : listeners) {
            try {
                listener.onLoginSuccess(role);
            } catch (Exception ignored) {}
        }
    }

    public void saveAuthSession(AuthResponse authResponse) {
        if (authResponse == null) return;
        saveAuthSession(
                authResponse.getToken() != null ? authResponse.getToken() : authResponse.getAccessToken(),
                authResponse.getUserId(),
                authResponse.getName() != null ? authResponse.getName() : authResponse.getFullName(),
                authResponse.getEmail(),
                authResponse.getRole()
        );
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false) && getToken() != null && !getToken().trim().isEmpty();
    }

    public String getToken() {
        return prefs.getString(Constants.KEY_AUTH_TOKEN, null);
    }

    public String getAccessToken() {
        return getToken();
    }

    public Long getUserId() {
        long id = prefs.getLong(Constants.KEY_USER_ID, -1L);
        return id != -1L ? id : null;
    }

    public String getUserName() {
        return prefs.getString(Constants.KEY_USER_NAME, "Artisan Explorer");
    }

    public String getUserEmail() {
        return prefs.getString(Constants.KEY_USER_EMAIL, "");
    }

    public UserRole getUserRole() {
        String roleStr = prefs.getString(Constants.KEY_USER_ROLE, null);
        return UserRole.fromString(roleStr);
    }

    public boolean isOnboardingCompleted() {
        return prefs.getBoolean(Constants.KEY_ONBOARDING_COMPLETED, false);
    }

    public void setOnboardingCompleted(boolean completed) {
        prefs.edit().putBoolean(Constants.KEY_ONBOARDING_COMPLETED, completed).apply();
    }

    public synchronized void logout() {
        boolean onboardingDone = isOnboardingCompleted();
        prefs.edit().clear().apply();
        setOnboardingCompleted(onboardingDone);

        for (AuthListener listener : listeners) {
            try {
                listener.onLogout();
            } catch (Exception ignored) {}
        }
    }

    public synchronized void notifyTokenExpired() {
        boolean onboardingDone = isOnboardingCompleted();
        prefs.edit().clear().apply();
        setOnboardingCompleted(onboardingDone);

        for (AuthListener listener : listeners) {
            try {
                listener.onTokenExpired();
            } catch (Exception ignored) {}
        }
    }
}
