package com.kalaconnect.adapters;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.kalaconnect.R;
import com.kalaconnect.models.Product;

import java.util.ArrayList;
import java.util.List;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.ProductViewHolder> {

    public interface OnProductActionListener {
        void onViewProduct(Product product);
        void onEditProduct(Product product);
        void onDeleteProduct(Product product);
        void onPublishProduct(Product product);
        void onUnpublishProduct(Product product);
    }

    private final List<Product> products = new ArrayList<>();
    private OnProductActionListener actionListener;

    public void setProducts(List<Product> newProducts) {
        this.products.clear();
        if (newProducts != null) {
            this.products.addAll(newProducts);
        }
        notifyDataSetChanged();
    }

    public void setOnProductActionListener(OnProductActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivMyProductImage;
        private final TextView tvMyProductName;
        private final TextView tvMyProductPrice;
        private final TextView tvMyProductStatus;
        private final TextView tvMyProductCategory;
        private final TextView tvMyProductViews;
        private final TextView tvMyProductEnquiries;
        private final ImageButton btnProductActions;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMyProductImage = itemView.findViewById(R.id.ivMyProductImage);
            tvMyProductName = itemView.findViewById(R.id.tvMyProductName);
            tvMyProductPrice = itemView.findViewById(R.id.tvMyProductPrice);
            tvMyProductStatus = itemView.findViewById(R.id.tvMyProductStatus);
            tvMyProductCategory = itemView.findViewById(R.id.tvMyProductCategory);
            tvMyProductViews = itemView.findViewById(R.id.tvMyProductViews);
            tvMyProductEnquiries = itemView.findViewById(R.id.tvMyProductEnquiries);
            btnProductActions = itemView.findViewById(R.id.btnProductActions);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && actionListener != null) {
                    actionListener.onViewProduct(products.get(position));
                }
            });

            btnProductActions.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && actionListener != null) {
                    showPopupMenu(btnProductActions, products.get(position));
                }
            });
        }

        public void bind(Product product) {
            tvMyProductName.setText(product.getName());

            String priceStr = product.getPrice();
            if (priceStr != null && !priceStr.startsWith("₹")) {
                priceStr = "₹ " + priceStr;
            }
            tvMyProductPrice.setText(priceStr != null ? priceStr : "₹ 0");

            String categoryText = "";
            if (product.getCategory() != null) categoryText += product.getCategory();
            if (product.getMaterial() != null && !product.getMaterial().isEmpty()) {
                if (!categoryText.isEmpty()) categoryText += " • ";
                categoryText += product.getMaterial();
            }
            tvMyProductCategory.setText(categoryText);

            int views = product.getViews() != null ? product.getViews() : 0;
            tvMyProductViews.setText(views + " views");

            int enquiries = product.getEnquiries() != null ? product.getEnquiries() : 0;
            tvMyProductEnquiries.setText(enquiries + " enquiries");

            // Status Badge Formatting
            String status = product.getStatus() != null ? product.getStatus() : "PUBLISHED";
            tvMyProductStatus.setText(status.toUpperCase());

            if ("PUBLISHED".equalsIgnoreCase(status) || "AVAILABLE".equalsIgnoreCase(status)) {
                tvMyProductStatus.setBackgroundResource(R.drawable.bg_status_badge_success);
                tvMyProductStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_success));
            } else if ("DRAFT".equalsIgnoreCase(status)) {
                tvMyProductStatus.setBackgroundResource(R.drawable.bg_status_badge_pending);
                tvMyProductStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_pending));
            } else {
                tvMyProductStatus.setBackgroundResource(R.drawable.bg_status_badge_error);
                tvMyProductStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_error));
            }

            // Image Thumbnail with resilient category fallback
            String imageUrl = product.getPrimaryPhoto();
            String fallbackUrl = product.getCategoryFallbackImageUrl();

            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                Object model = (imageUrl.startsWith("content://") || imageUrl.startsWith("file://"))
                        ? android.net.Uri.parse(imageUrl)
                        : imageUrl;

                Glide.with(itemView.getContext())
                        .load(model)
                        .transform(new CenterCrop(), new RoundedCorners(24))
                        .placeholder(R.drawable.bg_card_image_placeholder)
                        .error(
                                Glide.with(itemView.getContext())
                                        .load(fallbackUrl)
                                        .transform(new CenterCrop(), new RoundedCorners(24))
                                        .placeholder(R.drawable.bg_card_image_placeholder)
                                        .error(R.drawable.ic_craft)
                        )
                        .into(ivMyProductImage);
            } else {
                Glide.with(itemView.getContext())
                        .load(fallbackUrl)
                        .transform(new CenterCrop(), new RoundedCorners(24))
                        .placeholder(R.drawable.bg_card_image_placeholder)
                        .error(R.drawable.ic_craft)
                        .into(ivMyProductImage);
            }
        }

        private void showPopupMenu(View anchor, Product product) {
            PopupMenu popup = new PopupMenu(anchor.getContext(), anchor);
            popup.inflate(R.menu.menu_product_actions);

            boolean isPublished = product.isPublished();
            MenuItem publishItem = popup.getMenu().findItem(R.id.action_product_publish);
            MenuItem unpublishItem = popup.getMenu().findItem(R.id.action_product_unpublish);

            if (publishItem != null) publishItem.setVisible(!isPublished);
            if (unpublishItem != null) unpublishItem.setVisible(isPublished);

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_product_view) {
                    actionListener.onViewProduct(product);
                    return true;
                } else if (id == R.id.action_product_edit) {
                    actionListener.onEditProduct(product);
                    return true;
                } else if (id == R.id.action_product_publish) {
                    actionListener.onPublishProduct(product);
                    return true;
                } else if (id == R.id.action_product_unpublish) {
                    actionListener.onUnpublishProduct(product);
                    return true;
                } else if (id == R.id.action_product_delete) {
                    actionListener.onDeleteProduct(product);
                    return true;
                }
                return false;
            });

            popup.show();
        }
    }
}
