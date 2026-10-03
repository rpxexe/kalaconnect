package com.kalaconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kalaconnect.R;
import com.kalaconnect.models.OnboardingSlide;

import java.util.ArrayList;
import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.SlideViewHolder> {

    private final List<OnboardingSlide> slides = new ArrayList<>();

    public void setSlides(List<OnboardingSlide> newSlides) {
        this.slides.clear();
        if (newSlides != null) {
            this.slides.addAll(newSlides);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding_slide, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        holder.bind(slides.get(position));
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    static class SlideViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivSlideIcon;
        private final TextView tvSlideTitle;
        private final TextView tvSlideDesc;

        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSlideIcon = itemView.findViewById(R.id.ivSlideIcon);
            tvSlideTitle = itemView.findViewById(R.id.tvSlideTitle);
            tvSlideDesc = itemView.findViewById(R.id.tvSlideDesc);
        }

        public void bind(OnboardingSlide slide) {
            tvSlideTitle.setText(slide.getTitle());
            tvSlideDesc.setText(slide.getDescription());

            // Using Glide to load resource into ImageView
            Glide.with(itemView.getContext())
                    .load(slide.getIconResId())
                    .into(ivSlideIcon);
        }
    }
}
