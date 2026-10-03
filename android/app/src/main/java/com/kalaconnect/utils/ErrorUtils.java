package com.kalaconnect.utils;

import com.google.gson.Gson;
import com.kalaconnect.models.ErrorResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public final class ErrorUtils {

    private static final Gson gson = new Gson();

    private ErrorUtils() {
    }

    public static String parseError(Response<?> response) {
        if (response == null) {
            return "Unknown network error occurred.";
        }

        try {
            ResponseBody errorBody = response.errorBody();
            if (errorBody != null) {
                String errorJson = errorBody.string();
                ErrorResponse errorResponse = gson.fromJson(errorJson, ErrorResponse.class);
                if (errorResponse != null) {
                    if (errorResponse.getValidationErrors() != null && !errorResponse.getValidationErrors().isEmpty()) {
                        return String.join("\n", errorResponse.getValidationErrors());
                    }
                    if (errorResponse.getMessage() != null && !errorResponse.getMessage().trim().isEmpty()) {
                        return errorResponse.getMessage();
                    }
                }
            }
        } catch (Exception ignored) {
        }

        switch (response.code()) {
            case 400:
                return "Invalid request. Please check input fields.";
            case 401:
                return "Invalid email or password. Please verify your credentials.";
            case 403:
                return "Access denied: Registration or action restricted.";
            case 404:
                return "Requested account or service not found.";
            case 500:
            case 502:
            case 503:
                return "KalaConnect server encountered an error. Please try again later.";
            default:
                return "Request failed with status code " + response.code();
        }
    }

    public static String formatNetworkFailure(Throwable t) {
        if (t == null) {
            return "Network connection failed. Please try again.";
        }
        if (t instanceof SocketTimeoutException) {
            return "Connection timed out. Please check your internet connection and try again.";
        }
        if (t instanceof UnknownHostException || t instanceof ConnectException) {
            return "Unable to connect to KalaConnect server. Please check your network.";
        }
        if (t instanceof IOException) {
            return "Network communication error. Please check your network connection.";
        }
        return t.getMessage() != null ? t.getMessage() : "An unexpected error occurred.";
    }

    public static String parseHttpError(Response<?> response) {
        return parseError(response);
    }

    public static String getNetworkErrorMessage(Throwable t) {
        return formatNetworkFailure(t);
    }
}
