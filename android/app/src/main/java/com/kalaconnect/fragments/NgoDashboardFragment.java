package com.kalaconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kalaconnect.R;

public class NgoDashboardFragment extends Fragment {

    private final AdminOverviewFragment overviewFragment = new AdminOverviewFragment();
    private final AdminArtisansFragment artisansFragment = new AdminArtisansFragment();
    private final AdminProductsFragment productsFragment = new AdminProductsFragment();
    private final AdminEnquiriesFragment enquiriesFragment = new AdminEnquiriesFragment();
    private final AdminAnalyticsFragment analyticsFragment = new AdminAnalyticsFragment();
    private final AdminProfileFragment profileFragment = new AdminProfileFragment();

    private LinearLayout tabDashboard;
    private LinearLayout tabArtisans;
    private LinearLayout tabProducts;
    private LinearLayout tabEnquiries;
    private LinearLayout tabAnalytics;
    private LinearLayout tabProfile;

    private ImageView ivTabDashboard, ivTabArtisans, ivTabProducts, ivTabEnquiries, ivTabAnalytics, ivTabProfile;
    private TextView tvTabDashboard, tvTabArtisans, tvTabProducts, tvTabEnquiries, tvTabAnalytics, tvTabProfile;

    private int selectedTabIndex = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ngo_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabDashboard = view.findViewById(R.id.tab_admin_dashboard);
        tabArtisans = view.findViewById(R.id.tab_admin_artisans);
        tabProducts = view.findViewById(R.id.tab_admin_products);
        tabEnquiries = view.findViewById(R.id.tab_admin_enquiries);
        tabAnalytics = view.findViewById(R.id.tab_admin_analytics);
        tabProfile = view.findViewById(R.id.tab_admin_profile);

        ivTabDashboard = view.findViewById(R.id.ivTabDashboard);
        ivTabArtisans = view.findViewById(R.id.ivTabArtisans);
        ivTabProducts = view.findViewById(R.id.ivTabProducts);
        ivTabEnquiries = view.findViewById(R.id.ivTabEnquiries);
        ivTabAnalytics = view.findViewById(R.id.ivTabAnalytics);
        ivTabProfile = view.findViewById(R.id.ivTabProfile);

        tvTabDashboard = view.findViewById(R.id.tvTabDashboard);
        tvTabArtisans = view.findViewById(R.id.tvTabArtisans);
        tvTabProducts = view.findViewById(R.id.tvTabProducts);
        tvTabEnquiries = view.findViewById(R.id.tvTabEnquiries);
        tvTabAnalytics = view.findViewById(R.id.tvTabAnalytics);
        tvTabProfile = view.findViewById(R.id.tvTabProfile);

        overviewFragment.setNavigationListener(this::selectTab);

        tabDashboard.setOnClickListener(v -> selectTab(0));
        tabArtisans.setOnClickListener(v -> selectTab(1));
        tabProducts.setOnClickListener(v -> selectTab(2));
        tabEnquiries.setOnClickListener(v -> selectTab(3));
        tabAnalytics.setOnClickListener(v -> selectTab(4));
        tabProfile.setOnClickListener(v -> selectTab(5));

        if (savedInstanceState == null) {
            selectTab(0);
        }
    }

    public void selectTab(int tabIndex) {
        selectedTabIndex = tabIndex;
        Fragment targetFragment;

        resetTabs();

        int primaryColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary);

        switch (tabIndex) {
            case 1:
                targetFragment = artisansFragment;
                ivTabArtisans.setColorFilter(primaryColor);
                tvTabArtisans.setTextColor(primaryColor);
                tvTabArtisans.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case 2:
                targetFragment = productsFragment;
                ivTabProducts.setColorFilter(primaryColor);
                tvTabProducts.setTextColor(primaryColor);
                tvTabProducts.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case 3:
                targetFragment = enquiriesFragment;
                ivTabEnquiries.setColorFilter(primaryColor);
                tvTabEnquiries.setTextColor(primaryColor);
                tvTabEnquiries.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case 4:
                targetFragment = analyticsFragment;
                ivTabAnalytics.setColorFilter(primaryColor);
                tvTabAnalytics.setTextColor(primaryColor);
                tvTabAnalytics.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case 5:
                targetFragment = profileFragment;
                ivTabProfile.setColorFilter(primaryColor);
                tvTabProfile.setTextColor(primaryColor);
                tvTabProfile.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case 0:
            default:
                targetFragment = overviewFragment;
                ivTabDashboard.setColorFilter(primaryColor);
                tvTabDashboard.setTextColor(primaryColor);
                tvTabDashboard.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
        }

        getChildFragmentManager().beginTransaction()
                .replace(R.id.admin_nav_container, targetFragment)
                .commit();
    }

    public void openLearningAndCertificates() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.admin_nav_container, new AdminLearningFragment())
                .addToBackStack(null)
                .commit();
    }

    private void resetTabs() {
        int secondaryColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary);

        ivTabDashboard.setColorFilter(secondaryColor);
        tvTabDashboard.setTextColor(secondaryColor);
        tvTabDashboard.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivTabArtisans.setColorFilter(secondaryColor);
        tvTabArtisans.setTextColor(secondaryColor);
        tvTabArtisans.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivTabProducts.setColorFilter(secondaryColor);
        tvTabProducts.setTextColor(secondaryColor);
        tvTabProducts.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivTabEnquiries.setColorFilter(secondaryColor);
        tvTabEnquiries.setTextColor(secondaryColor);
        tvTabEnquiries.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivTabAnalytics.setColorFilter(secondaryColor);
        tvTabAnalytics.setTextColor(secondaryColor);
        tvTabAnalytics.setTypeface(null, android.graphics.Typeface.NORMAL);

        ivTabProfile.setColorFilter(secondaryColor);
        tvTabProfile.setTextColor(secondaryColor);
        tvTabProfile.setTypeface(null, android.graphics.Typeface.NORMAL);
    }
}
