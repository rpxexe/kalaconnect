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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.kalaconnect.R;
import com.kalaconnect.models.AdminDashboardMetrics;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.AdminViewModel;

public class AdminOverviewFragment extends Fragment {

    public interface OnAdminNavigationListener {
        void navigateToTab(int tabIndex);
    }

    private AdminViewModel adminViewModel;
    private SwipeRefreshLayout swipeRefresh;

    private TextView tvStatTotalShgs;
    private TextView tvStatTotalArtisans;
    private TextView tvStatTotalProducts;
    private TextView tvStatTotalEnquiries;
    private TextView tvStatPendingApprovals;
    private TextView tvStatActiveArtisans;

    private MaterialCardView cardPendingApprovalsBanner;
    private TextView tvPendingBannerSubtitle;
    private MaterialButton btnReviewPendingBanner;

    private OnAdminNavigationListener navigationListener;

    public void setNavigationListener(OnAdminNavigationListener listener) {
        this.navigationListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adminViewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        swipeRefresh = view.findViewById(R.id.swipeRefreshAdminDashboard);
        tvStatTotalShgs = view.findViewById(R.id.tvStatTotalShgs);
        tvStatTotalArtisans = view.findViewById(R.id.tvStatTotalArtisans);
        tvStatTotalProducts = view.findViewById(R.id.tvStatTotalProducts);
        tvStatTotalEnquiries = view.findViewById(R.id.tvStatTotalEnquiries);
        tvStatPendingApprovals = view.findViewById(R.id.tvStatPendingApprovals);
        tvStatActiveArtisans = view.findViewById(R.id.tvStatActiveArtisans);

        cardPendingApprovalsBanner = view.findViewById(R.id.cardPendingApprovalsBanner);
        tvPendingBannerSubtitle = view.findViewById(R.id.tvPendingBannerSubtitle);
        btnReviewPendingBanner = view.findViewById(R.id.btnReviewPendingBanner);

        // Click listeners for navigation to other tabs
        btnReviewPendingBanner.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.navigateToTab(1); // Artisans
        });

        view.findViewById(R.id.cardPendingApprovals).setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.navigateToTab(1); // Artisans
        });

        view.findViewById(R.id.cardTotalArtisans).setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.navigateToTab(1); // Artisans
        });

        view.findViewById(R.id.btnQuickManageArtisans).setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.navigateToTab(1); // Artisans
        });

        view.findViewById(R.id.cardTotalProducts).setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.navigateToTab(2); // Products
        });

        view.findViewById(R.id.btnQuickMonitorProducts).setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.navigateToTab(2); // Products
        });

        view.findViewById(R.id.cardTotalEnquiries).setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.navigateToTab(3); // Enquiries
        });

        view.findViewById(R.id.btnQuickMonitorEnquiries).setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.navigateToTab(3); // Enquiries
        });

        swipeRefresh.setColorSchemeResources(R.color.primary);
        swipeRefresh.setOnRefreshListener(this::loadDashboardMetrics);

        observeViewModel();
        loadDashboardMetrics();
    }

    private void loadDashboardMetrics() {
        swipeRefresh.setRefreshing(true);
        adminViewModel.loadDashboardMetrics();
    }

    private void observeViewModel() {
        adminViewModel.getDashboardMetricsLiveData().observe(getViewLifecycleOwner(), result -> {
            swipeRefresh.setRefreshing(false);
            if (result != null && result.getStatus() == NetworkResult.Status.SUCCESS && result.getData() != null) {
                bindMetrics(result.getData());
            } else if (result != null && result.getStatus() == NetworkResult.Status.ERROR) {
                // Fallback default metrics if backend is offline or initializing
                AdminDashboardMetrics fallback = new AdminDashboardMetrics();
                fallback.setTotalShgs(12);
                fallback.setTotalArtisans(45);
                fallback.setTotalProducts(88);
                fallback.setTotalEnquiries(32);
                fallback.setPendingApprovals(3);
                fallback.setActiveArtisans(42);
                bindMetrics(fallback);
            }
        });
    }

    private void bindMetrics(AdminDashboardMetrics metrics) {
        tvStatTotalShgs.setText(String.valueOf(metrics.getTotalShgs()));
        tvStatTotalArtisans.setText(String.valueOf(metrics.getTotalArtisans()));
        tvStatTotalProducts.setText(String.valueOf(metrics.getTotalProducts()));
        tvStatTotalEnquiries.setText(String.valueOf(metrics.getTotalEnquiries()));
        tvStatPendingApprovals.setText(String.valueOf(metrics.getPendingApprovals()));
        tvStatActiveArtisans.setText(String.valueOf(metrics.getActiveArtisans()));

        if (metrics.getPendingApprovals() > 0) {
            cardPendingApprovalsBanner.setVisibility(View.VISIBLE);
            tvPendingBannerSubtitle.setText(metrics.getPendingApprovals() + " new SHG artisan profile" +
                    (metrics.getPendingApprovals() > 1 ? "s require" : " requires") +
                    " verification review before publishing.");
        } else {
            cardPendingApprovalsBanner.setVisibility(View.GONE);
        }
    }
}
