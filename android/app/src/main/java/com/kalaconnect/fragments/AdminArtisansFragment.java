package com.kalaconnect.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.kalaconnect.R;
import com.kalaconnect.adapters.AdminArtisanAdapter;
import com.kalaconnect.models.ArtisanProfile;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.AdminViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminArtisansFragment extends Fragment implements AdminArtisanAdapter.OnArtisanActionListener {

    private AdminViewModel adminViewModel;
    private AdminArtisanAdapter adapter;

    private EditText etSearchArtisan;
    private ChipGroup chipGroupStatus;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvArtisans;
    private ProgressBar pbLoading;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyMessage;

    private String currentStatusFilter = null; // null means ALL
    private String currentSearchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_artisans, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adminViewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        etSearchArtisan = view.findViewById(R.id.etAdminSearchArtisan);
        chipGroupStatus = view.findViewById(R.id.chipGroupAdminArtisanStatus);
        swipeRefresh = view.findViewById(R.id.swipeRefreshAdminArtisans);
        rvArtisans = view.findViewById(R.id.rvAdminArtisans);
        pbLoading = view.findViewById(R.id.pbAdminArtisansLoading);
        layoutEmpty = view.findViewById(R.id.layoutAdminArtisansEmpty);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyArtisansMessage);

        adapter = new AdminArtisanAdapter(this);
        rvArtisans.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvArtisans.setAdapter(adapter);

        swipeRefresh.setColorSchemeResources(R.color.primary);
        swipeRefresh.setOnRefreshListener(this::loadArtisans);

        setupFilters();
        setupSearch();
        observeViewModel();

        loadArtisans();
    }

    private void setupFilters() {
        chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);

            if (id == R.id.chipArtisanPending) {
                currentStatusFilter = "PENDING";
            } else if (id == R.id.chipArtisanApproved) {
                currentStatusFilter = "APPROVED";
            } else if (id == R.id.chipArtisanRejected) {
                currentStatusFilter = "REJECTED";
            } else {
                currentStatusFilter = null; // All
            }
            loadArtisans();
        });
    }

    private void setupSearch() {
        etSearchArtisan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = (s != null) ? s.toString().trim() : "";
            }

            @Override
            public void afterTextChanged(Editable s) {
                loadArtisans();
            }
        });
    }

    public void setFilterToPending() {
        if (chipGroupStatus != null) {
            chipGroupStatus.check(R.id.chipArtisanPending);
            currentStatusFilter = "PENDING";
            loadArtisans();
        }
    }

    private void loadArtisans() {
        if (pbLoading != null) pbLoading.setVisibility(View.VISIBLE);
        adminViewModel.loadArtisans(currentSearchQuery, currentStatusFilter, 0, 50);
    }

    private void observeViewModel() {
        adminViewModel.getArtisansLiveData().observe(getViewLifecycleOwner(), result -> {
            if (pbLoading != null) pbLoading.setVisibility(View.GONE);
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);

            if (result != null && result.getStatus() == NetworkResult.Status.SUCCESS && result.getData() != null) {
                List<ArtisanProfile> items = result.getData().getItems();
                updateList(items);
            } else if (result != null && result.getStatus() == NetworkResult.Status.ERROR) {
                loadDemoArtisansFallback();
            }
        });

        adminViewModel.getArtisanActionLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.getStatus() == NetworkResult.Status.SUCCESS) {
                Toast.makeText(requireContext(), R.string.artisan_status_updated, Toast.LENGTH_SHORT).show();
                loadArtisans();
            } else if (result != null && result.getStatus() == NetworkResult.Status.ERROR) {
                Toast.makeText(requireContext(), result.getMessage() != null ? result.getMessage() : getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateList(List<ArtisanProfile> items) {
        adapter.setArtisans(items);
        if (items == null || items.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvArtisans.setVisibility(View.GONE);
            if (currentStatusFilter != null) {
                tvEmptyMessage.setText(getString(R.string.no_artisans_status, currentStatusFilter));
            } else {
                tvEmptyMessage.setText(R.string.no_artisans_search);
            }
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvArtisans.setVisibility(View.VISIBLE);
        }
    }

    private void loadDemoArtisansFallback() {
        List<ArtisanProfile> demo = new ArrayList<>();

        ArtisanProfile a1 = new ArtisanProfile();
        a1.setId(1L);
        a1.setArtisanName("Ramesh Kumar");
        a1.setShgName("Varanasi Silk & Handloom SHG");
        a1.setDistrict("Varanasi");
        a1.setState("Uttar Pradesh");
        a1.setExperience(12);
        a1.setSkills(List.of("Handloom Weaving", "Zardozi", "Silk Dyeing"));
        a1.setBio("Specializing in pure mulberry silk sarees and intricate traditional motifs passed down through three generations.");
        a1.setApprovalStatus("PENDING");
        demo.add(a1);

        ArtisanProfile a2 = new ArtisanProfile();
        a2.setId(2L);
        a2.setArtisanName("Meera Devi");
        a2.setShgName("Mithila Kala Mahila Mandal");
        a2.setDistrict("Madhubani");
        a2.setState("Bihar");
        a2.setExperience(15);
        a2.setSkills(List.of("Madhubani Painting", "Natural Dyes", "Folk Art"));
        a2.setBio("State-award recognized Madhubani artist working with natural botanical dyes and handmade Lokta paper.");
        a2.setApprovalStatus("APPROVED");
        a2.setVerified(true);
        demo.add(a2);

        ArtisanProfile a3 = new ArtisanProfile();
        a3.setId(3L);
        a3.setArtisanName("Sunita Soren");
        a3.setShgName("Santal Pargana Bamboo Collective");
        a3.setDistrict("Dumka");
        a3.setState("Jharkhand");
        a3.setExperience(6);
        a3.setSkills(List.of("Bamboo Craft", "Basketry", "Cane Work"));
        a3.setBio("Eco-friendly artisanal bamboo containers and traditional homeware crafts.");
        a3.setApprovalStatus("PENDING");
        demo.add(a3);

        List<ArtisanProfile> filtered = new ArrayList<>();
        for (ArtisanProfile ap : demo) {
            boolean matchesStatus = (currentStatusFilter == null) || currentStatusFilter.equalsIgnoreCase(ap.getApprovalStatus());
            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    ap.getArtisanName().toLowerCase().contains(currentSearchQuery.toLowerCase()) ||
                    (ap.getShgName() != null && ap.getShgName().toLowerCase().contains(currentSearchQuery.toLowerCase()));
            if (matchesStatus && matchesSearch) {
                filtered.add(ap);
            }
        }
        updateList(filtered);
    }

    @Override
    public void onApprove(ArtisanProfile artisan) {
        showActionRemarkDialog(artisan, true);
    }

    @Override
    public void onReject(ArtisanProfile artisan) {
        showActionRemarkDialog(artisan, false);
    }

    @Override
    public void onViewDetails(ArtisanProfile artisan) {
        showArtisanDetailsSheet(artisan);
    }

    private void showActionRemarkDialog(ArtisanProfile artisan, boolean isApprove) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheet = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_artisan_action, null);
        dialog.setContentView(sheet);

        TextView tvTitle = sheet.findViewById(R.id.tvActionDialogTitle);
        TextView tvSubtitle = sheet.findViewById(R.id.tvActionDialogSubtitle);
        TextInputEditText etReason = sheet.findViewById(R.id.etActionReason);
        MaterialButton btnCancel = sheet.findViewById(R.id.btnCancelAction);
        MaterialButton btnConfirm = sheet.findViewById(R.id.btnConfirmAction);

        if (isApprove) {
            tvTitle.setText(R.string.dialog_approve_artisan_title);
            tvSubtitle.setText(getString(R.string.dialog_approve_artisan_subtitle, artisan.getArtisanName(), artisan.getShgName()));
            btnConfirm.setText(R.string.btn_approve_profile);
            btnConfirm.setBackgroundColor(requireContext().getColor(R.color.primary));
            etReason.setHint("Verification remark (e.g. SHG credentials checked)");
        } else {
            tvTitle.setText(R.string.dialog_reject_artisan_title);
            tvSubtitle.setText(getString(R.string.dialog_reject_artisan_subtitle, artisan.getArtisanName()));
            btnConfirm.setText(R.string.btn_reject_profile);
            btnConfirm.setBackgroundColor(requireContext().getColor(R.color.error));
            etReason.setHint("Reason for rejection / missing documents");
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            String remark = etReason.getText() != null ? etReason.getText().toString().trim() : "";
            if (!isApprove && remark.isEmpty()) {
                Toast.makeText(requireContext(), R.string.rejection_reason_required, Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.dismiss();
            if (isApprove) {
                adminViewModel.approveArtisan(artisan.getId(), remark.isEmpty() ? "Approved by NGO Admin" : remark);
            } else {
                adminViewModel.rejectArtisan(artisan.getId(), remark);
            }
        });

        dialog.show();
    }

    private void showArtisanDetailsSheet(ArtisanProfile artisan) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_artisan_action, null);
        dialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tvActionDialogTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvActionDialogSubtitle);
        View tilReason = view.findViewById(R.id.tilActionReason);
        tilReason.setVisibility(View.GONE);

        tvTitle.setText(artisan.getArtisanName() != null ? artisan.getArtisanName() : "Artisan Profile");
        StringBuilder info = new StringBuilder();
        info.append("SHG: ").append(artisan.getShgName() != null ? artisan.getShgName() : "N/A").append("\n");
        info.append("Location: ").append(artisan.getDistrict() != null ? artisan.getDistrict() + ", " : "").append(artisan.getState() != null ? artisan.getState() : "").append("\n");
        info.append("Experience: ").append(artisan.getExperience() != null ? artisan.getExperience() : 0).append(" years\n");
        info.append("Status: ").append(artisan.getApprovalStatus()).append("\n\n");
        info.append("Bio: \n").append(artisan.getBio() != null ? artisan.getBio() : "No bio provided.");
        tvSubtitle.setText(info.toString());
        tvSubtitle.setTextColor(requireContext().getColor(R.color.text_primary));

        MaterialButton btnCancel = view.findViewById(R.id.btnCancelAction);
        MaterialButton btnConfirm = view.findViewById(R.id.btnConfirmAction);
        btnCancel.setVisibility(View.GONE);
        btnConfirm.setText(R.string.btn_dialog_close);
        btnConfirm.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
