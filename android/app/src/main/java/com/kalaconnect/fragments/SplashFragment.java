package com.kalaconnect.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.kalaconnect.R;
import com.kalaconnect.auth.AuthState;
import com.kalaconnect.models.UserRole;
import com.kalaconnect.viewmodel.AuthViewModel;

public class SplashFragment extends Fragment {

    private AuthViewModel authViewModel;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // Small delay to allow logo splash presentation and check persistent session
        handler.postDelayed(() -> {
            if (!isAdded()) return;
            authViewModel.checkSession();
        }, 1500);

        authViewModel.getAuthState().observe(getViewLifecycleOwner(), this::routeBasedOnAuthState);
    }

    private void routeBasedOnAuthState(AuthState state) {
        if (!isAdded()) return;
        NavController navController = Navigation.findNavController(requireView());

        if (state.isOnboardingRequired()) {
            navController.navigate(R.id.action_splash_to_onboarding);
        } else if (state.isAuthenticated()) {
            UserRole role = state.getUserRole();
            if (role == null) {
                role = authViewModel.getLoggedInRole();
            }
            routeRoleDestination(navController, role);
        } else {
            navController.navigate(R.id.action_splash_to_login);
        }
    }

    private void routeRoleDestination(NavController navController, UserRole role) {
        if (role == null) {
            navController.navigate(R.id.action_splash_to_login);
            return;
        }

        switch (role) {
            case ARTISAN:
                navController.navigate(R.id.action_splash_to_artisanDashboard);
                break;
            case NGO_ADMIN:
                navController.navigate(R.id.action_splash_to_ngoDashboard);
                break;
            case CUSTOMER:
            default:
                navController.navigate(R.id.action_splash_to_customerDashboard);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
