package com.kalaconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.kalaconnect.R;
import com.kalaconnect.activities.LoginActivity;
import com.kalaconnect.activities.SignupActivity;
import com.kalaconnect.models.UserRole;
import com.kalaconnect.viewmodel.UserTypeViewModel;

public class RoleSelectionFragment extends Fragment {

    private UserTypeViewModel userTypeViewModel;

    private CardView cardArtisan;
    private CardView cardNgo;
    private CardView cardCustomer;

    private LinearLayout layoutArtisanInner;
    private LinearLayout layoutNgoInner;
    private LinearLayout layoutCustomerInner;

    private MaterialButton btnContinueRole;
    private TextView tvLoginLink;

    private UserRole currentSelectedRole = UserRole.ARTISAN;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_type_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userTypeViewModel = new ViewModelProvider(this).get(UserTypeViewModel.class);

        cardArtisan = view.findViewById(R.id.cardArtisan);
        cardNgo = view.findViewById(R.id.cardNgo);
        cardCustomer = view.findViewById(R.id.cardCustomer);

        layoutArtisanInner = view.findViewById(R.id.layoutArtisanInner);
        layoutNgoInner = view.findViewById(R.id.layoutNgoInner);
        layoutCustomerInner = view.findViewById(R.id.layoutCustomerInner);

        btnContinueRole = view.findViewById(R.id.btnContinueRole);
        tvLoginLink = view.findViewById(R.id.tvLoginLink);

        userTypeViewModel.getSelectedRole().observe(getViewLifecycleOwner(), role -> {
            currentSelectedRole = role;
            updateSelectionUi(role);
        });

        cardArtisan.setOnClickListener(v -> userTypeViewModel.selectRole(UserRole.ARTISAN));
        cardCustomer.setOnClickListener(v -> userTypeViewModel.selectRole(UserRole.CUSTOMER));

        cardNgo.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.dialog_institutional_account_title)
                    .setMessage(R.string.dialog_institutional_account_desc)
                    .setPositiveButton(R.string.dialog_understood, null)
                    .show();
        });

        btnContinueRole.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SignupActivity.class);
            intent.putExtra("selectedRole", currentSelectedRole);
            startActivity(intent);
        });

        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
        });
    }

    private void updateSelectionUi(UserRole role) {
        if (layoutArtisanInner != null) {
            layoutArtisanInner.setBackgroundResource(
                    role == UserRole.ARTISAN ? R.drawable.bg_role_card_selected : R.drawable.bg_role_card_unselected
            );
        }
        if (layoutCustomerInner != null) {
            layoutCustomerInner.setBackgroundResource(
                    role == UserRole.CUSTOMER ? R.drawable.bg_role_card_selected : R.drawable.bg_role_card_unselected
            );
        }
    }
}
