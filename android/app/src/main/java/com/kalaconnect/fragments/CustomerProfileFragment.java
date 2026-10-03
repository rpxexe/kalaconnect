package com.kalaconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.kalaconnect.R;
import com.kalaconnect.models.User;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.AuthViewModel;
import com.kalaconnect.viewmodel.CustomerViewModel;

public class CustomerProfileFragment extends Fragment {

    private AuthViewModel authViewModel;
    private CustomerViewModel customerViewModel;

    private TextView tvName;
    private TextView tvEmail;
    private TextView tvEnquiriesCount;
    private MaterialButton btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        customerViewModel = new ViewModelProvider(requireActivity()).get(CustomerViewModel.class);

        tvName = view.findViewById(R.id.tvCustomerProfileName);
        tvEmail = view.findViewById(R.id.tvCustomerProfileEmail);
        tvEnquiriesCount = view.findViewById(R.id.tvCustomerProfileEnquiriesCount);
        btnLogout = view.findViewById(R.id.btnCustomerProfileLogout);

        String currentName = authViewModel.getLoggedInUserName();
        if (currentName != null && !currentName.trim().isEmpty()) {
            tvName.setText(currentName);
        }

        String currentEmail = authViewModel.getLoggedInUserEmail();
        if (currentEmail != null && !currentEmail.trim().isEmpty()) {
            tvEmail.setText(currentEmail);
        }

        customerViewModel.getMyEnquiries().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.getStatus() == NetworkResult.Status.SUCCESS && result.getData() != null) {
                tvEnquiriesCount.setText(String.valueOf(result.getData().size()));
            }
        });

        TextView tvLanguage = view.findViewById(R.id.tvCustomerCurrentLanguage);
        View cardLanguage = view.findViewById(R.id.cardCustomerLanguageSettings);
        if (tvLanguage != null) {
            String lang = com.kalaconnect.utils.LocaleHelper.getLanguage(requireContext());
            if (com.kalaconnect.utils.LocaleHelper.LANGUAGE_HINDI.equals(lang)) {
                tvLanguage.setText(R.string.language_hindi);
            } else if (com.kalaconnect.utils.LocaleHelper.LANGUAGE_MARATHI.equals(lang)) {
                tvLanguage.setText(R.string.language_marathi);
            } else {
                tvLanguage.setText(R.string.language_english);
            }
        }
        if (cardLanguage != null) {
            cardLanguage.setOnClickListener(v -> {
                LanguageSelectionDialogFragment.newInstance().show(getChildFragmentManager(), LanguageSelectionDialogFragment.TAG);
            });
        }

        btnLogout.setOnClickListener(v -> {
            authViewModel.logout();
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_customer_logout);
        });
    }
}
