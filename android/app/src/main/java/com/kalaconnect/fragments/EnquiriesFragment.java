package com.kalaconnect.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kalaconnect.R;
import com.kalaconnect.adapters.EnquiryAdapter;
import com.kalaconnect.models.EnquiryItem;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.ArtisanViewModel;

import java.util.ArrayList;
import java.util.List;

public class EnquiriesFragment extends Fragment {

    private ArtisanViewModel artisanViewModel;
    private RecyclerView rvArtisanEnquiries;
    private SwipeRefreshLayout swipeRefreshArtisanEnquiries;
    private ProgressBar pbArtisanEnquiriesLoading;
    private View layoutEnquiriesEmpty;
    private TextView tvEmptyEnquiriesMessage;
    private ChipGroup chipGroupStatusFilter;

    private EnquiryAdapter enquiryAdapter;
    private String currentStatusFilter = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artisan_enquiries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        artisanViewModel = new ViewModelProvider(requireActivity()).get(ArtisanViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupFilterChips();
        setupObservers();

        loadEnquiries();
    }

    private void initViews(View view) {
        rvArtisanEnquiries = view.findViewById(R.id.rvArtisanEnquiries);
        swipeRefreshArtisanEnquiries = view.findViewById(R.id.swipeRefreshArtisanEnquiries);
        pbArtisanEnquiriesLoading = view.findViewById(R.id.pbArtisanEnquiriesLoading);
        layoutEnquiriesEmpty = view.findViewById(R.id.layoutEnquiriesEmpty);
        tvEmptyEnquiriesMessage = view.findViewById(R.id.tvEmptyEnquiriesMessage);
        chipGroupStatusFilter = view.findViewById(R.id.chipGroupEnquiryStatusFilter);

        swipeRefreshArtisanEnquiries.setColorSchemeResources(R.color.primary);
        swipeRefreshArtisanEnquiries.setOnRefreshListener(this::loadEnquiries);
    }

    private void setupRecyclerView() {
        enquiryAdapter = new EnquiryAdapter();
        enquiryAdapter.setOnEnquiryClickListener(new EnquiryAdapter.OnEnquiryClickListener() {
            @Override
            public void onReply(EnquiryItem item) {
                showContactCustomerDialog(item);
            }

            @Override
            public void onUpdateStatus(EnquiryItem item) {
                showUpdateStatusDialog(item);
            }
        });

        rvArtisanEnquiries.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvArtisanEnquiries.setAdapter(enquiryAdapter);
    }

