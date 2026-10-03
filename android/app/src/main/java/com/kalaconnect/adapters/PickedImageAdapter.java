package com.kalaconnect.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.kalaconnect.R;

import java.util.ArrayList;
import java.util.List;

public class PickedImageAdapter extends RecyclerView.Adapter<PickedImageAdapter.ImageViewHolder> {

    public interface OnPhotoRemoveListener {
        void onRemovePhoto(int position);
    }

    private final List<String> photoUrls = new ArrayList<>();
    private OnPhotoRemoveListener removeListener;

    public void setPhotos(List<String> photos) {
        this.photoUrls.clear();
        if (photos != null) {
            this.photoUrls.addAll(photos);
        }
        notifyDataSetChanged();
    }

    public void addPhoto(String photoUrl) {
        if (photoUrl != null && !photoUrl.trim().isEmpty()) {
            this.photoUrls.add(photoUrl);
            notifyItemInserted(this.photoUrls.size() - 1);
        }
    }

    public void removePhoto(int position) {
        if (position >= 0 && position < photoUrls.size()) {
            this.photoUrls.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, photoUrls.size() - position);
        }
    }

    public List<String> getPhotos() {
        return new ArrayList<>(photoUrls);
    }

    public void setOnPhotoRemoveListener(OnPhotoRemoveListener listener) {
        this.removeListener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picked_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(photoUrls.get(position), position);
    }

    @Override
    public int getItemCount() {
        return photoUrls.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivPickedPhoto;
        private final FrameLayout btnRemovePhoto;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPickedPhoto = itemView.findViewById(R.id.ivPickedPhoto);
            btnRemovePhoto = itemView.findViewById(R.id.btnRemovePhoto);

            btnRemovePhoto.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && removeListener != null) {
                    removeListener.onRemovePhoto(position);
                }
            });
        }

        public void bind(String photoPath, int position) {
            // Support both content:// or file:// or http:// URLs
            Glide.with(itemView.getContext())
                    .load(photoPath.startsWith("content://") || photoPath.startsWith("file://") ? Uri.parse(photoPath) : photoPath)
                    .transform(new CenterCrop(), new RoundedCorners(24))
                    .placeholder(R.drawable.bg_card_image_placeholder)
                    .error(R.drawable.ic_craft)
                    .into(ivPickedPhoto);
        }
    }
}
