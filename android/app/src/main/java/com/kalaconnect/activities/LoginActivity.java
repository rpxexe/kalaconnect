package com.kalaconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kalaconnect.R;
import com.kalaconnect.auth.AuthManager;
import com.kalaconnect.models.LoginRequest;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private TextView tvSignUpLink;
    private TextView tvErrorBanner;
    private FrameLayout loadingOverlay;

    private AuthViewModel authViewModel;
    private AuthManager authManager;

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(com.kalaconnect.utils.LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authManager = AuthManager.getInstance(this);

        // If already logged in, route immediately to MainActivity
        if (authManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        initViews();
        initViewModel();
        setupListeners();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        etEmail = findViewById(R.id.etEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUpLink = findViewById(R.id.tvSignUpLink);
        tvErrorBanner = findViewById(R.id.tvErrorBanner);
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getLoginResult().observe(this, result -> {
            if (result == null) return;

            switch (result.getStatus()) {
                case LOADING:
                    showLoading(true);
                    hideErrorBanner();
                    break;
                case SUCCESS:
                    showLoading(false);
                    Toast.makeText(this, R.string.welcome_kalaconnect_toast, Toast.LENGTH_SHORT).show();
                    navigateToMain();
                    break;
                case ERROR:
                    showLoading(false);
                    showErrorBanner(result.getMessage());
                    break;
            }
        });
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        tvSignUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        tilEmail.setError(null);
        tilPassword.setError(null);
        hideErrorBanner();

        boolean isValid = true;
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.err_empty_email));
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.err_empty_email));
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.err_empty_password));
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.err_empty_password));
            isValid = false;
        }

        if (isValid) {
            LoginRequest request = new LoginRequest(email, password);
            authViewModel.login(request);
        }
    }

    private void showLoading(boolean isLoading) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (btnLogin != null) {
            btnLogin.setEnabled(!isLoading);
        }
    }

    private void showErrorBanner(String message) {
        if (tvErrorBanner != null) {
            tvErrorBanner.setText(message != null ? message : "Unable to authenticate. Please check your credentials.");
            tvErrorBanner.setVisibility(View.VISIBLE);
        }
    }

    private void hideErrorBanner() {
        if (tvErrorBanner != null) {
            tvErrorBanner.setVisibility(View.GONE);
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
