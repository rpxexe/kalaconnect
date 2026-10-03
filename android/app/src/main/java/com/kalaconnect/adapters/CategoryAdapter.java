package com.kalaconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kalaconnect.R;
import com.kalaconnect.models.CraftCategory;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(CraftCategory category, int position);
    }

    private final List<CraftCategory> categories = new ArrayList<>();
    private OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public void setCategories(List<CraftCategory> newCategories) {
        this.categories.clear();
        if (newCategories != null) {
            this.categories.addAll(newCategories);
        }
        notifyDataSetChanged();
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_chip, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position), position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final View layoutChipContainer;
        private final TextView tvChipLabel;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutChipContainer = itemView.findViewById(R.id.layoutChipContainer);
            tvChipLabel = itemView.findViewById(R.id.tvChipLabel);

            layoutChipContainer.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    int previous = selectedPosition;
                    selectedPosition = position;
                    notifyItemChanged(previous);
                    notifyItemChanged(selectedPosition);

                    if (listener != null) {
                        listener.onCategoryClick(categories.get(position), position);
                    }
                }
            });
        }

        public void bind(CraftCategory category, boolean isSelected) {
            tvChipLabel.setText(category.getName());
            if (isSelected) {
                layoutChipContainer.setBackgroundResource(R.drawable.bg_chip_selected);
                tvChipLabel.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.on_primary));
            } else {
                layoutChipContainer.setBackgroundResource(R.drawable.bg_chip_unselected);
                tvChipLabel.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_primary));
            }
        }
    }
}
