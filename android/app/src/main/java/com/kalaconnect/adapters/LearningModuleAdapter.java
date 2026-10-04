package com.kalaconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kalaconnect.R;
import com.kalaconnect.models.LearningModule;

import java.util.ArrayList;
import java.util.List;

public class LearningModuleAdapter extends RecyclerView.Adapter<LearningModuleAdapter.ModuleViewHolder> {

    public interface OnModuleClickListener {
        void onModuleClick(LearningModule module);
    }

    private final List<LearningModule> modules = new ArrayList<>();
    private OnModuleClickListener listener;

    public void setModules(List<LearningModule> newModules) {
        modules.clear();
        if (newModules != null) {
            modules.addAll(newModules);
        }
        notifyDataSetChanged();
    }

    public void setOnModuleClickListener(OnModuleClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_learning_module, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        holder.bind(modules.get(position));
    }

    @Override
    public int getItemCount() {
        return modules.size();
    }

    class ModuleViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardLearningModule;
        private final TextView tvModuleCategory;
        private final TextView tvModuleDuration;
        private final TextView tvModuleBadge;
        private final TextView tvModuleTitle;
        private final TextView tvModuleSummary;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            cardLearningModule = itemView.findViewById(R.id.cardLearningModule);
            tvModuleCategory = itemView.findViewById(R.id.tvModuleCategory);
            tvModuleDuration = itemView.findViewById(R.id.tvModuleDuration);
            tvModuleBadge = itemView.findViewById(R.id.tvModuleBadge);
            tvModuleTitle = itemView.findViewById(R.id.tvModuleTitle);
            tvModuleSummary = itemView.findViewById(R.id.tvModuleSummary);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onModuleClick(modules.get(position));
                }
            });
        }

        public void bind(LearningModule module) {
            tvModuleCategory.setText(module.getCategory());
            tvModuleDuration.setText(module.getDuration());
            tvModuleBadge.setText(module.getBadgeText());
            tvModuleTitle.setText(module.getTitle());
            tvModuleSummary.setText(module.getSummary());
        }
    }
}
