package com.kalaconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kalaconnect.R;
import com.kalaconnect.models.AnalyticsMetricItem;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsBarAdapter extends RecyclerView.Adapter<AnalyticsBarAdapter.ViewHolder> {

    private final List<AnalyticsMetricItem> items = new ArrayList<>();
    private String unitLabel = "";

    public AnalyticsBarAdapter() {
    }

    public AnalyticsBarAdapter(String unitLabel) {
        this.unitLabel = unitLabel;
    }

    public void setItems(List<AnalyticsMetricItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analytics_bar_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnalyticsMetricItem item = items.get(position);
        holder.tvBarLabel.setText(item.getLabel());

        String valueText = item.getCount() + (unitLabel.isEmpty() ? "" : " " + unitLabel);
        if (item.getPercentage() > 0) {
            valueText += " (" + item.getPercentage() + "%)";
        }
        holder.tvBarValue.setText(valueText);

        int progress = (int) Math.round(item.getPercentage());
        if (progress <= 0 && item.getCount() > 0) {
            progress = 5;
        }
        holder.progressBar.setProgress(Math.min(progress, 100));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBarLabel;
        TextView tvBarValue;
        LinearProgressIndicator progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBarLabel = itemView.findViewById(R.id.tvBarLabel);
            tvBarValue = itemView.findViewById(R.id.tvBarValue);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
