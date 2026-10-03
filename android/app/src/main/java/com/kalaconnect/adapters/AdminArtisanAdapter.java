package com.kalaconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.kalaconnect.R;
import com.kalaconnect.models.ArtisanProfile;

import java.util.ArrayList;
import java.util.List;

public class AdminArtisanAdapter extends RecyclerView.Adapter<AdminArtisanAdapter.ArtisanViewHolder> {

    public interface OnArtisanActionListener {
        void onApprove(ArtisanProfile artisan);
        void onReject(ArtisanProfile artisan);
        void onViewDetails(ArtisanProfile artisan);
    }

    private final List<ArtisanProfile> artisans = new ArrayList<>();
    private final OnArtisanActionListener listener;

    public AdminArtisanAdapter(OnArtisanActionListener listener) {
        this.listener = listener;
    }

    public void setArtisans(List<ArtisanProfile> items) {
        this.artisans.clear();
        if (items != null) {
            this.artisans.addAll(items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtisanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_artisan, parent, false);
        return new ArtisanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtisanViewHolder holder, int position) {
        holder.bind(artisans.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return artisans.size();
    }

    static class ArtisanViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivAdminArtisanPhoto;
        private final TextView tvAdminArtisanName;
        private final TextView tvAdminShgName;
        private final TextView tvAdminApprovalStatus;
        private final TextView tvAdminArtisanLocation;
        private final TextView tvAdminArtisanExperience;
        private final TextView tvAdminArtisanSkills;
        private final TextView tvAdminArtisanBio;
        private final MaterialButton btnAdminViewArtisan;
        private final MaterialButton btnAdminRejectArtisan;
        private final MaterialButton btnAdminApproveArtisan;

        public ArtisanViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAdminArtisanPhoto = itemView.findViewById(R.id.ivAdminArtisanPhoto);
            tvAdminArtisanName = itemView.findViewById(R.id.tvAdminArtisanName);
            tvAdminShgName = itemView.findViewById(R.id.tvAdminShgName);
            tvAdminApprovalStatus = itemView.findViewById(R.id.tvAdminApprovalStatus);
            tvAdminArtisanLocation = itemView.findViewById(R.id.tvAdminArtisanLocation);
            tvAdminArtisanExperience = itemView.findViewById(R.id.tvAdminArtisanExperience);
            tvAdminArtisanSkills = itemView.findViewById(R.id.tvAdminArtisanSkills);
            tvAdminArtisanBio = itemView.findViewById(R.id.tvAdminArtisanBio);
            btnAdminViewArtisan = itemView.findViewById(R.id.btnAdminViewArtisan);
            btnAdminRejectArtisan = itemView.findViewById(R.id.btnAdminRejectArtisan);
            btnAdminApproveArtisan = itemView.findViewById(R.id.btnAdminApproveArtisan);
        }

        public void bind(ArtisanProfile item, OnArtisanActionListener listener) {
            tvAdminArtisanName.setText(item.getArtisanName() != null ? item.getArtisanName() : "Artisan");
            tvAdminShgName.setText(item.getShgName() != null && !item.getShgName().isBlank() ? item.getShgName() : "Individual Artisan");

            StringBuilder loc = new StringBuilder();
            if (item.getDistrict() != null && !item.getDistrict().isBlank()) loc.append(item.getDistrict());
            if (item.getState() != null && !item.getState().isBlank()) {
                if (loc.length() > 0) loc.append(", ");
                loc.append(item.getState());
            } else if (item.getVillageCity() != null && !item.getVillageCity().isBlank()) {
                if (loc.length() > 0) loc.append(", ");
                loc.append(item.getVillageCity());
            }
            tvAdminArtisanLocation.setText(loc.length() > 0 ? loc.toString() : "Location not specified");

            int exp = item.getExperience() != null ? item.getExperience() : 0;
            tvAdminArtisanExperience.setText(exp > 0 ? exp + "+ years exp" : "New artisan");

            if (item.getSkills() != null && !item.getSkills().isEmpty()) {
                tvAdminArtisanSkills.setText("Skills: " + String.join(", ", item.getSkills()));
                tvAdminArtisanSkills.setVisibility(View.VISIBLE);
            } else {
                tvAdminArtisanSkills.setVisibility(View.GONE);
            }

            if (item.getBio() != null && !item.getBio().isBlank()) {
                tvAdminArtisanBio.setText(item.getBio());
                tvAdminArtisanBio.setVisibility(View.VISIBLE);
            } else {
                tvAdminArtisanBio.setVisibility(View.GONE);
            }

            String status = item.getApprovalStatus() != null ? item.getApprovalStatus().toUpperCase() : "PENDING";
            tvAdminApprovalStatus.setText(status);

            if ("APPROVED".equals(status)) {
                tvAdminApprovalStatus.setBackgroundResource(R.drawable.bg_status_badge_success);
                tvAdminApprovalStatus.setTextColor(itemView.getContext().getColor(R.color.badge_success_text));
                btnAdminApproveArtisan.setVisibility(View.GONE);
                btnAdminRejectArtisan.setVisibility(View.VISIBLE);
                btnAdminRejectArtisan.setText("Revoke");
            } else if ("REJECTED".equals(status)) {
                tvAdminApprovalStatus.setBackgroundResource(R.drawable.bg_status_badge_error);
                tvAdminApprovalStatus.setTextColor(itemView.getContext().getColor(R.color.error));
                btnAdminApproveArtisan.setVisibility(View.VISIBLE);
                btnAdminApproveArtisan.setText("Approve");
                btnAdminRejectArtisan.setVisibility(View.GONE);
            } else {
                tvAdminApprovalStatus.setBackgroundResource(R.drawable.bg_status_badge_pending);
                tvAdminApprovalStatus.setTextColor(itemView.getContext().getColor(R.color.status_pending));
                btnAdminApproveArtisan.setVisibility(View.VISIBLE);
                btnAdminApproveArtisan.setText("Approve");
                btnAdminRejectArtisan.setVisibility(View.VISIBLE);
                btnAdminRejectArtisan.setText("Reject");
            }

            if (item.getProfilePhoto() != null && !item.getProfilePhoto().isBlank()) {
                Glide.with(itemView.getContext())
                        .load(item.getProfilePhoto())
                        .placeholder(R.drawable.ic_person)
                        .circleCrop()
                        .into(ivAdminArtisanPhoto);
            } else {
                ivAdminArtisanPhoto.setImageResource(R.drawable.ic_person);
            }

            btnAdminApproveArtisan.setOnClickListener(v -> {
                if (listener != null) listener.onApprove(item);
            });

            btnAdminRejectArtisan.setOnClickListener(v -> {
                if (listener != null) listener.onReject(item);
            });

            btnAdminViewArtisan.setOnClickListener(v -> {
                if (listener != null) listener.onViewDetails(item);
            });
        }
    }
}
