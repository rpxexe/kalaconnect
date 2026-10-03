package com.kalaconnect.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kalaconnect.auth.AuthManager;
import com.kalaconnect.auth.AuthState;
import com.kalaconnect.models.AuthResponse;
import com.kalaconnect.models.LoginRequest;
import com.kalaconnect.models.RegisterRequest;
import com.kalaconnect.models.UserRole;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final AuthManager authManager;
    private final MutableLiveData<NetworkResult<AuthResponse>> authResultLiveData = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<AuthResponse>> loginResultLiveData = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<AuthResponse>> registerResultLiveData = new MutableLiveData<>();
    private final MutableLiveData<AuthState> authStateLiveData = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository(application);
        this.authManager = AuthManager.getInstance(application);
    }

    public LiveData<NetworkResult<AuthResponse>> getAuthResult() {
        return authResultLiveData;
    }

    public LiveData<NetworkResult<AuthResponse>> getLoginResult() {
        return loginResultLiveData;
    }

    public LiveData<NetworkResult<AuthResponse>> getRegisterResult() {
        return registerResultLiveData;
    }

    public LiveData<AuthState> getAuthState() {
        return authStateLiveData;
    }

    public void checkSession() {
        AuthState state = authRepository.checkAuthState();
        authStateLiveData.setValue(state);
    }

    public void login(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty() || !request.getEmail().contains("@")) {
            NetworkResult<AuthResponse> err = NetworkResult.error("Please enter a valid email address");
            loginResultLiveData.setValue(err);
            authResultLiveData.setValue(err);
            return;
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            NetworkResult<AuthResponse> err = NetworkResult.error("Password must be at least 6 characters");
            loginResultLiveData.setValue(err);
            authResultLiveData.setValue(err);
            return;
        }

        MutableLiveData<NetworkResult<AuthResponse>> repoCallback = new MutableLiveData<NetworkResult<AuthResponse>>() {
            @Override
            public void postValue(NetworkResult<AuthResponse> value) {
                super.postValue(value);
                loginResultLiveData.postValue(value);
                authResultLiveData.postValue(value);
            }
        };

        authRepository.login(request, repoCallback);
    }

    public void login(String email, String password) {
        LoginRequest request = new LoginRequest(email != null ? email.trim() : "", password);
        login(request);
    }

    public void register(RegisterRequest request) {
        String name = request.getName() != null ? request.getName().trim() : "";
        if (name.isEmpty()) {
            NetworkResult<AuthResponse> err = NetworkResult.error("Please enter your full name");
            registerResultLiveData.setValue(err);
            authResultLiveData.setValue(err);
            return;
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty() || !request.getEmail().contains("@")) {
            NetworkResult<AuthResponse> err = NetworkResult.error("Please enter a valid email address");
            registerResultLiveData.setValue(err);
            authResultLiveData.setValue(err);
            return;
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            NetworkResult<AuthResponse> err = NetworkResult.error("Password must be at least 6 characters");
            registerResultLiveData.setValue(err);
            authResultLiveData.setValue(err);
            return;
        }

        // Security check on client
        if (request.getRole() == UserRole.NGO_ADMIN) {
            NetworkResult<AuthResponse> err = NetworkResult.error("Self-registration as NGO_ADMIN is restricted. NGO Administrator accounts must be approved through official institutional verification.");
            registerResultLiveData.setValue(err);
            authResultLiveData.setValue(err);
            return;
        }
        if (request.getRole() == null) {
            NetworkResult<AuthResponse> err = NetworkResult.error("Please select a user role");
            registerResultLiveData.setValue(err);
            authResultLiveData.setValue(err);
            return;
        }

        MutableLiveData<NetworkResult<AuthResponse>> repoCallback = new MutableLiveData<NetworkResult<AuthResponse>>() {
            @Override
            public void postValue(NetworkResult<AuthResponse> value) {
                super.postValue(value);
                registerResultLiveData.postValue(value);
                authResultLiveData.postValue(value);
            }
        };

        authRepository.register(request, repoCallback);
    }

    public UserRole getLoggedInRole() {
        return authManager.getUserRole();
    }

    public String getLoggedInUserName() {
        return authManager.getUserName();
    }

    public String getLoggedInUserEmail() {
        return authManager.getUserEmail();
    }

    public boolean isLoggedIn() {
        return authManager.isLoggedIn();
    }

    public void logout() {
        authRepository.logout();
        authStateLiveData.setValue(AuthState.unauthenticated());
    }

    public void setOnboardingCompleted() {
        authManager.setOnboardingCompleted(true);
    }
}
