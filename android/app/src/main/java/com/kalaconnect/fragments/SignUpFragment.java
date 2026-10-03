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
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kalaconnect.R;
import com.kalaconnect.models.AuthResponse;
import com.kalaconnect.models.RegisterRequest;
import com.kalaconnect.models.UserRole;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.AuthViewModel;

public class SignUpFragment extends Fragment {

    private AuthViewModel authViewModel;

    private Chip chipSelectedRole;
    private TextInputLayout tilFullName;
    private TextInputEditText etFullName;
    private TextInputLayout tilSignUpEmail;
    private TextInputEditText etSignUpEmail;
    private TextInputLayout tilPhone;
    private TextInputEditText etPhone;
    private TextInputLayout tilSignUpPassword;
    private TextInputEditText etSignUpPassword;

    private TextInputLayout tilCraftType;
    private TextInputEditText etCraftType;
    private TextInputLayout tilShgName;
    private TextInputEditText etShgName;
    private TextInputLayout tilOrgName;
    private TextInputEditText etOrgName;

    private TextView tvSignUpError;
    private MaterialButton btnSignUp;
    private ProgressBar pbSignUpLoading;
    private TextView tvSignInLink;

    private UserRole currentRole = UserRole.ARTISAN;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        if (getArguments() != null && getArguments().containsKey("selectedRole")) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                UserRole role = getArguments().getSerializable("selectedRole", UserRole.class);
                if (role != null) {
                    currentRole = role;
                }
            } else {
                @SuppressWarnings("deprecation")
                Object roleObj = getArguments().getSerializable("selectedRole");
                if (roleObj instanceof UserRole) {
                    currentRole = (UserRole) roleObj;
                }
            }
        }

        chipSelectedRole = view.findViewById(R.id.chipSelectedRole);
        tilFullName = view.findViewById(R.id.tilFullName);
        etFullName = view.findViewById(R.id.etFullName);
        tilSignUpEmail = view.findViewById(R.id.tilSignUpEmail);
        etSignUpEmail = view.findViewById(R.id.etSignUpEmail);
        tilPhone = view.findViewById(R.id.tilPhone);
        etPhone = view.findViewById(R.id.etPhone);
        tilSignUpPassword = view.findViewById(R.id.tilSignUpPassword);
        etSignUpPassword = view.findViewById(R.id.etSignUpPassword);

        tilCraftType = view.findViewById(R.id.tilCraftType);
        etCraftType = view.findViewById(R.id.etCraftType);
        tilShgName = view.findViewById(R.id.tilShgName);
        etShgName = view.findViewById(R.id.etShgName);
        tilOrgName = view.findViewById(R.id.tilOrgName);
        etOrgName = view.findViewById(R.id.etOrgName);

        tvSignUpError = view.findViewById(R.id.tvSignUpError);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        pbSignUpLoading = view.findViewById(R.id.pbSignUpLoading);
        tvSignInLink = view.findViewById(R.id.tvSignInLink);

        setupRoleVisibility();

        chipSelectedRole.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_signUp_to_login);
        });

        authViewModel.getAuthResult().observe(getViewLifecycleOwner(), this::handleAuthResult);

        btnSignUp.setOnClickListener(v -> performSignUp());

        tvSignInLink.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_signUp_to_login);
        });
    }

    private void setupRoleVisibility() {
        chipSelectedRole.setText(getString(R.string.role_prefix, currentRole.name()));

        if (currentRole == UserRole.ARTISAN) {
            tilCraftType.setVisibility(View.VISIBLE);
            tilShgName.setVisibility(View.VISIBLE);
            tilOrgName.setVisibility(View.GONE);
        } else if (currentRole == UserRole.NGO_ADMIN) {
            tilCraftType.setVisibility(View.GONE);
            tilShgName.setVisibility(View.GONE);
            tilOrgName.setVisibility(View.VISIBLE);
        } else {
            tilCraftType.setVisibility(View.GONE);
            tilShgName.setVisibility(View.GONE);
            tilOrgName.setVisibility(View.GONE);
        }
    }

    private void performSignUp() {
        tilFullName.setError(null);
        tilSignUpEmail.setError(null);
        tilSignUpPassword.setError(null);
        tvSignUpError.setVisibility(View.GONE);

        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etSignUpEmail.getText() != null ? etSignUpEmail.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String password = etSignUpPassword.getText() != null ? etSignUpPassword.getText().toString().trim() : "";

        boolean hasError = false;
        if (fullName.isEmpty()) {
            tilFullName.setError(getString(R.string.err_empty_name));
            hasError = true;
        }

        if (email.isEmpty() || !email.contains("@")) {
            tilSignUpEmail.setError(getString(R.string.err_empty_email));
            hasError = true;
        }

        if (password.length() < 6) {
            tilSignUpPassword.setError(getString(R.string.err_empty_password));
            hasError = true;
        }

        if (!hasError) {
            if (currentRole == UserRole.NGO_ADMIN) {
                tvSignUpError.setText(R.string.ngo_admin_restricted_error);
                tvSignUpError.setVisibility(View.VISIBLE);
                return;
            }

            RegisterRequest request = new RegisterRequest();
            request.setFullName(fullName);
            request.setEmail(email);
            request.setPhoneNumber(phone);
            request.setPassword(password);
            request.setRole(currentRole);

            if (currentRole == UserRole.ARTISAN) {
                request.setCraftType(etCraftType.getText() != null ? etCraftType.getText().toString().trim() : null);
                request.setShgName(etShgName.getText() != null ? etShgName.getText().toString().trim() : null);
            } else if (currentRole == UserRole.NGO_ADMIN) {
                request.setOrganizationName(etOrgName.getText() != null ? etOrgName.getText().toString().trim() : null);
            }

            authViewModel.register(request);
        }
    }

    private void handleAuthResult(NetworkResult<AuthResponse> result) {
        if (result == null) return;

        switch (result.getStatus()) {
            case LOADING:
                btnSignUp.setEnabled(false);
                pbSignUpLoading.setVisibility(View.VISIBLE);
                tvSignUpError.setVisibility(View.GONE);
                break;

            case SUCCESS:
                btnSignUp.setEnabled(true);
                pbSignUpLoading.setVisibility(View.GONE);
                tvSignUpError.setVisibility(View.GONE);

                AuthResponse response = result.getData();
                UserRole role = response != null ? response.getRole() : currentRole;
                navigateForRole(role);
                break;

            case ERROR:
                btnSignUp.setEnabled(true);
                pbSignUpLoading.setVisibility(View.GONE);
                tvSignUpError.setText(result.getMessage());
                tvSignUpError.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void navigateForRole(UserRole role) {
        NavController navController = Navigation.findNavController(requireView());
        if (role == null) {
            role = currentRole;
        }

        switch (role) {
            case ARTISAN:
                navController.navigate(R.id.action_signUp_to_artisanDashboard);
                break;
            case NGO_ADMIN:
                navController.navigate(R.id.action_signUp_to_ngoDashboard);
                break;
            case CUSTOMER:
            default:
                navController.navigate(R.id.action_signUp_to_customerDashboard);
                break;
        }
    }
}
