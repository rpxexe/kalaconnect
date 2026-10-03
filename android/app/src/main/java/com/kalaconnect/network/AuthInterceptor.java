package com.kalaconnect.network;

import com.kalaconnect.auth.AuthManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final AuthManager authManager;

    public AuthInterceptor(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder()
                .header("Accept", "application/json");

        // Automatically attach JWT token if user is authenticated
        String token = authManager.getToken();
        if (token != null && !token.trim().isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + token.trim());
        }

        Response response = chain.proceed(requestBuilder.build());

        // Handle expired token or invalidated credentials on authenticated requests
        if (response.code() == 401 && authManager.isLoggedIn()) {
            authManager.notifyTokenExpired();
        }

        return response;
    }
}
