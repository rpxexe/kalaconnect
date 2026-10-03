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
import com.kalaconnect.viewmodel.AuthViewModel;

public class AdminProfileFragment extends Fragment {

    private AuthViewModel authViewModel;
    private TextView tvAdminProfileName;
    private TextView tvAdminProfileEmail;
    private MaterialButton btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        tvAdminProfileName = view.findViewById(R.id.tvAdminProfileName);
        tvAdminProfileEmail = view.findViewById(R.id.tvAdminProfileEmail);
        btnLogout = view.findViewById(R.id.btnAdminProfileLogout);

        String name = authViewModel.getLoggedInUserName();
        if (name != null && !name.trim().isEmpty()) {
            tvAdminProfileName.setText(name);
        }

        TextView tvLanguage = view.findViewById(R.id.tvAdminCurrentLanguage);
        View cardLanguage = view.findViewById(R.id.cardAdminLanguageSettings);
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
            try {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_ngo_logout);
            } catch (Exception e) {
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_ngo_logout);
            }
        });
    }
}
