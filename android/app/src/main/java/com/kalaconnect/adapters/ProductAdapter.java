package com.kalaconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.kalaconnect.R;
import com.kalaconnect.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final List<Product> products = new ArrayList<>();
    private OnProductClickListener listener;

    public void setProducts(List<Product> newProducts) {
        this.products.clear();
        if (newProducts != null) {
            this.products.addAll(newProducts);
        }
        notifyDataSetChanged();
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
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

        private final ImageView ivProductImage;
        private final ImageButton btnFavorite;
        private final TextView tvProductName;
        private final TextView tvProductArtisan;
        private final TextView tvProductPrice;
        private final TextView tvProductLocation;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductArtisan = itemView.findViewById(R.id.tvProductArtisan);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductLocation = itemView.findViewById(R.id.tvProductLocation);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onProductClick(products.get(position));
                }
            });

            btnFavorite.setOnClickListener(v -> {
                btnFavorite.setSelected(!btnFavorite.isSelected());
            });
        }

        public void bind(Product product) {
            tvProductName.setText(product.getName());
            String artisan = product.getArtisanName();
            if (artisan == null || artisan.trim().isEmpty()) {
                artisan = product.getShgName() != null ? product.getShgName() : "Master Artisan";
            }
            tvProductArtisan.setText(artisan);

            String price = product.getPrice();
            if (price != null && !price.trim().isEmpty() && !price.startsWith("₹")) {
                price = "₹" + price;
            }
            tvProductPrice.setText(price != null ? price : "₹0");
            tvProductLocation.setText(product.getLocation() != null ? product.getLocation() : "India");

            // Image-First: Load image with Glide, fallback to vector logo placeholder
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImageUrl())
                        .transform(new CenterCrop(), new RoundedCorners(24))
                        .placeholder(R.drawable.bg_card_image_placeholder)
                        .error(product.getImageResId() != 0 ? product.getImageResId() : R.drawable.ic_craft)
                        .into(ivProductImage);
            } else if (product.getImageResId() != 0) {
                Glide.with(itemView.getContext())
                        .load(product.getImageResId())
                        .transform(new CenterCrop(), new RoundedCorners(24))
                        .placeholder(R.drawable.bg_card_image_placeholder)
                        .into(ivProductImage);
            } else {
                ivProductImage.setImageResource(R.drawable.ic_craft);
            }
        }
    }
}
