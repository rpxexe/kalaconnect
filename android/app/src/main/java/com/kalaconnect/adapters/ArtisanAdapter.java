package com.kalaconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.kalaconnect.R;
import com.kalaconnect.models.Artisan;

import java.util.ArrayList;
import java.util.List;

public class ArtisanAdapter extends RecyclerView.Adapter<ArtisanAdapter.ArtisanViewHolder> {

    public interface OnArtisanClickListener {
        void onArtisanClick(Artisan artisan);
    }

    private final List<Artisan> artisans = new ArrayList<>();
    private OnArtisanClickListener listener;

    public void setArtisans(List<Artisan> newArtisans) {
        this.artisans.clear();
        if (newArtisans != null) {
            this.artisans.addAll(newArtisans);
        }
        notifyDataSetChanged();
    }

    public void setArtisanProfiles(List<com.kalaconnect.models.ArtisanProfile> profiles) {
        this.artisans.clear();
        if (profiles != null) {
            for (com.kalaconnect.models.ArtisanProfile p : profiles) {
                this.artisans.add(Artisan.fromProfile(p));
            }
        }
        notifyDataSetChanged();
    }

    public void setOnArtisanClickListener(OnArtisanClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArtisanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artisan_card, parent, false);
        return new ArtisanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtisanViewHolder holder, int position) {
        holder.bind(artisans.get(position));
    }

    @Override
    public int getItemCount() {
        return artisans.size();
    }

    class ArtisanViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivArtisanAvatar;
        private final TextView tvArtisanCardName;
        private final ImageView ivArtisanVerified;
        private final TextView tvArtisanCraftSpecialty;
        private final TextView tvArtisanLocation;
        private final TextView tvArtisanStory;
        private final TextView tvArtisanProductCount;
        private final View btnViewArtisan;

        public ArtisanViewHolder(@NonNull View itemView) {
            super(itemView);
            ivArtisanAvatar = itemView.findViewById(R.id.ivArtisanAvatar);
            tvArtisanCardName = itemView.findViewById(R.id.tvArtisanCardName);
            ivArtisanVerified = itemView.findViewById(R.id.ivArtisanVerified);
            tvArtisanCraftSpecialty = itemView.findViewById(R.id.tvArtisanCraftSpecialty);
            tvArtisanLocation = itemView.findViewById(R.id.tvArtisanLocation);
            tvArtisanStory = itemView.findViewById(R.id.tvArtisanStory);
            tvArtisanProductCount = itemView.findViewById(R.id.tvArtisanProductCount);
            btnViewArtisan = itemView.findViewById(R.id.btnViewArtisan);

            View.OnClickListener clickAction = v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onArtisanClick(artisans.get(position));
                }
            };

            itemView.setOnClickListener(clickAction);
            btnViewArtisan.setOnClickListener(clickAction);
        }

        public void bind(Artisan artisan) {
            tvArtisanCardName.setText(artisan.getName());
            tvArtisanCraftSpecialty.setText(artisan.getCraftSpecialty());
            tvArtisanLocation.setText(artisan.getLocation());
            tvArtisanStory.setText(artisan.getBio());
            tvArtisanProductCount.setText(artisan.getProductCount() + " handcrafted pieces");

            ivArtisanVerified.setVisibility(artisan.isVerified() ? View.VISIBLE : View.GONE);

            if (artisan.getAvatarUrl() != null && !artisan.getAvatarUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(artisan.getAvatarUrl())
                        .transform(new CircleCrop())
                        .placeholder(R.drawable.ic_role_artisan)
                        .error(artisan.getAvatarResId() != 0 ? artisan.getAvatarResId() : R.drawable.ic_role_artisan)
                        .into(ivArtisanAvatar);
            } else if (artisan.getAvatarResId() != 0) {
                Glide.with(itemView.getContext())
                        .load(artisan.getAvatarResId())
                        .transform(new CircleCrop())
                        .placeholder(R.drawable.ic_role_artisan)
                        .into(ivArtisanAvatar);
            } else {
                ivArtisanAvatar.setImageResource(R.drawable.ic_role_artisan);
            }
        }
    }
}
