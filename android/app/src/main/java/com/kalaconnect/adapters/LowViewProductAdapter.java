package com.kalaconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kalaconnect.R;
import com.kalaconnect.models.LowViewProduct;

import java.util.ArrayList;
import java.util.List;

public class LowViewProductAdapter extends RecyclerView.Adapter<LowViewProductAdapter.ViewHolder> {

    private final List<LowViewProduct> items = new ArrayList<>();

    public void setItems(List<LowViewProduct> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analytics_low_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LowViewProduct product = items.get(position);
        holder.tvProductName.setText(product.getName());
        String sub = product.getCategory() + " • by " + product.getArtisanName();
        holder.tvProductSub.setText(sub);
        String viewsText = product.getViews() + (product.getViews() == 1 ? " view" : " views");
        holder.tvViewCount.setText(viewsText);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        TextView tvProductSub;
        TextView tvViewCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvLowViewProductName);
            tvProductSub = itemView.findViewById(R.id.tvLowViewProductSub);
            tvViewCount = itemView.findViewById(R.id.tvLowViewCount);
        }
    }
}
