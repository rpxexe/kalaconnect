package com.kalaconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kalaconnect.R;
import com.kalaconnect.models.NotificationItem;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<NotificationItem> notifications = new ArrayList<>();

    public void setNotifications(List<NotificationItem> items) {
        this.notifications.clear();
        if (items != null) {
            this.notifications.addAll(items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artisan_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNotificationTitle;
        private final TextView tvNotificationTime;
        private final TextView tvNotificationBody;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotificationTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvNotificationTime = itemView.findViewById(R.id.tvNotificationTime);
            tvNotificationBody = itemView.findViewById(R.id.tvNotificationBody);
        }

        public void bind(NotificationItem item) {
            tvNotificationTitle.setText(item.getTitle());
            tvNotificationTime.setText(item.getCreatedAt() != null ? item.getCreatedAt() : "Recent");
            tvNotificationBody.setText(item.getMessage());
        }
    }
}
