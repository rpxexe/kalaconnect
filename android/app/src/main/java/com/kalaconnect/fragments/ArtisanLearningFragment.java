package com.kalaconnect.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kalaconnect.R;
import com.kalaconnect.adapters.LearningModuleAdapter;
import com.kalaconnect.data.ArtisanLearningData;
import com.kalaconnect.models.ApiResponse;
import com.kalaconnect.models.CertificateItem;
import com.kalaconnect.models.LearningModule;
import com.kalaconnect.models.QuizQuestion;
import com.kalaconnect.network.ApiClient;
import com.kalaconnect.network.ApiService;
import com.kalaconnect.viewmodel.AuthViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtisanLearningFragment extends Fragment {

    // View Components
    private MaterialButtonToggleGroup toggleGroupLearning;
    private MaterialButton btnTabModules;
    private MaterialButton btnTabQuiz;
    private MaterialButton btnTabCertificate;

    private LinearLayout containerStudyModules;
    private LinearLayout containerKnowledgeQuiz;
    private LinearLayout containerCertificate;

    // Modules Tab
    private RecyclerView rvLearningModules;
    private LearningModuleAdapter moduleAdapter;
    private TextView tvDailyTipText;

    // Quiz Tab
    private TextView tvQuizProgressCounter;
    private TextView tvQuizDomainBadge;
    private LinearProgressIndicator progressQuizIndicator;
    private MaterialCardView cardQuizQuestion;
    private TextView tvQuizQuestionText;
    private RadioGroup rgQuizOptions;
    private RadioButton[] rbOptions;
    private LinearLayout layoutQuizExplanation;
    private TextView tvQuizResultHeader;
    private TextView tvQuizExplanationText;
    private MaterialButton btnQuizAction;

    private MaterialCardView cardQuizCompletion;
    private TextView tvQuizFinalScore;
    private TextView tvQuizCertificateTag;
    private MaterialButton btnViewCertificate;
    private MaterialButton btnRetakeQuiz;

    // Certificate Tab
    private MaterialCardView cardCertificateStatusNotice;
    private TextView tvCertificateStatusBadge;
    private TextView tvCertificateScoreBadge;
    private TextView tvCertificateStatusDescription;
    private MaterialCardView cardCertificateDisplay;
    private TextView tvCertificateArtisanName;

    // Quiz State
    private List<QuizQuestion> quizQuestions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int correctAnswersCount = 0;
    private boolean isAnswerVerified = false;

    private AuthViewModel authViewModel;
    private ApiService apiService;
    private CertificateItem currentCertificate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artisan_learning, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        apiService = ApiClient.getApiService(requireContext());

        initViews(view);
        setupTabs();
        setupModules();
        setupQuiz();
        setupCertificate();
    }

    private void initViews(View view) {
        toggleGroupLearning = view.findViewById(R.id.toggleGroupLearning);
        btnTabModules = view.findViewById(R.id.btnTabModules);
        btnTabQuiz = view.findViewById(R.id.btnTabQuiz);
        btnTabCertificate = view.findViewById(R.id.btnTabCertificate);

        containerStudyModules = view.findViewById(R.id.containerStudyModules);
        containerKnowledgeQuiz = view.findViewById(R.id.containerKnowledgeQuiz);
        containerCertificate = view.findViewById(R.id.containerCertificate);

        tvDailyTipText = view.findViewById(R.id.tvDailyTipText);
        rvLearningModules = view.findViewById(R.id.rvLearningModules);

        tvQuizProgressCounter = view.findViewById(R.id.tvQuizProgressCounter);
        tvQuizDomainBadge = view.findViewById(R.id.tvQuizDomainBadge);
        progressQuizIndicator = view.findViewById(R.id.progressQuizIndicator);
        cardQuizQuestion = view.findViewById(R.id.cardQuizQuestion);
        tvQuizQuestionText = view.findViewById(R.id.tvQuizQuestionText);
        rgQuizOptions = view.findViewById(R.id.rgQuizOptions);

        rbOptions = new RadioButton[4];
        rbOptions[0] = view.findViewById(R.id.rbOption0);
        rbOptions[1] = view.findViewById(R.id.rbOption1);
        rbOptions[2] = view.findViewById(R.id.rbOption2);
        rbOptions[3] = view.findViewById(R.id.rbOption3);

        layoutQuizExplanation = view.findViewById(R.id.layoutQuizExplanation);
        tvQuizResultHeader = view.findViewById(R.id.tvQuizResultHeader);
        tvQuizExplanationText = view.findViewById(R.id.tvQuizExplanationText);
        btnQuizAction = view.findViewById(R.id.btnQuizAction);

        cardQuizCompletion = view.findViewById(R.id.cardQuizCompletion);
        tvQuizFinalScore = view.findViewById(R.id.tvQuizFinalScore);
        tvQuizCertificateTag = view.findViewById(R.id.tvQuizCertificateTag);
        btnViewCertificate = view.findViewById(R.id.btnViewCertificate);
        btnRetakeQuiz = view.findViewById(R.id.btnRetakeQuiz);

        cardCertificateStatusNotice = view.findViewById(R.id.cardCertificateStatusNotice);
        tvCertificateStatusBadge = view.findViewById(R.id.tvCertificateStatusBadge);
        tvCertificateScoreBadge = view.findViewById(R.id.tvCertificateScoreBadge);
        tvCertificateStatusDescription = view.findViewById(R.id.tvCertificateStatusDescription);
        cardCertificateDisplay = view.findViewById(R.id.cardCertificateDisplay);
        tvCertificateArtisanName = view.findViewById(R.id.tvCertificateArtisanName);
    }

    private void setupTabs() {
        toggleGroupLearning.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnTabModules) {
                    containerStudyModules.setVisibility(View.VISIBLE);
                    containerKnowledgeQuiz.setVisibility(View.GONE);
                    containerCertificate.setVisibility(View.GONE);
                } else if (checkedId == R.id.btnTabQuiz) {
                    containerStudyModules.setVisibility(View.GONE);
                    containerKnowledgeQuiz.setVisibility(View.VISIBLE);
                    containerCertificate.setVisibility(View.GONE);
                } else if (checkedId == R.id.btnTabCertificate) {
                    containerStudyModules.setVisibility(View.GONE);
                    containerKnowledgeQuiz.setVisibility(View.GONE);
                    containerCertificate.setVisibility(View.VISIBLE);
                    fetchCertificateStatus();
                }
            }
        });
    }

    private void setupModules() {
        tvDailyTipText.setText(ArtisanLearningData.getDailyCraftTip());

        moduleAdapter = new LearningModuleAdapter();
        rvLearningModules.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvLearningModules.setAdapter(moduleAdapter);
        moduleAdapter.setOnModuleClickListener(this::openModuleDetailDialog);

        // Fetch dynamic learning modules from backend API managed by admin
        apiService.getLearningModules().enqueue(new Callback<ApiResponse<List<LearningModule>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<LearningModule>>> call, Response<ApiResponse<List<LearningModule>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null && !response.body().getData().isEmpty()) {
                    moduleAdapter.setModules(response.body().getData());
                } else {
                    moduleAdapter.setModules(ArtisanLearningData.getLearningModules());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<LearningModule>>> call, Throwable t) {
                moduleAdapter.setModules(ArtisanLearningData.getLearningModules());
            }
        });
    }

    private void openModuleDetailDialog(LearningModule module) {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_learning_module_detail, null);
        dialog.setContentView(dialogView);

        TextView tvCategory = dialogView.findViewById(R.id.tvDetailCategory);
        TextView tvDuration = dialogView.findViewById(R.id.tvDetailDuration);
        TextView tvTitle = dialogView.findViewById(R.id.tvDetailTitle);
        TextView tvSummary = dialogView.findViewById(R.id.tvDetailSummary);
        TextView tvTip = dialogView.findViewById(R.id.tvDetailTip);
        TextView tvKeyPoints = dialogView.findViewById(R.id.tvDetailKeyPoints);
        TextView tvContent = dialogView.findViewById(R.id.tvDetailContent);
        ImageButton btnClose = dialogView.findViewById(R.id.btnDetailClose);
        MaterialButton btnDone = dialogView.findViewById(R.id.btnDetailDone);

        tvCategory.setText(module.getCategory());
        tvDuration.setText(module.getDuration());
        tvTitle.setText(module.getTitle());
        tvSummary.setText(module.getSummary());
        tvTip.setText(module.getPracticalTip());

        StringBuilder sbPoints = new StringBuilder();
        if (module.getKeyPoints() != null) {
            for (String pt : module.getKeyPoints()) {
                sbPoints.append("• ").append(pt).append("\n\n");
            }
        }
        tvKeyPoints.setText(sbPoints.toString().trim());
        tvContent.setText(module.getDetailedContent());

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnDone.setOnClickListener(v -> {
            module.setCompleted(true);
            moduleAdapter.notifyDataSetChanged();
            dialog.dismiss();
            Toast.makeText(requireContext(), "Module completed! Keep studying to unlock certification.", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void setupQuiz() {
        quizQuestions = ArtisanLearningData.getQuizQuestions();
        currentQuestionIndex = 0;
        correctAnswersCount = 0;
        isAnswerVerified = false;

        loadQuestion(currentQuestionIndex);

        btnQuizAction.setOnClickListener(v -> {
            if (!isAnswerVerified) {
                verifyCurrentAnswer();
            } else {
                moveToNextQuestion();
            }
        });

        btnRetakeQuiz.setOnClickListener(v -> {
            currentQuestionIndex = 0;
            correctAnswersCount = 0;
            isAnswerVerified = false;
            cardQuizCompletion.setVisibility(View.GONE);
            cardQuizQuestion.setVisibility(View.VISIBLE);
            loadQuestion(0);
        });

        btnViewCertificate.setOnClickListener(v -> {
            toggleGroupLearning.check(R.id.btnTabCertificate);
        });
    }

    private void loadQuestion(int index) {
        if (index < 0 || index >= quizQuestions.size()) return;

        QuizQuestion q = quizQuestions.get(index);
        isAnswerVerified = false;

        tvQuizProgressCounter.setText(String.format(Locale.getDefault(), "Question %d of %d", (index + 1), quizQuestions.size()));
        tvQuizDomainBadge.setText(q.getKnowledgeDomain() != null ? q.getKnowledgeDomain().toUpperCase() : "GENERAL");

        int progress = (int) (((float) (index) / quizQuestions.size()) * 100);
        progressQuizIndicator.setProgress(progress);

        tvQuizQuestionText.setText(q.getQuestion());

        rgQuizOptions.clearCheck();
        for (int i = 0; i < rbOptions.length; i++) {
            if (i < q.getOptions().size()) {
                rbOptions[i].setVisibility(View.VISIBLE);
                rbOptions[i].setText(q.getOptions().get(i));
                rbOptions[i].setEnabled(true);
            } else {
                rbOptions[i].setVisibility(View.GONE);
            }
        }

        layoutQuizExplanation.setVisibility(View.GONE);
        btnQuizAction.setText("Verify Answer");
        btnQuizAction.setIconResource(R.drawable.ic_check);
    }

    private void verifyCurrentAnswer() {
        int checkedId = rgQuizOptions.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(requireContext(), "Please select an answer to continue.", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedIndex = -1;
        for (int i = 0; i < rbOptions.length; i++) {
            if (rbOptions[i].getId() == checkedId) {
                selectedIndex = i;
                break;
            }
        }

        QuizQuestion q = quizQuestions.get(currentQuestionIndex);
        q.setSelectedOptionIndex(selectedIndex);

        boolean isCorrect = (selectedIndex == q.getCorrectOptionIndex());
        if (isCorrect) {
            correctAnswersCount++;
            tvQuizResultHeader.setText("✓ Correct! Master Knowledge Verified");
            tvQuizResultHeader.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_success));
            layoutQuizExplanation.setBackgroundResource(R.drawable.bg_status_badge_success);
        } else {
            tvQuizResultHeader.setText("✕ Key Insight to Remember");
            tvQuizResultHeader.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_error));
            layoutQuizExplanation.setBackgroundResource(R.drawable.bg_status_badge_error);
        }

        tvQuizExplanationText.setText(q.getExplanation());
        layoutQuizExplanation.setVisibility(View.VISIBLE);

        for (RadioButton rb : rbOptions) {
            rb.setEnabled(false);
        }

        isAnswerVerified = true;
        if (currentQuestionIndex == quizQuestions.size() - 1) {
            btnQuizAction.setText("View Assessment Results");
        } else {
            btnQuizAction.setText("Next Question");
        }
        btnQuizAction.setIconResource(R.drawable.ic_arrow_forward);
    }

    private void moveToNextQuestion() {
        if (currentQuestionIndex < quizQuestions.size() - 1) {
            currentQuestionIndex++;
            loadQuestion(currentQuestionIndex);
        } else {
            showQuizCompletion();
        }
    }

    private void showQuizCompletion() {
        progressQuizIndicator.setProgress(100);
        cardQuizQuestion.setVisibility(View.GONE);
        cardQuizCompletion.setVisibility(View.VISIBLE);

        int total = quizQuestions.size();
        int percent = (int) (((float) correctAnswersCount / total) * 100);

        tvQuizFinalScore.setText(correctAnswersCount + " / " + total + " (" + percent + "%)");

        String artisanName = authViewModel.getLoggedInUserName();
        if (artisanName == null || artisanName.trim().isEmpty()) {
            artisanName = "Artisan";
        }
        Long artisanId = authViewModel.getLoggedInUserId();

        if (percent >= 60) {
            tvQuizCertificateTag.setText("⏳ Submitted for Admin Approval");
            tvQuizCertificateTag.setBackgroundResource(R.drawable.bg_status_badge_pending);
            tvQuizCertificateTag.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_pending));

            // Submit result to backend for Admin Approval
            CertificateItem certReq = new CertificateItem(
                    artisanId,
                    artisanName,
                    "Self-Help Group Cluster",
                    correctAnswersCount,
                    total,
                    percent
            );

            apiService.submitCertificate(certReq).enqueue(new Callback<ApiResponse<CertificateItem>>() {
                @Override
                public void onResponse(Call<ApiResponse<CertificateItem>> call, Response<ApiResponse<CertificateItem>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        currentCertificate = response.body().getData();
                        Toast.makeText(requireContext(), "Assessment submitted! Certificate is pending NGO Admin approval.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<CertificateItem>> call, Throwable t) {
                    // Handled gracefully
                }
            });
        } else {
            tvQuizCertificateTag.setText("Artisan Scholar • Keep Studying");
            tvQuizCertificateTag.setBackgroundResource(R.drawable.bg_badge_primary_light);
            tvQuizCertificateTag.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
        }
    }

    private void setupCertificate() {
        String name = authViewModel.getLoggedInUserName();
        if (name != null && !name.trim().isEmpty()) {
            tvCertificateArtisanName.setText(name);
        } else {
            tvCertificateArtisanName.setText("Meera Bai");
        }

        fetchCertificateStatus();
    }

    private void fetchCertificateStatus() {
        Long artisanId = authViewModel.getLoggedInUserId();
        apiService.getMyCertificateStatus(artisanId).enqueue(new Callback<ApiResponse<CertificateItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<CertificateItem>> call, Response<ApiResponse<CertificateItem>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    currentCertificate = response.body().getData();
                    bindCertificateStatus(currentCertificate);
                } else {
                    bindCertificateStatus(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CertificateItem>> call, Throwable t) {
                bindCertificateStatus(null);
            }
        });
    }

    private void bindCertificateStatus(@Nullable CertificateItem cert) {
        if (cert == null) {
            tvCertificateStatusBadge.setText("ℹ ASSESSMENT REQUIRED");
            tvCertificateStatusBadge.setBackgroundResource(R.drawable.bg_badge_primary_light);
            tvCertificateStatusBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
            tvCertificateScoreBadge.setText("Score: Pending Quiz");
            tvCertificateStatusDescription.setText("Take the Knowledge Assessment quiz in the 'Quiz' tab to earn your accredited KalaConnect Certificate.");
            if (cardCertificateDisplay != null) cardCertificateDisplay.setAlpha(0.35f);
            return;
        }

        tvCertificateScoreBadge.setText("Score: " + cert.getScore() + "/" + cert.getTotalQuestions() + " (" + cert.getPercentage() + "%)");

        if (cert.isApproved()) {
            tvCertificateStatusBadge.setText("✓ APPROVED BY ADMIN");
            tvCertificateStatusBadge.setBackgroundResource(R.drawable.bg_status_badge_success);
            tvCertificateStatusBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_success));

            String approver = cert.getApprovedBy() != null ? cert.getApprovedBy() : "NGO Admin";
            tvCertificateStatusDescription.setText("Congratulations! Your certificate of artisan excellence has been verified and issued by " + approver + ". Your verified artisan credentials are live!");
            if (cardCertificateDisplay != null) cardCertificateDisplay.setAlpha(1.0f);
        } else if (cert.isRejected()) {
            tvCertificateStatusBadge.setText("✕ NEEDS IMPROVEMENT");
            tvCertificateStatusBadge.setBackgroundResource(R.drawable.bg_status_badge_error);
            tvCertificateStatusBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_error));

            String notes = cert.getAdminNotes() != null ? cert.getAdminNotes() : "Please review the study modules and re-attempt the quiz.";
            tvCertificateStatusDescription.setText("Admin Feedback: " + notes);
            if (cardCertificateDisplay != null) cardCertificateDisplay.setAlpha(0.35f);
        } else {
            tvCertificateStatusBadge.setText("⏳ PENDING ADMIN APPROVAL");
            tvCertificateStatusBadge.setBackgroundResource(R.drawable.bg_status_badge_pending);
            tvCertificateStatusBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_pending));
            tvCertificateStatusDescription.setText("Your quiz result (" + cert.getPercentage() + "%) is currently under review by the NGO Administrator. Once verified, your certificate of mastery will be unlocked here.");
            if (cardCertificateDisplay != null) cardCertificateDisplay.setAlpha(0.35f);
        }
    }
}
