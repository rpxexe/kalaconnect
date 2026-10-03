package com.kalaconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.kalaconnect.R;
import com.kalaconnect.models.UserRole;
import com.kalaconnect.viewmodel.UserTypeViewModel;

public class UserTypeSelectionFragment extends Fragment {

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
        cardNgo.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle(R.string.dialog_institutional_account_title)
                    .setMessage(R.string.dialog_institutional_account_desc)
                    .setPositiveButton(R.string.dialog_understood, null)
                    .show();
        });
        cardCustomer.setOnClickListener(v -> userTypeViewModel.selectRole(UserRole.CUSTOMER));

        btnContinueRole.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putSerializable("selectedRole", currentSelectedRole);
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_userTypeSelection_to_signUp, args);
        });

        tvLoginLink.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_userTypeSelection_to_login);
        });
    }

    private void updateSelectionUi(UserRole role) {
        layoutArtisanInner.setBackgroundResource(
                role == UserRole.ARTISAN ? R.drawable.bg_role_card_selected : R.drawable.bg_role_card_unselected
        );
        layoutNgoInner.setBackgroundResource(
                role == UserRole.NGO_ADMIN ? R.drawable.bg_role_card_selected : R.drawable.bg_role_card_unselected
        );
        layoutCustomerInner.setBackgroundResource(
                role == UserRole.CUSTOMER ? R.drawable.bg_role_card_selected : R.drawable.bg_role_card_unselected
        );
    }
}
