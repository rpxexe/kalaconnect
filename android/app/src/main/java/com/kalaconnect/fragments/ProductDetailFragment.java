package com.kalaconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.kalaconnect.R;
import com.kalaconnect.models.Artisan;
import com.kalaconnect.models.EnquiryRequest;
import com.kalaconnect.models.Product;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.CustomerViewModel;

public class ProductDetailFragment extends Fragment {

    private CustomerViewModel customerViewModel;
    private Product product;

    private MaterialToolbar toolbar;
    private ImageView ivProductImage;
    private TextView tvProductName;
    private TextView tvProductPrice;
    private TextView tvProductAvailability;
    private Chip chipCategory;
    private Chip chipCraftType;
    private Chip chipLocation;
    private TextView tvProductMaterial;
    private TextView tvProductDescription;

    private View cardArtisan;
    private ImageView ivArtisanAvatar;
    private TextView tvArtisanName;
    private TextView tvArtisanShg;
    private TextView tvArtisanLocation;

    private View layoutAiContent;
    private TextView tvAiCaption;
    private TextView tvAiHashtags;

    private TextView tvStickyPrice;
    private MaterialButton btnSendEnquiry;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        customerViewModel = new ViewModelProvider(requireActivity()).get(CustomerViewModel.class);

        initViews(view);

        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable("product");
            if (product != null) {
                bindProduct(product);
            } else {
                long productId = getArguments().getLong("productId", -1L);
                if (productId > 0) {
                    fetchProduct(productId);
                }
            }
        }
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbarProductDetail);
        ivProductImage = view.findViewById(R.id.ivDetailProductImage);
        tvProductName = view.findViewById(R.id.tvDetailProductName);
        tvProductPrice = view.findViewById(R.id.tvDetailProductPrice);
        tvProductAvailability = view.findViewById(R.id.tvDetailProductAvailability);
        chipCategory = view.findViewById(R.id.chipDetailCategory);
        chipCraftType = view.findViewById(R.id.chipDetailCraftType);
        chipLocation = view.findViewById(R.id.chipDetailLocation);
        tvProductMaterial = view.findViewById(R.id.tvDetailProductMaterial);
        tvProductDescription = view.findViewById(R.id.tvDetailProductDescription);

        cardArtisan = view.findViewById(R.id.cardDetailArtisan);
        ivArtisanAvatar = view.findViewById(R.id.ivDetailArtisanAvatar);
        tvArtisanName = view.findViewById(R.id.tvDetailArtisanName);
        tvArtisanShg = view.findViewById(R.id.tvDetailArtisanShg);
        tvArtisanLocation = view.findViewById(R.id.tvDetailArtisanLocation);

        layoutAiContent = view.findViewById(R.id.layoutDetailAiContent);
        tvAiCaption = view.findViewById(R.id.tvDetailAiCaption);
        tvAiHashtags = view.findViewById(R.id.tvDetailAiHashtags);

        tvStickyPrice = view.findViewById(R.id.tvDetailStickyPrice);
        btnSendEnquiry = view.findViewById(R.id.btnDetailSendEnquiry);

        toolbar.setNavigationOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigateUp();
        });

        cardArtisan.setOnClickListener(v -> openArtisanProfile());

        btnSendEnquiry.setOnClickListener(v -> showSendEnquiryModal());
    }

    private void fetchProduct(long id) {
        customerViewModel.getProductDetail(id).observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.getStatus() == NetworkResult.Status.SUCCESS && result.getData() != null) {
                this.product = result.getData();
                bindProduct(this.product);
            }
        });
    }

    private void bindProduct(@NonNull Product p) {
        tvProductName.setText(p.getName());

        String priceStr = p.getPrice();
        if (priceStr != null && !priceStr.startsWith("₹")) {
            priceStr = "₹" + priceStr;
        }
        tvProductPrice.setText(priceStr != null ? priceStr : "₹0");
        tvStickyPrice.setText(priceStr != null ? priceStr : "₹0");

        String avail = p.getAvailability();
        if (avail == null || avail.isBlank()) {
            avail = p.getStatus() != null ? p.getStatus() : "IN_STOCK";
        }
        tvProductAvailability.setText(avail.replace("_", " ").toUpperCase());

        if (p.getCategory() != null && !p.getCategory().isBlank()) {
            chipCategory.setText(p.getCategory());
            chipCategory.setVisibility(View.VISIBLE);
        } else {
            chipCategory.setVisibility(View.GONE);
        }

        if (p.getCraftType() != null && !p.getCraftType().isBlank()) {
            chipCraftType.setText(p.getCraftType());
            chipCraftType.setVisibility(View.VISIBLE);
        } else {
            chipCraftType.setVisibility(View.GONE);
        }

        if (p.getLocation() != null && !p.getLocation().isBlank()) {
            chipLocation.setText(p.getLocation());
            chipLocation.setVisibility(View.VISIBLE);
        } else {
            chipLocation.setVisibility(View.GONE);
        }

        if (p.getMaterial() != null && !p.getMaterial().isBlank()) {
            tvProductMaterial.setText(getString(R.string.material_label_prefix, p.getMaterial()));
            tvProductMaterial.setVisibility(View.VISIBLE);
        } else {
            tvProductMaterial.setVisibility(View.GONE);
        }

        if (p.getDescription() != null && !p.getDescription().isBlank()) {
            tvProductDescription.setText(p.getDescription());
        }

        // Artisan Card
        String artisanName = p.getArtisanName();
        if (artisanName == null || artisanName.isBlank()) {
            artisanName = p.getShgName() != null ? p.getShgName() : getString(R.string.artisan_collective_default);
        }
        tvArtisanName.setText(artisanName);

        if (p.getShgName() != null && !p.getShgName().isBlank()) {
            tvArtisanShg.setText(getString(R.string.shg_prefix, p.getShgName()));
            tvArtisanShg.setVisibility(View.VISIBLE);
        } else {
            tvArtisanShg.setVisibility(View.GONE);
        }

        tvArtisanLocation.setText(p.getLocation() != null ? p.getLocation() : "India");

        // Load Images
        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(p.getImageUrl())
                    .transform(new CenterCrop(), new RoundedCorners(24))
                    .placeholder(R.drawable.bg_card_image_placeholder)
                    .error(R.drawable.ic_craft)
                    .into(ivProductImage);
        } else if (p.getImageResId() != 0) {
            Glide.with(this)
                    .load(p.getImageResId())
                    .transform(new CenterCrop(), new RoundedCorners(24))
                    .placeholder(R.drawable.bg_card_image_placeholder)
                    .into(ivProductImage);
        }

        Glide.with(this)
                .load(R.drawable.ic_role_artisan)
                .transform(new CircleCrop())
                .into(ivArtisanAvatar);

        // AI Promotion Content (if present)
        if ((p.getAiCaption() != null && !p.getAiCaption().isBlank()) ||
            (p.getAiHashtags() != null && !p.getAiHashtags().isBlank())) {
            layoutAiContent.setVisibility(View.VISIBLE);
            if (p.getAiCaption() != null) tvAiCaption.setText(p.getAiCaption());
            if (p.getAiHashtags() != null) tvAiHashtags.setText(p.getAiHashtags());
        } else {
            layoutAiContent.setVisibility(View.GONE);
        }
    }

    private void openArtisanProfile() {
        if (product == null) return;
        Artisan a = new Artisan();
        a.setId(product.getArtisanProfileId() != null ? product.getArtisanProfileId() : product.getArtisanId());
        a.setName(product.getArtisanName() != null ? product.getArtisanName() : product.getShgName());
        a.setCraftSpecialty(product.getCraftType() != null ? product.getCraftType() : product.getCategory());
        a.setLocation(product.getLocation());
        a.setBio("Handicraft collective producing authentic traditional creations.");
        a.setVerified(true);
        a.setProductCount(12);

        Bundle args = new Bundle();
        args.putSerializable("artisan", a);
        if (product.getArtisanProfileId() != null) {
            args.putLong("artisanId", product.getArtisanProfileId());
        }
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.artisanDetailFragment, args);
    }

    private void showSendEnquiryModal() {
        if (product == null) return;

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View modalView = getLayoutInflater().inflate(R.layout.dialog_send_enquiry, null);
        dialog.setContentView(modalView);

        ImageView ivThumb = modalView.findViewById(R.id.ivEnquiryProductThumb);
        TextView tvProdName = modalView.findViewById(R.id.tvEnquiryProductName);
        TextView tvProdArtisan = modalView.findViewById(R.id.tvEnquiryProductArtisan);
        TextView tvProdPrice = modalView.findViewById(R.id.tvEnquiryProductPrice);
        EditText etMessage = modalView.findViewById(R.id.etEnquiryMessage);
        EditText etCustomerName = modalView.findViewById(R.id.etEnquiryCustomerName);
        EditText etCustomerContact = modalView.findViewById(R.id.etEnquiryCustomerContact);
        ProgressBar pbLoading = modalView.findViewById(R.id.pbEnquiryLoading);
        MaterialButton btnSubmit = modalView.findViewById(R.id.btnEnquirySubmit);
        MaterialButton btnCancel = modalView.findViewById(R.id.btnEnquiryCancel);
        View btnClose = modalView.findViewById(R.id.btnEnquiryClose);

        tvProdName.setText(product.getName());
        tvProdArtisan.setText(product.getArtisanName() != null ? "By " + product.getArtisanName() : (product.getShgName() != null ? "By " + product.getShgName() : ""));
        tvProdPrice.setText(product.getPrice() != null && product.getPrice().startsWith("₹") ? product.getPrice() : "₹" + product.getPrice());

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(product.getImageUrl())
                    .transform(new CenterCrop(), new RoundedCorners(12))
                    .placeholder(R.drawable.bg_card_image_placeholder)
                    .into(ivThumb);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (msg.isEmpty()) {
                etMessage.setError("Please write your message");
                return;
            }

            EnquiryRequest req = new EnquiryRequest();
            req.setProductId(product.getId());
            req.setArtisanId(product.getArtisanId());
            req.setMessage(msg);
            req.setCustomerName(etCustomerName.getText().toString().trim());
            String contact = etCustomerContact.getText().toString().trim();
            if (contact.contains("@")) {
                req.setCustomerEmail(contact);
            } else {
                req.setCustomerPhone(contact);
            }

            pbLoading.setVisibility(View.VISIBLE);
            btnSubmit.setEnabled(false);

            customerViewModel.sendEnquiry(req).observe(getViewLifecycleOwner(), result -> {
                if (result == null) return;

                if (result.getStatus() == NetworkResult.Status.SUCCESS) {
                    pbLoading.setVisibility(View.GONE);
                    dialog.dismiss();
                    Toast.makeText(requireContext(), R.string.enquiry_sent_buyer_success, Toast.LENGTH_LONG).show();
                } else if (result.getStatus() == NetworkResult.Status.ERROR) {
                    pbLoading.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    Toast.makeText(requireContext(), result.getMessage() != null ? result.getMessage() : getString(R.string.enquiry_send_failed), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
