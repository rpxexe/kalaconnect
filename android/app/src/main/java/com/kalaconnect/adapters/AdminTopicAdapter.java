package com.kalaconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kalaconnect.R;
import com.kalaconnect.models.LearningModule;

import java.util.ArrayList;
import java.util.List;

public class AdminTopicAdapter extends RecyclerView.Adapter<AdminTopicAdapter.TopicViewHolder> {

    public interface OnTopicActionListener {
        void onPreviewTopic(LearningModule module);
        void onDeleteTopic(LearningModule module);
    }

    private final List<LearningModule> modules = new ArrayList<>();
    private final OnTopicActionListener listener;

    public AdminTopicAdapter(OnTopicActionListener listener) {
        this.listener = listener;
    }

    public void setModules(List<LearningModule> list) {
        this.modules.clear();
        if (list != null) {
            this.modules.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_topic, parent, false);
        return new TopicViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        holder.bind(modules.get(position));
    }

    @Override
    public int getItemCount() {
        return modules.size();
    }

    class TopicViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAdminTopicCategory;
        private final TextView tvAdminTopicDuration;
        private final TextView tvAdminTopicTitle;
        private final TextView tvAdminTopicSummary;
        private final TextView tvAdminTopicBadge;
        private final ImageButton btnDeleteTopic;
        private final MaterialButton btnPreviewTopic;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAdminTopicCategory = itemView.findViewById(R.id.tvAdminTopicCategory);
            tvAdminTopicDuration = itemView.findViewById(R.id.tvAdminTopicDuration);
            tvAdminTopicTitle = itemView.findViewById(R.id.tvAdminTopicTitle);
            tvAdminTopicSummary = itemView.findViewById(R.id.tvAdminTopicSummary);
            tvAdminTopicBadge = itemView.findViewById(R.id.tvAdminTopicBadge);
            btnDeleteTopic = itemView.findViewById(R.id.btnDeleteTopic);
            btnPreviewTopic = itemView.findViewById(R.id.btnPreviewTopic);
        }

        public void bind(LearningModule module) {
            tvAdminTopicCategory.setText(module.getCategory() != null ? module.getCategory().toUpperCase() : "GENERAL");
            tvAdminTopicDuration.setText("⏱ " + (module.getDuration() != null ? module.getDuration() : "15 mins"));
            tvAdminTopicTitle.setText(module.getTitle());
            tvAdminTopicSummary.setText(module.getSummary() != null ? module.getSummary() : module.getSubtitle());
            tvAdminTopicBadge.setText(module.getBadgeText() != null ? module.getBadgeText() : "Verified");

            btnDeleteTopic.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteTopic(module);
                }
            });

            btnPreviewTopic.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPreviewTopic(module);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPreviewTopic(module);
                }
            });
        }
    }
}
