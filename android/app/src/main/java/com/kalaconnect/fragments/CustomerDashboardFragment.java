package com.kalaconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kalaconnect.R;

public class CustomerDashboardFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;

    private final HomeFragment homeFragment = new HomeFragment();
    private final ExploreFragment exploreFragment = new ExploreFragment();
    private final CustomerEnquiriesFragment enquiriesFragment = new CustomerEnquiriesFragment();
    private final CustomerNotificationsFragment notificationsFragment = new CustomerNotificationsFragment();
    private final CustomerProfileFragment profileFragment = new CustomerProfileFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigationView = view.findViewById(R.id.customer_bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this::onNavItemSelected);

        // Show Home screen initially
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.customer_nav_container, homeFragment)
                    .commit();
        }
    }

    private boolean onNavItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment;
        int id = item.getItemId();

        if (id == R.id.nav_customer_home) {
            selectedFragment = homeFragment;
        } else if (id == R.id.nav_customer_explore) {
            selectedFragment = exploreFragment;
        } else if (id == R.id.nav_customer_enquiries) {
            selectedFragment = enquiriesFragment;
        } else if (id == R.id.nav_customer_notifications) {
            selectedFragment = notificationsFragment;
        } else if (id == R.id.nav_customer_profile) {
            selectedFragment = profileFragment;
        } else {
            selectedFragment = homeFragment;
        }

        getChildFragmentManager().beginTransaction()
                .replace(R.id.customer_nav_container, selectedFragment)
                .commit();

        return true;
    }

    public void selectBottomNavTab(int menuItemId) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(menuItemId);
        }
    }

    public void switchToExploreTab(String category) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_customer_explore);
        }
        if (category != null && !category.isEmpty() && !"all".equalsIgnoreCase(category)) {
            exploreFragment.selectCategory(category);
        }
    }
}
