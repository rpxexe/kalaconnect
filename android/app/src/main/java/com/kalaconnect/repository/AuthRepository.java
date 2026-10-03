package com.kalaconnect.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.kalaconnect.auth.AuthManager;
import com.kalaconnect.auth.AuthState;
import com.kalaconnect.models.ApiResponse;
import com.kalaconnect.models.AuthResponse;
import com.kalaconnect.models.LoginRequest;
import com.kalaconnect.models.RegisterRequest;
import com.kalaconnect.models.User;
import com.kalaconnect.network.ApiClient;
import com.kalaconnect.network.ApiService;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiService apiService;
    private final AuthManager authManager;

    public AuthRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
        this.authManager = AuthManager.getInstance(context);
    }

    public AuthRepository(ApiService apiService, AuthManager authManager) {
        this.apiService = apiService;
        this.authManager = authManager;
    }

    public void login(LoginRequest request, MutableLiveData<NetworkResult<AuthResponse>> resultLiveData) {
        resultLiveData.postValue(NetworkResult.loading());

        apiService.login(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    AuthResponse authResponse = response.body().getData();
                    authManager.saveAuthSession(authResponse);
                    resultLiveData.postValue(NetworkResult.success(authResponse));
                } else {
                    String errorMsg = ErrorUtils.parseError(response);
                    resultLiveData.postValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                String errorMsg = ErrorUtils.formatNetworkFailure(t);
                resultLiveData.postValue(NetworkResult.error(errorMsg));
            }
        });
    }

    public void register(RegisterRequest request, MutableLiveData<NetworkResult<AuthResponse>> resultLiveData) {
        resultLiveData.postValue(NetworkResult.loading());

        apiService.register(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    AuthResponse authResponse = response.body().getData();
                    authManager.saveAuthSession(authResponse);
                    resultLiveData.postValue(NetworkResult.success(authResponse));
                } else {
                    String errorMsg = ErrorUtils.parseError(response);
                    resultLiveData.postValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                String errorMsg = ErrorUtils.formatNetworkFailure(t);
                resultLiveData.postValue(NetworkResult.error(errorMsg));
            }
        });
    }

    public void getCurrentUser(MutableLiveData<NetworkResult<User>> resultLiveData) {
        resultLiveData.postValue(NetworkResult.loading());

        apiService.getCurrentUser().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    resultLiveData.postValue(NetworkResult.success(response.body().getData()));
                } else {
                    String errorMsg = ErrorUtils.parseError(response);
                    resultLiveData.postValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                String errorMsg = ErrorUtils.formatNetworkFailure(t);
                resultLiveData.postValue(NetworkResult.error(errorMsg));
            }
        });
    }

    public AuthState checkAuthState() {
        if (!authManager.isOnboardingCompleted()) {
            return AuthState.onboardingRequired();
        }
        if (authManager.isLoggedIn()) {
            return AuthState.authenticated(authManager.getUserRole());
        }
        return AuthState.unauthenticated();
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public void logout() {
        authManager.logout();
    }
}
