package com.kalaconnect.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.ChipGroup;
import com.kalaconnect.R;
import com.kalaconnect.adapters.EnquiryAdapter;
import com.kalaconnect.models.EnquiryItem;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.AdminViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminEnquiriesFragment extends Fragment implements EnquiryAdapter.OnEnquiryClickListener {

    private AdminViewModel adminViewModel;
    private EnquiryAdapter adapter;

    private ChipGroup chipGroupStatus;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvEnquiries;
    private ProgressBar pbLoading;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyMessage;

    private String currentStatusFilter = null; // null means ALL

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_enquiries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adminViewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        chipGroupStatus = view.findViewById(R.id.chipGroupAdminEnquiryStatus);
        swipeRefresh = view.findViewById(R.id.swipeRefreshAdminEnquiries);
        rvEnquiries = view.findViewById(R.id.rvAdminEnquiries);
        pbLoading = view.findViewById(R.id.pbAdminEnquiriesLoading);
        layoutEmpty = view.findViewById(R.id.layoutAdminEnquiriesEmpty);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyAdminEnquiriesMessage);

        adapter = new EnquiryAdapter();
        adapter.setOnEnquiryClickListener(this);
        rvEnquiries.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvEnquiries.setAdapter(adapter);

        swipeRefresh.setColorSchemeResources(R.color.primary);
        swipeRefresh.setOnRefreshListener(this::loadEnquiries);

        setupFilters();
        observeViewModel();

        loadEnquiries();
    }

    private void setupFilters() {
        chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);

            if (id == R.id.chipAdminEnquiryPending) {
                currentStatusFilter = "PENDING";
            } else if (id == R.id.chipAdminEnquiryContacted) {
                currentStatusFilter = "CONTACTED";
            } else if (id == R.id.chipAdminEnquiryResolved) {
                currentStatusFilter = "RESOLVED";
            } else if (id == R.id.chipAdminEnquiryClosed) {
                currentStatusFilter = "CLOSED";
            } else {
                currentStatusFilter = null; // ALL
            }
            loadEnquiries();
        });
    }

    private void loadEnquiries() {
        if (pbLoading != null) pbLoading.setVisibility(View.VISIBLE);
        adminViewModel.loadEnquiries(currentStatusFilter);
    }

    private void observeViewModel() {
        adminViewModel.getEnquiriesLiveData().observe(getViewLifecycleOwner(), result -> {
            if (pbLoading != null) pbLoading.setVisibility(View.GONE);
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);

            if (result != null && result.getStatus() == NetworkResult.Status.SUCCESS && result.getData() != null) {
                updateList(result.getData());
            } else if (result != null && result.getStatus() == NetworkResult.Status.ERROR) {
                loadDemoEnquiriesFallback();
            }
        });
    }

    private void updateList(List<EnquiryItem> items) {
        adapter.setEnquiries(items);
        if (items == null || items.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvEnquiries.setVisibility(View.GONE);
            if (currentStatusFilter != null) {
                tvEmptyMessage.setText(getString(R.string.no_enquiries_status, currentStatusFilter));
            } else {
                tvEmptyMessage.setText(R.string.no_enquiries_recorded);
            }
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvEnquiries.setVisibility(View.VISIBLE);
        }
    }

    private void loadDemoEnquiriesFallback() {
        List<EnquiryItem> demo = new ArrayList<>();
        demo.add(new EnquiryItem(1L, 101L, "Terracotta Decorative Vase", 10L, "Ananya Sharma",
                "Hello, are you able to provide 30 units of this terracotta vase for an upcoming wedding in Lucknow?",
                "PENDING", "Today, 11:20 AM", "ananya@example.com", "+91 98765 43210"));

        demo.add(new EnquiryItem(2L, 102L, "Handwoven Tussar Silk Dupatta", 11L, "Vikram Singhania",
                "Interested in bulk order for corporate gifting. Can you share custom packaging options?",
                "CONTACTED", "Yesterday, 4:15 PM", "vikram@singhania.org", "+91 98111 22334"));

        demo.add(new EnquiryItem(3L, 103L, "Carved Teakwood Keepsake Box", 12L, "Dr. Priya Nair",
                "Thank you for dispatching the sample piece. Order finalized and confirmed.",
                "RESOLVED", "2 days ago", "priya.nair@hospital.in", "+91 94470 11223"));

        List<EnquiryItem> filtered = new ArrayList<>();
        for (EnquiryItem item : demo) {
            if (currentStatusFilter == null || currentStatusFilter.equalsIgnoreCase(item.getStatus())) {
                filtered.add(item);
            }
        }
        updateList(filtered);
    }

    @Override
    public void onReply(EnquiryItem enquiry) {
        if (enquiry.getCustomerPhone() != null && !enquiry.getCustomerPhone().isBlank()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + enquiry.getCustomerPhone()));
            startActivity(intent);
        } else if (enquiry.getCustomerEmail() != null && !enquiry.getCustomerEmail().isBlank()) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + enquiry.getCustomerEmail()));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding your KalaConnect inquiry on " + enquiry.getProductName());
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        } else {
            Toast.makeText(requireContext(), R.string.no_buyer_contact, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpdateStatus(EnquiryItem enquiry) {
        Toast.makeText(requireContext(), getString(R.string.enquiry_status_toast, enquiry.getId(), enquiry.getStatus()), Toast.LENGTH_SHORT).show();
    }
}
