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

public class ArtisanDashboardFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;

    private final ArtisanOverviewFragment overviewFragment = new ArtisanOverviewFragment();
    private final MyProductsFragment productsFragment = new MyProductsFragment();
    private final EnquiriesFragment enquiriesFragment = new EnquiriesFragment();
    private final ArtisanNotificationsFragment notificationsFragment = new ArtisanNotificationsFragment();
    private final ArtisanProfileFragment profileFragment = new ArtisanProfileFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artisan_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigationView = view.findViewById(R.id.artisan_bottom_navigation);

        overviewFragment.setOnOverviewNavigationListener(this::selectBottomNavTab);

        bottomNavigationView.setOnItemSelectedListener(this::onNavItemSelected);

        // Show Dashboard overview initially
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.artisan_nav_container, overviewFragment)
                    .commit();
        }
    }

    private boolean onNavItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment;
        int id = item.getItemId();

        if (id == R.id.nav_artisan_dashboard) {
            selectedFragment = overviewFragment;
        } else if (id == R.id.nav_artisan_products) {
            selectedFragment = productsFragment;
        } else if (id == R.id.nav_artisan_enquiries) {
            selectedFragment = enquiriesFragment;
        } else if (id == R.id.nav_artisan_notifications) {
            selectedFragment = notificationsFragment;
        } else if (id == R.id.nav_artisan_profile) {
            selectedFragment = profileFragment;
        } else {
            selectedFragment = overviewFragment;
        }

        getChildFragmentManager().beginTransaction()
                .replace(R.id.artisan_nav_container, selectedFragment)
                .commit();

        return true;
    }

    public void selectBottomNavTab(int menuItemId) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(menuItemId);
        }
    }
}
