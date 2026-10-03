package com.kalaconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kalaconnect.R;
import com.kalaconnect.models.AnalyticsMetricItem;

import java.util.ArrayList;
import java.util.List;

public class OpportunityAdapter extends RecyclerView.Adapter<OpportunityAdapter.ViewHolder> {

    private final List<AnalyticsMetricItem> items = new ArrayList<>();
    private String unitLabel = "artisans";

    public OpportunityAdapter() {
    }

    public OpportunityAdapter(String unitLabel) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analytics_opportunity_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnalyticsMetricItem item = items.get(position);
        holder.tvLabel.setText(item.getLabel());
        String countText = item.getCount() + " " + unitLabel;
        holder.tvCount.setText(countText);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel;
        TextView tvCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvOppLabel);
            tvCount = itemView.findViewById(R.id.tvOppCount);
        }
    }
}
