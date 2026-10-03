package com.kalaconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kalaconnect.R;
import com.kalaconnect.models.EnquiryItem;

import java.util.ArrayList;
import java.util.List;

public class EnquiryAdapter extends RecyclerView.Adapter<EnquiryAdapter.EnquiryViewHolder> {

    public interface OnEnquiryClickListener {
        void onReply(EnquiryItem item);
        default void onUpdateStatus(EnquiryItem item) {}
    }

    private final List<EnquiryItem> enquiries = new ArrayList<>();
    private OnEnquiryClickListener listener;

    public void setEnquiries(List<EnquiryItem> items) {
        this.enquiries.clear();
        if (items != null) {
            this.enquiries.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void setOnEnquiryClickListener(OnEnquiryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public EnquiryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artisan_enquiry, parent, false);
        return new EnquiryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnquiryViewHolder holder, int position) {
        holder.bind(enquiries.get(position));
    }

    @Override
    public int getItemCount() {
        return enquiries.size();
    }

    class EnquiryViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCustomerName;
        private final TextView tvCustomerContact;
        private final TextView tvEnquiryStatus;
        private final TextView tvEnquiryProduct;
        private final TextView tvEnquiryMessage;
        private final TextView tvEnquiryDate;
        private final MaterialButton btnUpdateStatus;
        private final MaterialButton btnReplyEnquiry;

        public EnquiryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerContact = itemView.findViewById(R.id.tvCustomerContact);
            tvEnquiryStatus = itemView.findViewById(R.id.tvEnquiryStatus);
            tvEnquiryProduct = itemView.findViewById(R.id.tvEnquiryProduct);
            tvEnquiryMessage = itemView.findViewById(R.id.tvEnquiryMessage);
            tvEnquiryDate = itemView.findViewById(R.id.tvEnquiryDate);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
            btnReplyEnquiry = itemView.findViewById(R.id.btnReplyEnquiry);

            if (btnUpdateStatus != null) {
                btnUpdateStatus.setOnClickListener(v -> {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && listener != null) {
                        listener.onUpdateStatus(enquiries.get(pos));
                    }
                });
            }

            if (btnReplyEnquiry != null) {
                btnReplyEnquiry.setOnClickListener(v -> {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && listener != null) {
                        listener.onReply(enquiries.get(pos));
                    }
                });
            }
        }

        public void bind(EnquiryItem item) {
            tvCustomerName.setText(item.getCustomerName() != null ? item.getCustomerName() : "Customer");

            StringBuilder contactStr = new StringBuilder();
            if (item.getCustomerPhone() != null && !item.getCustomerPhone().isBlank()) {
                contactStr.append("Tel: ").append(item.getCustomerPhone());
            }
            if (item.getCustomerEmail() != null && !item.getCustomerEmail().isBlank()) {
                if (contactStr.length() > 0) contactStr.append(" • ");
                contactStr.append(item.getCustomerEmail());
            }
            if (tvCustomerContact != null) {
                if (contactStr.length() > 0) {
                    tvCustomerContact.setText(contactStr.toString());
                    tvCustomerContact.setVisibility(View.VISIBLE);
                } else {
                    tvCustomerContact.setVisibility(View.GONE);
                }
            }

            tvEnquiryProduct.setText("Regarding: " + (item.getProductName() != null ? item.getProductName() : "Product"));
            tvEnquiryMessage.setText(item.getMessage());
            tvEnquiryDate.setText(item.getCreatedAt() != null ? item.getCreatedAt() : "Recent");

            String status = item.getStatus() != null ? item.getStatus().toUpperCase() : "PENDING";
            tvEnquiryStatus.setText(status);

            if ("RESOLVED".equals(status)) {
                tvEnquiryStatus.setBackgroundResource(R.drawable.bg_status_badge_success);
                tvEnquiryStatus.setTextColor(itemView.getContext().getColor(R.color.badge_success_text));
            } else if ("CONTACTED".equals(status)) {
                tvEnquiryStatus.setBackgroundResource(R.drawable.bg_chip_selected);
                tvEnquiryStatus.setTextColor(itemView.getContext().getColor(R.color.primary));
            } else if ("CLOSED".equals(status)) {
                tvEnquiryStatus.setBackgroundResource(R.drawable.bg_chip_unselected);
                tvEnquiryStatus.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
            } else {
                tvEnquiryStatus.setBackgroundResource(R.drawable.bg_status_badge_pending);
                tvEnquiryStatus.setTextColor(itemView.getContext().getColor(R.color.status_pending));
            }
        }
    }
}
