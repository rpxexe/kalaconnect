package com.kalaconnect.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kalaconnect.R;
import com.kalaconnect.adapters.PickedImageAdapter;
import com.kalaconnect.models.AiProductContentRequest;
import com.kalaconnect.models.AiProductContentResponse;
import com.kalaconnect.models.Product;
import com.kalaconnect.models.ProductRequest;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.ProductViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AddProductFragment extends Fragment {

    private ProductViewModel productViewModel;
    private PickedImageAdapter pickedImageAdapter;

    private MaterialToolbar toolbarAddProduct;
    private TextInputLayout tilProductName, tilProductCategory, tilProductPrice, tilProductDescription;
    private TextInputEditText etProductName, etProductCategory, etProductCraftType, etProductMaterial;
    private TextInputEditText etProductQuantity, etProductPrice, etProductLocation, etProductDescription;
    private MaterialCardView btnAddPhotoCard;
    private RecyclerView rvPickedPhotos;
    private MaterialButton btnSubmitProduct;
    private TextView tvAddProductError;
    private FrameLayout loadingOverlayAddProduct;

    // AI Assistant Views
    private TextInputLayout tilAiFeatures;
    private TextInputEditText etAiFeatures;
    private MaterialButton btnGenerateAi;
    private View layoutAiLoading;
    private View layoutAiError;
    private TextView tvAiErrorMessage;
    private MaterialButton btnRetryAi;
    private View layoutAiResult;
    private TextInputLayout tilAiDescription, tilAiCaption, tilAiHashtags;
    private TextInputEditText etAiDescription, etAiCaption, etAiHashtags;
    private MaterialButton btnAiRegenerate, btnAiEdit, btnAiUseContent;
    private View layoutAiConfirmedBanner;

    // Confirmed AI State
    private String confirmedAiDescription = null;
    private String confirmedAiCaption = null;
    private String confirmedAiHashtags = null;
    private boolean isAiContentConfirmed = false;

    private Product existingProductToEdit;
    private boolean isEditMode = false;

    // Multiple photo picker launcher
    private final ActivityResultLauncher<String> pickImagesLauncher =
            registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), uris -> {
                if (uris != null && !uris.isEmpty()) {
                    for (Uri uri : uris) {
                        pickedImageAdapter.addPhoto(uri.toString());
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initViewModel();
        setupImagePicker();
        checkForEditMode();
        setupListeners();
    }

    private void initViews(View view) {
        toolbarAddProduct = view.findViewById(R.id.toolbarAddProduct);
        tilProductName = view.findViewById(R.id.tilProductName);
        tilProductCategory = view.findViewById(R.id.tilProductCategory);
        tilProductPrice = view.findViewById(R.id.tilProductPrice);
        tilProductDescription = view.findViewById(R.id.tilProductDescription);

        etProductName = view.findViewById(R.id.etProductName);
        etProductCategory = view.findViewById(R.id.etProductCategory);
        etProductCraftType = view.findViewById(R.id.etProductCraftType);
        etProductMaterial = view.findViewById(R.id.etProductMaterial);
        etProductQuantity = view.findViewById(R.id.etProductQuantity);
        etProductPrice = view.findViewById(R.id.etProductPrice);
        etProductLocation = view.findViewById(R.id.etProductLocation);
        etProductDescription = view.findViewById(R.id.etProductDescription);

        btnAddPhotoCard = view.findViewById(R.id.btnAddPhotoCard);
        rvPickedPhotos = view.findViewById(R.id.rvPickedPhotos);
        btnSubmitProduct = view.findViewById(R.id.btnSubmitProduct);
        tvAddProductError = view.findViewById(R.id.tvAddProductError);
        loadingOverlayAddProduct = view.findViewById(R.id.loadingOverlayAddProduct);

        // AI Assistant Components
        tilAiFeatures = view.findViewById(R.id.tilAiFeatures);
        etAiFeatures = view.findViewById(R.id.etAiFeatures);
        btnGenerateAi = view.findViewById(R.id.btnGenerateAi);
        layoutAiLoading = view.findViewById(R.id.layoutAiLoading);
        layoutAiError = view.findViewById(R.id.layoutAiError);
        tvAiErrorMessage = view.findViewById(R.id.tvAiErrorMessage);
        btnRetryAi = view.findViewById(R.id.btnRetryAi);
        layoutAiResult = view.findViewById(R.id.layoutAiResult);
        tilAiDescription = view.findViewById(R.id.tilAiDescription);
        etAiDescription = view.findViewById(R.id.etAiDescription);
        tilAiCaption = view.findViewById(R.id.tilAiCaption);
        etAiCaption = view.findViewById(R.id.etAiCaption);
        tilAiHashtags = view.findViewById(R.id.tilAiHashtags);
        etAiHashtags = view.findViewById(R.id.etAiHashtags);
        btnAiRegenerate = view.findViewById(R.id.btnAiRegenerate);
        btnAiEdit = view.findViewById(R.id.btnAiEdit);
        btnAiUseContent = view.findViewById(R.id.btnAiUseContent);
        layoutAiConfirmedBanner = view.findViewById(R.id.layoutAiConfirmedBanner);
    }

    private void initViewModel() {
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        productViewModel.getCreateProductResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            handleMutationResult(result, "Product created and published successfully!");
        });

        productViewModel.getUpdateProductResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            handleMutationResult(result, "Product updated successfully!");
        });

        productViewModel.getAiContentResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.getStatus()) {
                case LOADING:
                    if (layoutAiLoading != null) layoutAiLoading.setVisibility(View.VISIBLE);
                    if (layoutAiError != null) layoutAiError.setVisibility(View.GONE);
                    if (layoutAiResult != null) layoutAiResult.setVisibility(View.GONE);
                    if (btnGenerateAi != null) btnGenerateAi.setEnabled(false);
                    if (btnAiRegenerate != null) btnAiRegenerate.setEnabled(false);
                    break;
                case SUCCESS:
                    if (layoutAiLoading != null) layoutAiLoading.setVisibility(View.GONE);
                    if (layoutAiError != null) layoutAiError.setVisibility(View.GONE);
                    if (layoutAiResult != null) layoutAiResult.setVisibility(View.VISIBLE);
                    if (btnGenerateAi != null) btnGenerateAi.setEnabled(true);
                    if (btnAiRegenerate != null) btnAiRegenerate.setEnabled(true);

                    AiProductContentResponse data = result.getData();
                    if (data != null) {
                        if (etAiDescription != null) etAiDescription.setText(data.getDescription());
                        if (etAiCaption != null) etAiCaption.setText(data.getCaption());
                        if (etAiHashtags != null) etAiHashtags.setText(data.getHashtagsAsString());
                    }
                    if (layoutAiConfirmedBanner != null) layoutAiConfirmedBanner.setVisibility(View.GONE);
                    isAiContentConfirmed = false;
                    break;
                case ERROR:
                    if (layoutAiLoading != null) layoutAiLoading.setVisibility(View.GONE);
                    if (layoutAiResult != null) layoutAiResult.setVisibility(View.GONE);
                    if (layoutAiError != null) layoutAiError.setVisibility(View.VISIBLE);
                    if (tvAiErrorMessage != null) {
                        tvAiErrorMessage.setText(result.getMessage() != null ? result.getMessage() : "Failed to generate AI content. Please retry.");
                    }
                    if (btnGenerateAi != null) btnGenerateAi.setEnabled(true);
                    if (btnAiRegenerate != null) btnAiRegenerate.setEnabled(true);
                    break;
            }
        });
    }

    private void handleMutationResult(NetworkResult<?> result, String successMessage) {
        switch (result.getStatus()) {
            case LOADING:
                showLoading(true);
                hideError();
                break;
            case SUCCESS:
                showLoading(false);
                Toast.makeText(requireContext(), successMessage, Toast.LENGTH_SHORT).show();
                navigateBack();
                break;
            case ERROR:
                showLoading(false);
                showError(result.getMessage());
                break;
        }
    }

    private void setupImagePicker() {
        pickedImageAdapter = new PickedImageAdapter();
        pickedImageAdapter.setOnPhotoRemoveListener(position -> pickedImageAdapter.removePhoto(position));

        rvPickedPhotos.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPickedPhotos.setAdapter(pickedImageAdapter);

        btnAddPhotoCard.setOnClickListener(v -> {
            try {
                pickImagesLauncher.launch("image/*");
            } catch (Exception e) {
                // Fallback demo sample photo if device has no gallery activity
                pickedImageAdapter.addPhoto("https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=500&auto=format&fit=crop");
                Toast.makeText(requireContext(), R.string.sample_photo_added, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkForEditMode() {
        if (getArguments() != null && getArguments().containsKey("product")) {
            existingProductToEdit = (Product) getArguments().getSerializable("product");
            if (existingProductToEdit != null) {
                isEditMode = true;
                toolbarAddProduct.setTitle(getString(R.string.edit_handcrafted_product));
                btnSubmitProduct.setText(getString(R.string.save_changes));

                etProductName.setText(existingProductToEdit.getName());
                etProductCategory.setText(existingProductToEdit.getCategory());
                etProductCraftType.setText(existingProductToEdit.getCraftType());
                etProductMaterial.setText(existingProductToEdit.getMaterial());
                etProductQuantity.setText(String.valueOf(existingProductToEdit.getQuantity() != null ? existingProductToEdit.getQuantity() : 1));
                etProductPrice.setText(existingProductToEdit.getPrice() != null ? existingProductToEdit.getPrice().replace("₹", "").trim() : "");
                etProductLocation.setText(existingProductToEdit.getLocation());
                etProductDescription.setText(existingProductToEdit.getDescription());

                if (existingProductToEdit.getPhotos() != null && !existingProductToEdit.getPhotos().isEmpty()) {
                    pickedImageAdapter.setPhotos(existingProductToEdit.getPhotos());
                } else if (existingProductToEdit.getPrimaryPhoto() != null) {
                    pickedImageAdapter.addPhoto(existingProductToEdit.getPrimaryPhoto());
                }
            }
        }
    }

    private void setupListeners() {
        toolbarAddProduct.setNavigationOnClickListener(v -> navigateBack());
        btnSubmitProduct.setOnClickListener(v -> submitProduct());

        // AI Generation Trigger
        btnGenerateAi.setOnClickListener(v -> triggerAiGeneration());

        // AI Retry Trigger
        btnRetryAi.setOnClickListener(v -> triggerAiGeneration());

        // AI Regenerate Button
        btnAiRegenerate.setOnClickListener(v -> triggerAiGeneration());

        // AI Edit Button (Focuses on description for quick refinements)
        btnAiEdit.setOnClickListener(v -> {
            if (etAiDescription != null) {
                etAiDescription.requestFocus();
                Toast.makeText(requireContext(), R.string.ai_edit_prompt, Toast.LENGTH_SHORT).show();
            }
        });

        // AI Use Content Button (Explicit Artisan Confirmation)
        btnAiUseContent.setOnClickListener(v -> applyAiContent());
    }

    private void triggerAiGeneration() {
        String name = etProductName.getText() != null ? etProductName.getText().toString().trim() : "";
        String material = etProductMaterial.getText() != null ? etProductMaterial.getText().toString().trim() : "";
        String craftType = etProductCraftType.getText() != null ? etProductCraftType.getText().toString().trim() : "";
        String location = etProductLocation.getText() != null ? etProductLocation.getText().toString().trim() : "";
        String features = etAiFeatures.getText() != null ? etAiFeatures.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            tilProductName.setError(getString(R.string.enter_product_name_for_ai));
            etProductName.requestFocus();
            Toast.makeText(requireContext(), R.string.please_provide_product_name, Toast.LENGTH_SHORT).show();
            return;
        }

        tilProductName.setError(null);
        AiProductContentRequest request = new AiProductContentRequest(
                name,
                material,
                craftType,
                location,
                features
        );

        productViewModel.generateAiContent(request);
    }

    private void applyAiContent() {
        String desc = etAiDescription != null && etAiDescription.getText() != null ? etAiDescription.getText().toString().trim() : "";
        String caption = etAiCaption != null && etAiCaption.getText() != null ? etAiCaption.getText().toString().trim() : "";
        String hashtags = etAiHashtags != null && etAiHashtags.getText() != null ? etAiHashtags.getText().toString().trim() : "";

        if (TextUtils.isEmpty(desc)) {
            if (tilAiDescription != null) {
                tilAiDescription.setError(getString(R.string.ai_desc_cannot_be_empty));
            }
            return;
        }

        if (tilAiDescription != null) {
            tilAiDescription.setError(null);
        }

        // Populate main product description
        if (etProductDescription != null) {
            etProductDescription.setText(desc);
            if (tilProductDescription != null) {
                tilProductDescription.setError(null);
            }
        }

        // Save confirmed AI state
        confirmedAiDescription = desc;
        confirmedAiCaption = caption;
        confirmedAiHashtags = hashtags;
        isAiContentConfirmed = true;

        if (layoutAiConfirmedBanner != null) {
            layoutAiConfirmedBanner.setVisibility(View.VISIBLE);
        }

        Toast.makeText(requireContext(), R.string.ai_content_confirmed_toast, Toast.LENGTH_SHORT).show();
    }

    private void submitProduct() {
        String name = etProductName.getText() != null ? etProductName.getText().toString().trim() : "";
        String category = etProductCategory.getText() != null ? etProductCategory.getText().toString().trim() : "";
        String craftType = etProductCraftType.getText() != null ? etProductCraftType.getText().toString().trim() : "";
        String material = etProductMaterial.getText() != null ? etProductMaterial.getText().toString().trim() : "";
        String qtyStr = etProductQuantity.getText() != null ? etProductQuantity.getText().toString().trim() : "1";
        String priceStr = etProductPrice.getText() != null ? etProductPrice.getText().toString().trim() : "";
        String location = etProductLocation.getText() != null ? etProductLocation.getText().toString().trim() : "";
        String description = etProductDescription.getText() != null ? etProductDescription.getText().toString().trim() : "";

        tilProductName.setError(null);
        tilProductCategory.setError(null);
        tilProductPrice.setError(null);
        tilProductDescription.setError(null);
        hideError();

        boolean isValid = true;
        if (TextUtils.isEmpty(name)) {
            tilProductName.setError(getString(R.string.err_product_name_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(category)) {
            tilProductCategory.setError(getString(R.string.err_category_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(priceStr)) {
            tilProductPrice.setError(getString(R.string.err_price_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(description)) {
            tilProductDescription.setError(getString(R.string.err_description_required));
            isValid = false;
        }

        BigDecimal price = BigDecimal.ZERO;
        try {
            price = new BigDecimal(priceStr.replace(",", ""));
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                tilProductPrice.setError(getString(R.string.err_price_negative));
                isValid = false;
            }
        } catch (Exception e) {
            tilProductPrice.setError(getString(R.string.err_price_invalid));
            isValid = false;
        }

        int quantity = 1;
        try {
            if (!TextUtils.isEmpty(qtyStr)) {
                quantity = Integer.parseInt(qtyStr);
            }
        } catch (Exception ignored) {}

        if (!isValid) return;

        List<String> photos = pickedImageAdapter.getPhotos();
        if (photos.isEmpty()) {
            // Provide friendly default if artisan hasn't added photo
            photos.add("https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=500&auto=format&fit=crop");
        }

        ProductRequest request = new ProductRequest(
                name,
                category,
                material,
                craftType,
                description,
                price,
                quantity,
                location,
                "PUBLISHED",
                photos
        );

        // If the artisan explicitly confirmed AI content, attach it
        if (isAiContentConfirmed) {
            request.setAiDescription(confirmedAiDescription);
            request.setAiCaption(confirmedAiCaption);
            request.setAiHashtags(confirmedAiHashtags);
        }

        if (isEditMode && existingProductToEdit != null) {
            productViewModel.updateProduct(existingProductToEdit.getId(), request);
        } else {
            productViewModel.createProduct(request);
        }
    }

    private void showLoading(boolean isLoading) {
        if (loadingOverlayAddProduct != null) {
            loadingOverlayAddProduct.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (btnSubmitProduct != null) {
            btnSubmitProduct.setEnabled(!isLoading);
        }
    }

    private void showError(String message) {
        if (tvAddProductError != null) {
            tvAddProductError.setText(message != null ? message : "An error occurred");
            tvAddProductError.setVisibility(View.VISIBLE);
        }
    }

    private void hideError() {
        if (tvAddProductError != null) {
            tvAddProductError.setVisibility(View.GONE);
        }
    }

    private void navigateBack() {
        try {
            NavController navController = Navigation.findNavController(requireView());
            navController.popBackStack();
        } catch (Exception e) {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        }
    }
}
