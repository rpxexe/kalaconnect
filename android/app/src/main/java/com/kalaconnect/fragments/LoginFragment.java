package com.kalaconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kalaconnect.R;
import com.kalaconnect.models.AuthResponse;
import com.kalaconnect.models.UserRole;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.AuthViewModel;

public class LoginFragment extends Fragment {

    private AuthViewModel authViewModel;

    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etPassword;
    private TextView tvLoginError;
    private MaterialButton btnLogin;
    private ProgressBar pbLoginLoading;
    private MaterialButton btnChangeRole;
    private TextView tvSignUpLink;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        tilEmail = view.findViewById(R.id.tilEmail);
        etEmail = view.findViewById(R.id.etEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        etPassword = view.findViewById(R.id.etPassword);
        tvLoginError = view.findViewById(R.id.tvLoginError);
        btnLogin = view.findViewById(R.id.btnLogin);
        pbLoginLoading = view.findViewById(R.id.pbLoginLoading);
        btnChangeRole = view.findViewById(R.id.btnChangeRole);
        tvSignUpLink = view.findViewById(R.id.tvSignUpLink);

        authViewModel.getAuthResult().observe(getViewLifecycleOwner(), this::handleAuthResult);

        btnLogin.setOnClickListener(v -> performLogin());

        btnChangeRole.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_login_to_userTypeSelection);
        });

        tvSignUpLink.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_login_to_signUp);
        });
    }

    private void performLogin() {
        tilEmail.setError(null);
        tilPassword.setError(null);
        tvLoginError.setVisibility(View.GONE);

        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        boolean hasError = false;
        if (email.isEmpty() || !email.contains("@")) {
            tilEmail.setError(getString(R.string.err_empty_email));
            hasError = true;
        }

        if (password.length() < 6) {
            tilPassword.setError(getString(R.string.err_empty_password));
            hasError = true;
        }

        if (!hasError) {
            authViewModel.login(email, password);
        }
    }

    private void handleAuthResult(NetworkResult<AuthResponse> result) {
        if (result == null) return;

        switch (result.getStatus()) {
            case LOADING:
                btnLogin.setEnabled(false);
                pbLoginLoading.setVisibility(View.VISIBLE);
                tvLoginError.setVisibility(View.GONE);
                break;

            case SUCCESS:
                btnLogin.setEnabled(true);
                pbLoginLoading.setVisibility(View.GONE);
                tvLoginError.setVisibility(View.GONE);

                AuthResponse response = result.getData();
                UserRole role = response != null ? response.getRole() : authViewModel.getLoggedInRole();
                navigateForRole(role);
                break;

            case ERROR:
                btnLogin.setEnabled(true);
                pbLoginLoading.setVisibility(View.GONE);
                tvLoginError.setText(result.getMessage());
                tvLoginError.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void navigateForRole(UserRole role) {
        NavController navController = Navigation.findNavController(requireView());
        if (role == null) {
            role = UserRole.CUSTOMER;
        }

        switch (role) {
            case ARTISAN:
                navController.navigate(R.id.action_login_to_artisanDashboard);
                break;
            case NGO_ADMIN:
                navController.navigate(R.id.action_login_to_ngoDashboard);
                break;
            case CUSTOMER:
            default:
                navController.navigate(R.id.action_login_to_customerDashboard);
                break;
        }
    }
}
