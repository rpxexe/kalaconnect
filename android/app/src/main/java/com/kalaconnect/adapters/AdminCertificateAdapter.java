package com.kalaconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kalaconnect.R;
import com.kalaconnect.models.CertificateItem;

import java.util.ArrayList;
import java.util.List;

public class AdminCertificateAdapter extends RecyclerView.Adapter<AdminCertificateAdapter.CertViewHolder> {

    public interface OnCertificateActionListener {
        void onApproveCertificate(CertificateItem item);
        void onRejectCertificate(CertificateItem item);
    }

    private final List<CertificateItem> certificates = new ArrayList<>();
    private final OnCertificateActionListener listener;

    public AdminCertificateAdapter(OnCertificateActionListener listener) {
        this.listener = listener;
    }

    public void setCertificates(List<CertificateItem> list) {
        this.certificates.clear();
        if (list != null) {
            this.certificates.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_certificate, parent, false);
        return new CertViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CertViewHolder holder, int position) {
        holder.bind(certificates.get(position));
    }

    @Override
    public int getItemCount() {
        return certificates.size();
    }

    class CertViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCertArtisanName;
        private final TextView tvCertShgName;
        private final TextView tvCertStatus;
        private final TextView tvCertScore;
        private final TextView tvCertDate;
        private final TextView tvCertAdminNotes;
        private final LinearLayout layoutCertActions;
        private final MaterialButton btnApproveCert;
        private final MaterialButton btnRejectCert;

        public CertViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCertArtisanName = itemView.findViewById(R.id.tvCertArtisanName);
            tvCertShgName = itemView.findViewById(R.id.tvCertShgName);
            tvCertStatus = itemView.findViewById(R.id.tvCertStatus);
            tvCertScore = itemView.findViewById(R.id.tvCertScore);
            tvCertDate = itemView.findViewById(R.id.tvCertDate);
            tvCertAdminNotes = itemView.findViewById(R.id.tvCertAdminNotes);
            layoutCertActions = itemView.findViewById(R.id.layoutCertActions);
            btnApproveCert = itemView.findViewById(R.id.btnApproveCert);
            btnRejectCert = itemView.findViewById(R.id.btnRejectCert);
        }

        public void bind(CertificateItem item) {
            tvCertArtisanName.setText(item.getArtisanName() != null ? item.getArtisanName() : "Master Artisan");
            tvCertShgName.setText(item.getShgName() != null ? item.getShgName() : "Self-Help Group Cluster");

            String scoreText = item.getScore() + " / " + item.getTotalQuestions() + " (" + item.getPercentage() + "%)";
            tvCertScore.setText(scoreText);

            String created = item.getCreatedAt();
            if (created != null && created.length() >= 10) {
                created = created.substring(0, 10);
            }
            tvCertDate.setText(created != null ? "Date: " + created : "Recently submitted");

            if (item.isApproved()) {
                tvCertStatus.setText("APPROVED");
                tvCertStatus.setBackgroundResource(R.drawable.bg_status_badge_success);
                tvCertStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_success));
                layoutCertActions.setVisibility(View.GONE);

                String approvedBy = item.getApprovedBy() != null ? item.getApprovedBy() : "NGO Admin";
                tvCertAdminNotes.setVisibility(View.VISIBLE);
                tvCertAdminNotes.setText("✓ Verified and Issued by " + approvedBy);
            } else if (item.isRejected()) {
                tvCertStatus.setText("REJECTED");
                tvCertStatus.setBackgroundResource(R.drawable.bg_status_badge_error);
                tvCertStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_error));
                layoutCertActions.setVisibility(View.GONE);

                tvCertAdminNotes.setVisibility(View.VISIBLE);
                String notes = item.getAdminNotes() != null ? item.getAdminNotes() : "Score requires improvement.";
                tvCertAdminNotes.setText("✕ Feedback: " + notes);
            } else {
                tvCertStatus.setText("PENDING APPROVAL");
                tvCertStatus.setBackgroundResource(R.drawable.bg_status_badge_pending);
                tvCertStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_pending));
                layoutCertActions.setVisibility(View.VISIBLE);
                tvCertAdminNotes.setVisibility(View.GONE);

                btnApproveCert.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onApproveCertificate(item);
                    }
                });

                btnRejectCert.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onRejectCertificate(item);
                    }
                });
            }
        }
    }
}
