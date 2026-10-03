package com.kalaconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kalaconnect.R;
import com.kalaconnect.auth.AuthManager;
import com.kalaconnect.models.RegisterRequest;
import com.kalaconnect.models.UserRole;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.AuthViewModel;

public class SignupActivity extends AppCompatActivity {

    private TextInputLayout tilFullName;
    private TextInputEditText etFullName;
    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private TextInputLayout tilPhone;
    private TextInputEditText etPhone;
    private TextInputLayout tilPassword;
    private TextInputEditText etPassword;
    private TextInputLayout tilShgName;
    private TextInputEditText etShgName;
    private TextInputLayout tilCraftType;
    private TextInputEditText etCraftType;

    private RadioGroup rgRoleSelector;
    private RadioButton rbArtisan;
    private RadioButton rbCustomer;
    private LinearLayout layoutArtisanFields;

    private MaterialButton btnSignUp;
    private TextView tvLoginLink;
    private TextView tvErrorBanner;
    private FrameLayout loadingOverlay;

    private AuthViewModel authViewModel;
    private UserRole selectedRole = UserRole.ARTISAN;

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(com.kalaconnect.utils.LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Check if role passed from previous screen
        if (getIntent() != null && getIntent().hasExtra("selectedRole")) {
            UserRole roleExtra = (UserRole) getIntent().getSerializableExtra("selectedRole");
            if (roleExtra != null) {
                selectedRole = roleExtra;
            }
        }

        initViews();
        initViewModel();
        setupListeners();
        updateRoleSelection(selectedRole);
    }

    private void initViews() {
        tilFullName = findViewById(R.id.tilFullName);
        etFullName = findViewById(R.id.etFullName);
        tilEmail = findViewById(R.id.tilEmail);
        etEmail = findViewById(R.id.etEmail);
        tilPhone = findViewById(R.id.tilPhone);
        etPhone = findViewById(R.id.etPhone);
        tilPassword = findViewById(R.id.tilPassword);
        etPassword = findViewById(R.id.etPassword);
        tilShgName = findViewById(R.id.tilShgName);
        etShgName = findViewById(R.id.etShgName);
        tilCraftType = findViewById(R.id.tilCraftType);
        etCraftType = findViewById(R.id.etCraftType);

        rgRoleSelector = findViewById(R.id.rgRoleSelector);
        rbArtisan = findViewById(R.id.rbArtisan);
        rbCustomer = findViewById(R.id.rbCustomer);
        layoutArtisanFields = findViewById(R.id.layoutArtisanFields);

        btnSignUp = findViewById(R.id.btnSignUp);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        tvErrorBanner = findViewById(R.id.tvErrorBanner);
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getRegisterResult().observe(this, result -> {
            if (result == null) return;

            switch (result.getStatus()) {
                case LOADING:
                    showLoading(true);
                    hideErrorBanner();
                    break;
                case SUCCESS:
                    showLoading(false);
                    Toast.makeText(this, R.string.account_created_success, Toast.LENGTH_SHORT).show();
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
        rgRoleSelector.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbArtisan) {
                selectedRole = UserRole.ARTISAN;
                layoutArtisanFields.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rbCustomer) {
                selectedRole = UserRole.CUSTOMER;
                layoutArtisanFields.setVisibility(View.GONE);
            }
        });

        btnSignUp.setOnClickListener(v -> performRegister());

        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void updateRoleSelection(UserRole role) {
        if (role == UserRole.CUSTOMER) {
            rbCustomer.setChecked(true);
            layoutArtisanFields.setVisibility(View.GONE);
        } else {
            rbArtisan.setChecked(true);
            layoutArtisanFields.setVisibility(View.VISIBLE);
        }
    }

    private void performRegister() {
        String name = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String shgName = etShgName.getText() != null ? etShgName.getText().toString().trim() : "";
        String craftType = etCraftType.getText() != null ? etCraftType.getText().toString().trim() : "";

        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        hideErrorBanner();

        boolean isValid = true;
        if (TextUtils.isEmpty(name)) {
            tilFullName.setError(getString(R.string.err_empty_name));
            isValid = false;
        }

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.err_empty_email));
            isValid = false;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            tilPassword.setError(getString(R.string.err_empty_password));
            isValid = false;
        }

        if (isValid) {
            RegisterRequest request = new RegisterRequest();
            request.setName(name);
            request.setEmail(email);
            request.setPhone(phone);
            request.setPassword(password);
            request.setRole(selectedRole);
            if (selectedRole == UserRole.ARTISAN) {
                request.setShgName(shgName);
                request.setCraftType(craftType);
            }

            authViewModel.register(request);
        }
    }

    private void showLoading(boolean isLoading) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (btnSignUp != null) {
            btnSignUp.setEnabled(!isLoading);
        }
    }

    private void showErrorBanner(String message) {
        if (tvErrorBanner != null) {
            tvErrorBanner.setText(message != null ? message : "Registration failed. Please check the details entered.");
            tvErrorBanner.setVisibility(View.VISIBLE);
        }
    }

    private void hideErrorBanner() {
        if (tvErrorBanner != null) {
            tvErrorBanner.setVisibility(View.GONE);
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
