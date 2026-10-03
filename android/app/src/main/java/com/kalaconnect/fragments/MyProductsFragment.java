package com.kalaconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.kalaconnect.R;
import com.kalaconnect.adapters.MyProductAdapter;
import com.kalaconnect.models.Product;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyProductsFragment extends Fragment implements MyProductAdapter.OnProductActionListener {

    private ProductViewModel productViewModel;
    private MyProductAdapter productAdapter;

    private SwipeRefreshLayout swipeRefreshMyProducts;
    private RecyclerView rvMyProducts;
    private TextView tvProductCountSubtitle;
    private MaterialButton btnAddProductHeader;
    private ExtendedFloatingActionButton fabAddProduct;
    private MaterialButton btnEmptyAddProduct;
    private MaterialButton btnRetryMyProducts;
    private TextView tvMyProductsErrorMessage;

    private LinearLayout layoutMyProductsLoading;
    private LinearLayout layoutMyProductsEmpty;
    private LinearLayout layoutMyProductsError;

    private ChipGroup chipGroupProductFilters;
    private Chip chipFilterAll, chipFilterPublished, chipFilterDraft;

    private final List<Product> allProducts = new ArrayList<>();
    private String currentFilter = "ALL"; // ALL, PUBLISHED, DRAFT

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        initViewModel();
        setupListeners();

        productViewModel.loadMyProducts();
    }

    private void initViews(View view) {
        swipeRefreshMyProducts = view.findViewById(R.id.swipeRefreshMyProducts);
        rvMyProducts = view.findViewById(R.id.rvMyProducts);
        tvProductCountSubtitle = view.findViewById(R.id.tvProductCountSubtitle);
        btnAddProductHeader = view.findViewById(R.id.btnAddProductHeader);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);
        btnEmptyAddProduct = view.findViewById(R.id.btnEmptyAddProduct);
        btnRetryMyProducts = view.findViewById(R.id.btnRetryMyProducts);
        tvMyProductsErrorMessage = view.findViewById(R.id.tvMyProductsErrorMessage);

        layoutMyProductsLoading = view.findViewById(R.id.layoutMyProductsLoading);
        layoutMyProductsEmpty = view.findViewById(R.id.layoutMyProductsEmpty);
        layoutMyProductsError = view.findViewById(R.id.layoutMyProductsError);

        chipGroupProductFilters = view.findViewById(R.id.chipGroupProductFilters);
        chipFilterAll = view.findViewById(R.id.chipFilterAll);
        chipFilterPublished = view.findViewById(R.id.chipFilterPublished);
        chipFilterDraft = view.findViewById(R.id.chipFilterDraft);
    }

    private void setupRecyclerView() {
        productAdapter = new MyProductAdapter();
        productAdapter.setOnProductActionListener(this);
        rvMyProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMyProducts.setAdapter(productAdapter);
    }

    private void initViewModel() {
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        productViewModel.getMyProductsResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            swipeRefreshMyProducts.setRefreshing(false);

            switch (result.getStatus()) {
                case LOADING:
                    if (allProducts.isEmpty()) {
                        showState(true, false, false);
                    }
                    break;
                case SUCCESS:
                    allProducts.clear();
                    if (result.getData() != null) {
                        allProducts.addAll(result.getData());
                    }
                    applyFilter();
                    break;
                case ERROR:
                    if (allProducts.isEmpty()) {
                        showState(false, false, true);
                        if (tvMyProductsErrorMessage != null) {
                            tvMyProductsErrorMessage.setText(result.getMessage());
                        }
                    } else {
                        Toast.makeText(requireContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        });

        productViewModel.getDeleteProductResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.getStatus() == NetworkResult.Status.SUCCESS) {
                Toast.makeText(requireContext(), R.string.product_removed_toast, Toast.LENGTH_SHORT).show();
                productViewModel.loadMyProducts();
            } else if (result.getStatus() == NetworkResult.Status.ERROR) {
                Toast.makeText(requireContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        productViewModel.getStatusUpdateResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.getStatus() == NetworkResult.Status.SUCCESS) {
                Toast.makeText(requireContext(), R.string.status_updated_success, Toast.LENGTH_SHORT).show();
                productViewModel.loadMyProducts();
            } else if (result.getStatus() == NetworkResult.Status.ERROR) {
                Toast.makeText(requireContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        swipeRefreshMyProducts.setOnRefreshListener(() -> productViewModel.loadMyProducts());

        View.OnClickListener openAddProduct = v -> navigateToAddProduct(null);
        btnAddProductHeader.setOnClickListener(openAddProduct);
        fabAddProduct.setOnClickListener(openAddProduct);
        btnEmptyAddProduct.setOnClickListener(openAddProduct);

        btnRetryMyProducts.setOnClickListener(v -> productViewModel.loadMyProducts());

        chipFilterAll.setOnClickListener(v -> {
            currentFilter = "ALL";
            applyFilter();
        });
        chipFilterPublished.setOnClickListener(v -> {
            currentFilter = "PUBLISHED";
            applyFilter();
        });
        chipFilterDraft.setOnClickListener(v -> {
            currentFilter = "DRAFT";
            applyFilter();
        });
    }

    private void applyFilter() {
        List<Product> filtered = new ArrayList<>();
        for (Product p : allProducts) {
            String status = p.getStatus() != null ? p.getStatus() : "PUBLISHED";
            if ("ALL".equals(currentFilter)) {
                filtered.add(p);
            } else if ("PUBLISHED".equals(currentFilter) && ("PUBLISHED".equalsIgnoreCase(status) || "AVAILABLE".equalsIgnoreCase(status))) {
                filtered.add(p);
            } else if ("DRAFT".equals(currentFilter) && ("DRAFT".equalsIgnoreCase(status) || "UNPUBLISHED".equalsIgnoreCase(status))) {
                filtered.add(p);
            }
        }

        productAdapter.setProducts(filtered);

        tvProductCountSubtitle.setText(allProducts.size() + " total handcrafted products catalogued");

        if (filtered.isEmpty()) {
            showState(false, true, false);
        } else {
            showState(false, false, false);
        }
    }

    private void showState(boolean loading, boolean empty, boolean error) {
        layoutMyProductsLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        layoutMyProductsEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        layoutMyProductsError.setVisibility(error ? View.VISIBLE : View.GONE);
        rvMyProducts.setVisibility((!loading && !empty && !error) ? View.VISIBLE : View.GONE);
    }

    private void navigateToAddProduct(@Nullable Product productToEdit) {
        try {
            NavController navController = Navigation.findNavController(requireView());
            Bundle args = new Bundle();
            if (productToEdit != null) {
                args.putSerializable("product", productToEdit);
            }
            navController.navigate(R.id.action_artisan_to_addProduct, args);
        } catch (Exception e) {
            // If inner tab container navigation is active, perform child navigation
            AddProductFragment fragment = new AddProductFragment();
            if (productToEdit != null) {
                Bundle args = new Bundle();
                args.putSerializable("product", productToEdit);
                fragment.setArguments(args);
            }
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.artisan_nav_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onViewProduct(Product product) {
        String msg = getString(
                R.string.product_detail_format,
                product.getCategory() != null ? product.getCategory() : "N/A",
                product.getMaterial() != null ? product.getMaterial() : "N/A",
                product.getCraftType() != null ? product.getCraftType() : "N/A",
                product.getPrice() != null ? product.getPrice() : "₹ 0",
                product.getStatus() != null ? product.getStatus() : "DRAFT",
                product.getViews() != null ? product.getViews() : 0,
                product.getEnquiries() != null ? product.getEnquiries() : 0,
                product.getDescription() != null ? product.getDescription() : ""
        );
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(product.getName())
                .setMessage(msg)
                .setPositiveButton(R.string.btn_dialog_close, null)
                .setNeutralButton(R.string.action_edit, (dialog, which) -> onEditProduct(product))
                .show();
    }

    @Override
    public void onEditProduct(Product product) {
        navigateToAddProduct(product);
    }

    @Override
    public void onDeleteProduct(Product product) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_product_title)
                .setMessage(getString(R.string.delete_product_named_confirm, product.getName()))
                .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    if (product.getId() != null) {
                        productViewModel.deleteProduct(product.getId());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onPublishProduct(Product product) {
        if (product.getId() != null) {
            productViewModel.publishProduct(product.getId());
        }
    }

    @Override
    public void onUnpublishProduct(Product product) {
        if (product.getId() != null) {
            productViewModel.unpublishProduct(product.getId());
        }
    }
}
