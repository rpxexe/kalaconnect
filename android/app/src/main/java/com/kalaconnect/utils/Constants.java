package com.kalaconnect.utils;

public final class Constants {

    private Constants() {
    }

    // 10.0.2.2 points to local machine host from Android emulator.
    // For physical device, change to host machine IP or remote deployed API.
    // public static final String BASE_URL = "http://192.168.0.114:8080/";
public static final String BASE_URL = "https://kalaconnect-eptc.onrender.com/";
    public static final String PREF_NAME = "kalaconnect_prefs";
    public static final String KEY_AUTH_TOKEN = "key_auth_token";
    public static final String KEY_USER_ID = "key_user_id";
    public static final String KEY_USER_EMAIL = "key_user_email";
    public static final String KEY_USER_NAME = "key_user_name";
    public static final String KEY_USER_ROLE = "key_user_role";
    public static final String KEY_IS_LOGGED_IN = "key_is_logged_in";
    public static final String KEY_ONBOARDING_COMPLETED = "key_onboarding_completed";

    public static final long NETWORK_TIMEOUT_SECONDS = 30L;
}
