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
import com.kalaconnect.models.Product;

import java.util.ArrayList;
import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final List<Product> products = new ArrayList<>();
    private final OnProductClickListener listener;

    public AdminProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> items) {
        this.products.clear();
        if (items != null) {
            this.products.addAll(items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivAdminProductImage;
        private final TextView tvAdminProductName;
        private final TextView tvAdminProductStatus;
        private final TextView tvAdminProductArtisan;
        private final TextView tvAdminProductCategory;
        private final TextView tvAdminProductPrice;
        private final TextView tvAdminProductViews;
        private final TextView tvAdminProductEnquiries;
        private final TextView tvAdminProductStock;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAdminProductImage = itemView.findViewById(R.id.ivAdminProductImage);
            tvAdminProductName = itemView.findViewById(R.id.tvAdminProductName);
            tvAdminProductStatus = itemView.findViewById(R.id.tvAdminProductStatus);
            tvAdminProductArtisan = itemView.findViewById(R.id.tvAdminProductArtisan);
            tvAdminProductCategory = itemView.findViewById(R.id.tvAdminProductCategory);
            tvAdminProductPrice = itemView.findViewById(R.id.tvAdminProductPrice);
            tvAdminProductViews = itemView.findViewById(R.id.tvAdminProductViews);
            tvAdminProductEnquiries = itemView.findViewById(R.id.tvAdminProductEnquiries);
            tvAdminProductStock = itemView.findViewById(R.id.tvAdminProductStock);
        }

        public void bind(Product item, OnProductClickListener listener) {
            tvAdminProductName.setText(item.getName() != null ? item.getName() : "Handcrafted Product");

            String artisan = item.getArtisanName() != null ? item.getArtisanName() : "Artisan";
            String shg = item.getShgName() != null ? item.getShgName() : "";
            tvAdminProductArtisan.setText("Artisan: " + artisan + (shg.isBlank() ? "" : " • " + shg));

            String cat = item.getCategory() != null ? item.getCategory() : "Handicraft";
            String craft = item.getCraftType() != null ? item.getCraftType() : "";
            tvAdminProductCategory.setText("Category: " + cat + (craft.isBlank() ? "" : " • " + craft));

            if (item.getPrice() != null) {
                tvAdminProductPrice.setText("₹" + item.getPrice());
            } else {
                tvAdminProductPrice.setText("₹0.00");
            }

            String status = item.getStatus() != null ? item.getStatus().toUpperCase() : "AVAILABLE";
            tvAdminProductStatus.setText(status);

            if ("AVAILABLE".equals(status)) {
                tvAdminProductStatus.setBackgroundResource(R.drawable.bg_status_badge_success);
                tvAdminProductStatus.setTextColor(itemView.getContext().getColor(R.color.badge_success_text));
            } else if ("OUT_OF_STOCK".equals(status)) {
                tvAdminProductStatus.setBackgroundResource(R.drawable.bg_status_badge_error);
                tvAdminProductStatus.setTextColor(itemView.getContext().getColor(R.color.error));
            } else {
                tvAdminProductStatus.setBackgroundResource(R.drawable.bg_status_badge_pending);
                tvAdminProductStatus.setTextColor(itemView.getContext().getColor(R.color.status_pending));
            }

            int views = item.getViews() != null ? item.getViews() : 0;
            tvAdminProductViews.setText("👀 " + views + " Views");

            int enquiries = item.getEnquiries() != null ? item.getEnquiries() : 0;
            tvAdminProductEnquiries.setText("💬 " + enquiries + " Inquiries");

            int qty = item.getQuantity() != null ? item.getQuantity() : 0;
            tvAdminProductStock.setText("Qty: " + qty + " units");

            String photoUrl = item.getPrimaryPhoto();
            String fallbackUrl = item.getCategoryFallbackImageUrl();

            if (photoUrl != null && !photoUrl.trim().isEmpty()) {
                Object model = (photoUrl.startsWith("content://") || photoUrl.startsWith("file://"))
                        ? android.net.Uri.parse(photoUrl)
                        : photoUrl;

                Glide.with(itemView.getContext())
                        .load(model)
                        .placeholder(R.drawable.bg_card_image_placeholder)
                        .centerCrop()
                        .error(
                                Glide.with(itemView.getContext())
                                        .load(fallbackUrl)
                                        .placeholder(R.drawable.bg_card_image_placeholder)
                                        .centerCrop()
                        )
                        .into(ivAdminProductImage);
            } else {
                Glide.with(itemView.getContext())
                        .load(fallbackUrl)
                        .placeholder(R.drawable.bg_card_image_placeholder)
                        .centerCrop()
                        .into(ivAdminProductImage);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onProductClick(item);
            });
        }
    }
}