    private void setupFilterChips() {
        if (chipGroupStatusFilter != null) {
            chipGroupStatusFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty() || checkedIds.contains(R.id.chipFilterAll)) {
                    currentStatusFilter = null;
                } else if (checkedIds.contains(R.id.chipFilterPending)) {
                    currentStatusFilter = "PENDING";
                } else if (checkedIds.contains(R.id.chipFilterContacted)) {
                    currentStatusFilter = "CONTACTED";
                } else if (checkedIds.contains(R.id.chipFilterResolved)) {
                    currentStatusFilter = "RESOLVED";
                } else if (checkedIds.contains(R.id.chipFilterClosed)) {
                    currentStatusFilter = "CLOSED";
                }
                loadEnquiries();
            });
        }
    }

    private void setupObservers() {
        artisanViewModel.getEnquiriesLiveData().observe(getViewLifecycleOwner(), result -> {
            swipeRefreshArtisanEnquiries.setRefreshing(false);
            if (result == null) return;

            if (result.getStatus() == NetworkResult.Status.LOADING) {
                if (enquiryAdapter.getItemCount() == 0) {
                    pbArtisanEnquiriesLoading.setVisibility(View.VISIBLE);
                }
            } else if (result.getStatus() == NetworkResult.Status.SUCCESS) {
                pbArtisanEnquiriesLoading.setVisibility(View.GONE);
                List<EnquiryItem> items = result.getData() != null ? result.getData() : new ArrayList<>();
                enquiryAdapter.setEnquiries(items);

                if (items.isEmpty()) {
                    layoutEnquiriesEmpty.setVisibility(View.VISIBLE);
                    rvArtisanEnquiries.setVisibility(View.GONE);
                    if (currentStatusFilter != null) {
                        tvEmptyEnquiriesMessage.setText(getString(R.string.no_enquiries_status, currentStatusFilter));
                    } else {
                        tvEmptyEnquiriesMessage.setText(R.string.enquiries_empty_artisan_hint);
                    }
                } else {
                    layoutEnquiriesEmpty.setVisibility(View.GONE);
                    rvArtisanEnquiries.setVisibility(View.VISIBLE);
                }
            } else if (result.getStatus() == NetworkResult.Status.ERROR) {
                pbArtisanEnquiriesLoading.setVisibility(View.GONE);
                if (enquiryAdapter.getItemCount() == 0) {
                    layoutEnquiriesEmpty.setVisibility(View.VISIBLE);
                    rvArtisanEnquiries.setVisibility(View.GONE);
                }
                Toast.makeText(requireContext(), getString(R.string.failed_load_enquiries, result.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });

        artisanViewModel.getUpdateStatusResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.getStatus() == NetworkResult.Status.SUCCESS) {
                Toast.makeText(requireContext(), R.string.status_updated_success, Toast.LENGTH_SHORT).show();
            } else if (result.getStatus() == NetworkResult.Status.ERROR) {
                Toast.makeText(requireContext(), getString(R.string.error_updating_status, result.getMessage()), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadEnquiries() {
        artisanViewModel.loadEnquiries(currentStatusFilter);
    }

    private void showUpdateStatusDialog(EnquiryItem item) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_update_enquiry_status, null);
        dialog.setContentView(sheetView);

        RadioGroup rgStatus = sheetView.findViewById(R.id.rgEnquiryStatus);
        MaterialButton btnCancel = sheetView.findViewById(R.id.btnCancelStatusUpdate);
        MaterialButton btnSave = sheetView.findViewById(R.id.btnSaveStatusUpdate);

        String currentStatus = item.getStatus() != null ? item.getStatus().toUpperCase() : "PENDING";
        if ("RESOLVED".equals(currentStatus)) {
            rgStatus.check(R.id.rbStatusResolved);
        } else if ("CONTACTED".equals(currentStatus)) {
            rgStatus.check(R.id.rbStatusContacted);
        } else if ("CLOSED".equals(currentStatus)) {
            rgStatus.check(R.id.rbStatusClosed);
        } else {
            rgStatus.check(R.id.rbStatusPending);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            int selectedId = rgStatus.getCheckedRadioButtonId();
            String newStatus = "PENDING";
            if (selectedId == R.id.rbStatusContacted) {
                newStatus = "CONTACTED";
            } else if (selectedId == R.id.rbStatusResolved) {
                newStatus = "RESOLVED";
            } else if (selectedId == R.id.rbStatusClosed) {
                newStatus = "CLOSED";
            }

            dialog.dismiss();
            artisanViewModel.updateEnquiryStatus(item.getId(), newStatus);
        });

        dialog.show();
    }

    private void showContactCustomerDialog(EnquiryItem item) {
        String phone = item.getCustomerPhone();
        String email = item.getCustomerEmail();
        String name = item.getCustomerName() != null ? item.getCustomerName() : "Buyer";

        List<String> options = new ArrayList<>();
        if (phone != null && !phone.isBlank()) {
            options.add("Call " + phone);
            options.add("WhatsApp " + phone);
        }
        if (email != null && !email.isBlank()) {
            options.add("Email " + email);
        }
        options.add("Update Status");

        String[] optionsArray = options.toArray(new String[0]);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.connect_with_buyer, name))
                .setItems(optionsArray, (dialog, which) -> {
                    String selected = optionsArray[which];
                    if (selected.startsWith("Call")) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                        startActivity(intent);
                    } else if (selected.startsWith("WhatsApp")) {
                        String cleanPhone = phone.replaceAll("[^0-9]", "");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + cleanPhone));
                        startActivity(intent);
                    } else if (selected.startsWith("Email")) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Regarding your enquiry on KalaConnect");
                        startActivity(intent);
                    } else if ("Update Status".equals(selected)) {
                        showUpdateStatusDialog(item);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
