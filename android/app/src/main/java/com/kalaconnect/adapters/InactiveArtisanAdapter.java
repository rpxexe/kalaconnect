package com.kalaconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kalaconnect.R;
import com.kalaconnect.models.InactiveArtisan;

import java.util.ArrayList;
import java.util.List;

public class InactiveArtisanAdapter extends RecyclerView.Adapter<InactiveArtisanAdapter.ViewHolder> {

    private final List<InactiveArtisan> items = new ArrayList<>();

    public void setItems(List<InactiveArtisan> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analytics_inactive_artisan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InactiveArtisan artisan = items.get(position);
        holder.tvName.setText(artisan.getArtisanName());
        String sub = artisan.getShgName() + " • " + artisan.getDistrict();
        holder.tvSub.setText(sub);
        holder.tvBadge.setText("0 products");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvSub;
        TextView tvBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvInactiveArtisanName);
            tvSub = itemView.findViewById(R.id.tvInactiveArtisanSub);
            tvBadge = itemView.findViewById(R.id.tvInactiveBadge);
        }
    }
}
