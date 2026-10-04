package com.kalaconnect.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kalaconnect.R;
import com.kalaconnect.adapters.AdminCertificateAdapter;
import com.kalaconnect.adapters.AdminTopicAdapter;
import com.kalaconnect.models.ApiResponse;
import com.kalaconnect.models.CertificateItem;
import com.kalaconnect.models.LearningModule;
import com.kalaconnect.network.ApiClient;
import com.kalaconnect.network.ApiService;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminLearningFragment extends Fragment {

    private MaterialToolbar toolbarAdminLearning;
    private MaterialButtonToggleGroup toggleGroupAdminLearning;
    private MaterialButton btnTabAdminTopics, btnTabAdminCertificates;
    private LinearProgressIndicator progressAdminLoading;

    // Topics Tab
    private LinearLayout containerAdminTopics;
    private TextView tvAdminTopicsCount;
    private MaterialButton btnAdminAddTopic;
    private RecyclerView rvAdminTopics;
    private AdminTopicAdapter topicAdapter;
    private List<LearningModule> allModules = new ArrayList<>();

    // Certificates Tab
    private LinearLayout containerAdminCertificates;
    private ChipGroup chipGroupCertFilter;
    private TextView tvAdminCertCount;
    private RecyclerView rvAdminCertificates;
    private LinearLayout layoutCertEmpty;
    private AdminCertificateAdapter certificateAdapter;
    private List<CertificateItem> allCertificates = new ArrayList<>();

    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_learning, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getApiService(requireContext());

        initViews(view);
        setupToolbar();
        setupToggleGroup();
        setupTopicsTab();
        setupCertificatesTab();

        loadTopics();
        loadCertificates(null);
    }

    private void initViews(View view) {
        toolbarAdminLearning = view.findViewById(R.id.toolbarAdminLearning);
        toggleGroupAdminLearning = view.findViewById(R.id.toggleGroupAdminLearning);
        btnTabAdminTopics = view.findViewById(R.id.btnTabAdminTopics);
        btnTabAdminCertificates = view.findViewById(R.id.btnTabAdminCertificates);
        progressAdminLoading = view.findViewById(R.id.progressAdminLoading);

        containerAdminTopics = view.findViewById(R.id.containerAdminTopics);
        tvAdminTopicsCount = view.findViewById(R.id.tvAdminTopicsCount);
        btnAdminAddTopic = view.findViewById(R.id.btnAdminAddTopic);
        rvAdminTopics = view.findViewById(R.id.rvAdminTopics);

        containerAdminCertificates = view.findViewById(R.id.containerAdminCertificates);
        chipGroupCertFilter = view.findViewById(R.id.chipGroupCertFilter);
        tvAdminCertCount = view.findViewById(R.id.tvAdminCertCount);
        rvAdminCertificates = view.findViewById(R.id.rvAdminCertificates);
        layoutCertEmpty = view.findViewById(R.id.layoutCertEmpty);
    }

    private void setupToolbar() {
        toolbarAdminLearning.setNavigationOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void setupToggleGroup() {
        toggleGroupAdminLearning.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.btnTabAdminTopics) {
                containerAdminTopics.setVisibility(View.VISIBLE);
                containerAdminCertificates.setVisibility(View.GONE);
            } else if (checkedId == R.id.btnTabAdminCertificates) {
                containerAdminTopics.setVisibility(View.GONE);
                containerAdminCertificates.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupTopicsTab() {
        topicAdapter = new AdminTopicAdapter(new AdminTopicAdapter.OnTopicActionListener() {
            @Override
            public void onPreviewTopic(LearningModule module) {
                showTopicPreviewDialog(module);
            }

            @Override
            public void onDeleteTopic(LearningModule module) {
                deleteTopic(module);
            }
        });

        rvAdminTopics.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvAdminTopics.setAdapter(topicAdapter);

        btnAdminAddTopic.setOnClickListener(v -> showAddTopicDialog());
    }

    private void setupCertificatesTab() {
        certificateAdapter = new AdminCertificateAdapter(new AdminCertificateAdapter.OnCertificateActionListener() {
            @Override
            public void onApproveCertificate(CertificateItem item) {
                approveCertificate(item);
            }

            @Override
            public void onRejectCertificate(CertificateItem item) {
                showRejectDialog(item);
            }
        });

        rvAdminCertificates.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvAdminCertificates.setAdapter(certificateAdapter);

        chipGroupCertFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipFilterAll) {
                filterCertificates(null);
            } else if (checkedId == R.id.chipFilterPending) {
                filterCertificates("PENDING_APPROVAL");
            } else if (checkedId == R.id.chipFilterApproved) {
                filterCertificates("APPROVED");
            } else if (checkedId == R.id.chipFilterRejected) {
                filterCertificates("REJECTED");
            }
        });
    }

    private void loadTopics() {
        progressAdminLoading.setVisibility(View.VISIBLE);
        apiService.getLearningModules().enqueue(new Callback<ApiResponse<List<LearningModule>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<LearningModule>>> call, Response<ApiResponse<List<LearningModule>>> response) {
                progressAdminLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    allModules = response.body().getData();
                    topicAdapter.setModules(allModules);
                    tvAdminTopicsCount.setText(allModules.size() + " Active Learning Topics");
                    btnTabAdminTopics.setText("Learning Topics (" + allModules.size() + ")");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<LearningModule>>> call, Throwable t) {
                progressAdminLoading.setVisibility(View.GONE);
            }
        });
    }

    private void loadCertificates(@Nullable String statusFilter) {
        progressAdminLoading.setVisibility(View.VISIBLE);
        apiService.getAllCertificates(statusFilter).enqueue(new Callback<ApiResponse<List<CertificateItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CertificateItem>>> call, Response<ApiResponse<List<CertificateItem>>> response) {
                progressAdminLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    allCertificates = response.body().getData();
                    filterCertificates(statusFilter);
                    btnTabAdminCertificates.setText("Certificates (" + allCertificates.size() + ")");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CertificateItem>>> call, Throwable t) {
                progressAdminLoading.setVisibility(View.GONE);
            }
        });
    }

    private void filterCertificates(@Nullable String status) {
        if (allCertificates == null) return;
        List<CertificateItem> filtered = new ArrayList<>();
        for (CertificateItem item : allCertificates) {
            if (status == null || status.equalsIgnoreCase(item.getStatus())) {
                filtered.add(item);
            }
        }

        certificateAdapter.setCertificates(filtered);
        tvAdminCertCount.setText("Showing " + filtered.size() + " submissions");
        layoutCertEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showAddTopicDialog() {
        Dialog dialog = new Dialog(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_admin_add_topic, null);
        dialog.setContentView(dialogView);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextInputEditText etTopicTitle = dialogView.findViewById(R.id.etTopicTitle);
        TextInputEditText etTopicCategory = dialogView.findViewById(R.id.etTopicCategory);
        TextInputEditText etTopicDuration = dialogView.findViewById(R.id.etTopicDuration);
        TextInputEditText etTopicBadge = dialogView.findViewById(R.id.etTopicBadge);
        TextInputEditText etTopicReadTime = dialogView.findViewById(R.id.etTopicReadTime);
        TextInputEditText etTopicSummary = dialogView.findViewById(R.id.etTopicSummary);
        TextInputEditText etTopicKeyPoints = dialogView.findViewById(R.id.etTopicKeyPoints);
        TextInputEditText etTopicContent = dialogView.findViewById(R.id.etTopicContent);
        TextInputEditText etTopicTip = dialogView.findViewById(R.id.etTopicTip);

        dialogView.findViewById(R.id.btnCancelAddTopic).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btnSubmitTopic).setOnClickListener(v -> {
            String title = etTopicTitle.getText() != null ? etTopicTitle.getText().toString().trim() : "";
            String category = etTopicCategory.getText() != null ? etTopicCategory.getText().toString().trim() : "Business";
            String duration = etTopicDuration.getText() != null ? etTopicDuration.getText().toString().trim() : "15 mins";
            String badge = etTopicBadge.getText() != null ? etTopicBadge.getText().toString().trim() : "Verified";
            String readTime = etTopicReadTime.getText() != null ? etTopicReadTime.getText().toString().trim() : "4 min read";
            String summary = etTopicSummary.getText() != null ? etTopicSummary.getText().toString().trim() : "";
            String rawPoints = etTopicKeyPoints.getText() != null ? etTopicKeyPoints.getText().toString().trim() : "";
            String content = etTopicContent.getText() != null ? etTopicContent.getText().toString().trim() : "";
            String tip = etTopicTip.getText() != null ? etTopicTip.getText().toString().trim() : "";

            if (TextUtils.isEmpty(title)) {
                etTopicTitle.setError("Title is required");
                return;
            }
            if (TextUtils.isEmpty(summary)) {
                etTopicSummary.setError("Summary is required");
                return;
            }
            if (TextUtils.isEmpty(content)) {
                etTopicContent.setError("Detailed content is required");
                return;
            }

            List<String> pointsList = new ArrayList<>();
            if (!TextUtils.isEmpty(rawPoints)) {
                for (String p : rawPoints.split("\\r?\\n")) {
                    if (!p.trim().isEmpty()) {
                        pointsList.add(p.trim());
                    }
                }
            }

            LearningModule newModule = new LearningModule(
                    "mod_" + System.currentTimeMillis(),
                    title,
                    summary,
                    category,
                    duration,
                    readTime,
                    badge,
                    summary,
                    pointsList,
                    content,
                    tip
            );

            progressAdminLoading.setVisibility(View.VISIBLE);
            apiService.createLearningModule(newModule).enqueue(new Callback<ApiResponse<LearningModule>>() {
                @Override
                public void onResponse(Call<ApiResponse<LearningModule>> call, Response<ApiResponse<LearningModule>> response) {
                    progressAdminLoading.setVisibility(View.GONE);
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Topic published successfully!", Toast.LENGTH_SHORT).show();
                    loadTopics();
                }

                @Override
                public void onFailure(Call<ApiResponse<LearningModule>> call, Throwable t) {
                    progressAdminLoading.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Failed to publish topic: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void deleteTopic(LearningModule module) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Learning Topic")
                .setMessage("Are you sure you want to remove \"" + module.getTitle() + "\" from the artisan curriculum?")
                .setPositiveButton("Delete", (d, which) -> {
                    progressAdminLoading.setVisibility(View.VISIBLE);
                    apiService.deleteLearningModule(module.getId()).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            progressAdminLoading.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Topic removed", Toast.LENGTH_SHORT).show();
                            loadTopics();
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            progressAdminLoading.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Error deleting: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showTopicPreviewDialog(LearningModule module) {
        Dialog dialog = new Dialog(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_learning_module_detail, null);
        dialog.setContentView(dialogView);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvCategory = dialogView.findViewById(R.id.tvDetailCategory);
        TextView tvDuration = dialogView.findViewById(R.id.tvDetailDuration);
        TextView tvTitle = dialogView.findViewById(R.id.tvDetailTitle);
        TextView tvSummary = dialogView.findViewById(R.id.tvDetailSummary);
        TextView tvKeyPoints = dialogView.findViewById(R.id.tvDetailKeyPoints);
        TextView tvContent = dialogView.findViewById(R.id.tvDetailContent);
        TextView tvTip = dialogView.findViewById(R.id.tvDetailTip);
        ImageButton btnClose = dialogView.findViewById(R.id.btnDetailClose);
        MaterialButton btnGotIt = dialogView.findViewById(R.id.btnDetailDone);

        if (tvCategory != null) tvCategory.setText(module.getCategory() != null ? module.getCategory().toUpperCase() : "GENERAL");
        if (tvDuration != null) tvDuration.setText(module.getDuration() != null ? module.getDuration() : "15 mins");
        if (tvTitle != null) tvTitle.setText(module.getTitle());
        if (tvSummary != null) tvSummary.setText(module.getSummary());
        if (tvContent != null) tvContent.setText(module.getDetailedContent());
        if (tvTip != null) tvTip.setText(module.getPracticalTip() != null ? module.getPracticalTip() : "Always maintain consistency and genuine handcraft excellence.");

        if (tvKeyPoints != null && module.getKeyPoints() != null) {
            StringBuilder sb = new StringBuilder();
            for (String point : module.getKeyPoints()) {
                sb.append("• ").append(point).append("\n");
            }
            tvKeyPoints.setText(sb.toString().trim());
        }

        if (btnClose != null) btnClose.setOnClickListener(v -> dialog.dismiss());
        if (btnGotIt != null) btnGotIt.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void approveCertificate(CertificateItem item) {
        progressAdminLoading.setVisibility(View.VISIBLE);
        apiService.approveCertificate(item.getId()).enqueue(new Callback<ApiResponse<CertificateItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<CertificateItem>> call, Response<ApiResponse<CertificateItem>> response) {
                progressAdminLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), "Certificate approved for " + item.getArtisanName() + "!", Toast.LENGTH_LONG).show();
                    loadCertificates(null);
                } else {
                    Toast.makeText(requireContext(), "Failed to approve certificate", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CertificateItem>> call, Throwable t) {
                progressAdminLoading.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRejectDialog(CertificateItem item) {
        EditText input = new EditText(requireContext());
        input.setHint("Enter feedback note for artisan (e.g. Please re-read Module 2 and retry)");
        input.setPadding(32, 24, 32, 24);

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Reject Certificate Application")
                .setMessage("Provide guidance so the artisan can improve before re-attempting:")
                .setView(input)
                .setPositiveButton("Reject Application", (d, which) -> {
                    String notes = input.getText().toString().trim();
                    Map<String, String> body = new HashMap<>();
                    body.put("notes", notes.isEmpty() ? "Please review the learning modules and re-attempt the assessment." : notes);

                    progressAdminLoading.setVisibility(View.VISIBLE);
                    apiService.rejectCertificate(item.getId(), body).enqueue(new Callback<ApiResponse<CertificateItem>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<CertificateItem>> call, Response<ApiResponse<CertificateItem>> response) {
                            progressAdminLoading.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Application rejected with feedback", Toast.LENGTH_SHORT).show();
                            loadCertificates(null);
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<CertificateItem>> call, Throwable t) {
                            progressAdminLoading.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
