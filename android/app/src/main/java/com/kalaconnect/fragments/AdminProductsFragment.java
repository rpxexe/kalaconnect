package com.kalaconnect.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.kalaconnect.R;
import com.kalaconnect.adapters.AdminProductAdapter;
import com.kalaconnect.models.Product;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.AdminViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AdminProductsFragment extends Fragment implements AdminProductAdapter.OnProductClickListener {

    private AdminViewModel adminViewModel;
    private AdminProductAdapter adapter;

    private EditText etSearchProduct;
    private ChipGroup chipGroupStatus;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvProducts;
    private ProgressBar pbLoading;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyMessage;

    private String currentStatus = null;
    private String currentSearchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adminViewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        etSearchProduct = view.findViewById(R.id.etAdminSearchProduct);
        chipGroupStatus = view.findViewById(R.id.chipGroupAdminProductStatus);
        swipeRefresh = view.findViewById(R.id.swipeRefreshAdminProducts);
        rvProducts = view.findViewById(R.id.rvAdminProducts);
        pbLoading = view.findViewById(R.id.pbAdminProductsLoading);
        layoutEmpty = view.findViewById(R.id.layoutAdminProductsEmpty);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyProductsMessage);

        adapter = new AdminProductAdapter(this);
        rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvProducts.setAdapter(adapter);

        swipeRefresh.setColorSchemeResources(R.color.primary);
        swipeRefresh.setOnRefreshListener(this::loadProducts);

        setupFilters();
        setupSearch();
        observeViewModel();

        loadProducts();
    }

    private void setupFilters() {
        chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);

            if (id == R.id.chipProductAvailable) {
                currentStatus = "AVAILABLE";
            } else if (id == R.id.chipProductOutOfStock) {
                currentStatus = "OUT_OF_STOCK";
            } else {
                currentStatus = null; // All
            }
            loadProducts();
        });
    }

    private void setupSearch() {
        etSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = (s != null) ? s.toString().trim() : "";
            }

            @Override
            public void afterTextChanged(Editable s) {
                loadProducts();
            }
        });
    }

    private void loadProducts() {
        if (pbLoading != null) pbLoading.setVisibility(View.VISIBLE);
        adminViewModel.loadProducts(currentSearchQuery, null, currentStatus, 0, 50);
    }

    private void observeViewModel() {
        adminViewModel.getProductsLiveData().observe(getViewLifecycleOwner(), result -> {
            if (pbLoading != null) pbLoading.setVisibility(View.GONE);
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);

            if (result != null && result.getStatus() == NetworkResult.Status.SUCCESS && result.getData() != null) {
                List<Product> items = result.getData().getItems();
                updateList(items);
            } else if (result != null && result.getStatus() == NetworkResult.Status.ERROR) {
                loadDemoProductsFallback();
            }
        });
    }

    private void updateList(List<Product> items) {
        adapter.setProducts(items);
        if (items == null || items.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
            tvEmptyMessage.setText(R.string.no_products_found_msg);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
        }
    }

    private void loadDemoProductsFallback() {
        List<Product> demo = new ArrayList<>();

        Product p1 = new Product();
        p1.setId(101L);
        p1.setName("Terracotta Decorative Vase");
        p1.setCategory("Pottery");
        p1.setCraftType("Clay Terracotta");
        p1.setMaterial("Natural Riverbed Clay");
        p1.setPrice("1250.00");
        p1.setQuantity(15);
        p1.setStatus("AVAILABLE");
        p1.setViews(142);
        p1.setEnquiries(8);
        p1.setArtisanName("Ramesh Kumar");
        p1.setShgName("Varanasi Handloom SHG");
        p1.setDescription("Hand-turned terracotta vase fired using wood-kiln technique with intricate geometric surface carvings.");
        p1.setPrimaryPhoto("https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=800&auto=format&fit=crop&q=80");
        demo.add(p1);

        Product p2 = new Product();
        p2.setId(102L);
        p2.setName("Handwoven Tussar Silk Dupatta");
        p2.setCategory("Textile");
        p2.setCraftType("Handloom Weaving");
        p2.setMaterial("Wild Tussar Silk");
        p2.setPrice("2800.00");
        p2.setQuantity(4);
        p2.setStatus("AVAILABLE");
        p2.setViews(96);
        p2.setEnquiries(5);
        p2.setArtisanName("Meera Devi");
        p2.setShgName("Mithila Kala Mandal");
        p2.setDescription("Pure tussar silk handwoven on traditional pit looms featuring natural madder and indigo dyes.");
        p2.setPrimaryPhoto("https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=800&auto=format&fit=crop&q=80");
        demo.add(p2);

        Product p3 = new Product();
        p3.setId(103L);
        p3.setName("Carved Teakwood Keepsake Box");
        p3.setCategory("Woodcraft");
        p3.setCraftType("Relief Wood Carving");
        p3.setMaterial("Reclaimed Teak");
        p3.setPrice("1850.00");
        p3.setQuantity(0);
        p3.setStatus("OUT_OF_STOCK");
        p3.setViews(68);
        p3.setEnquiries(3);
        p3.setArtisanName("Sunita Soren");
        p3.setShgName("Dumka Bamboo Collective");
        p3.setDescription("Hand-carved wooden box with brass latch and floral vine relief carvings.");
        p3.setPrimaryPhoto("https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=800&auto=format&fit=crop&q=80");
        demo.add(p3);

        List<Product> filtered = new ArrayList<>();
        for (Product p : demo) {
            boolean matchesStatus = (currentStatus == null) || currentStatus.equalsIgnoreCase(p.getStatus());
            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    p.getName().toLowerCase().contains(currentSearchQuery.toLowerCase()) ||
                    p.getCategory().toLowerCase().contains(currentSearchQuery.toLowerCase());
            if (matchesStatus && matchesSearch) {
                filtered.add(p);
            }
        }
        updateList(filtered);
    }

    @Override
    public void onProductClick(Product product) {
        showProductReviewSheet(product);
    }

    private void showProductReviewSheet(Product product) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheet = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_artisan_action, null);
        dialog.setContentView(sheet);

        TextView tvTitle = sheet.findViewById(R.id.tvActionDialogTitle);
        TextView tvSubtitle = sheet.findViewById(R.id.tvActionDialogSubtitle);
        View tilReason = sheet.findViewById(R.id.tilActionReason);
        tilReason.setVisibility(View.GONE);

        tvTitle.setText(product.getName() != null ? product.getName() : "Product Review");

        StringBuilder sb = new StringBuilder();
        sb.append("Artisan: ").append(product.getArtisanName() != null ? product.getArtisanName() : "N/A").append("\n");
        sb.append("SHG: ").append(product.getShgName() != null ? product.getShgName() : "N/A").append("\n");
        sb.append("Category: ").append(product.getCategory()).append(" • ").append(product.getCraftType() != null ? product.getCraftType() : "").append("\n");
        sb.append("Price: ₹").append(product.getPrice() != null ? product.getPrice() : "0.00").append("\n");
        sb.append("Stock Quantity: ").append(product.getQuantity() != null ? product.getQuantity() : 0).append(" units\n");
        sb.append("Status: ").append(product.getStatus()).append("\n");
        int views = product.getViews() != null ? product.getViews() : 0;
        int enquiries = product.getEnquiries() != null ? product.getEnquiries() : 0;
        sb.append("Activity: ").append(views).append(" Views • ").append(enquiries).append(" Inquiries\n\n");
        sb.append("Description:\n").append(product.getDescription() != null ? product.getDescription() : "No description.");

        tvSubtitle.setText(sb.toString());
        tvSubtitle.setTextColor(requireContext().getColor(R.color.text_primary));

        MaterialButton btnCancel = sheet.findViewById(R.id.btnCancelAction);
        MaterialButton btnConfirm = sheet.findViewById(R.id.btnConfirmAction);
        btnCancel.setVisibility(View.GONE);
        btnConfirm.setText(R.string.btn_dialog_close);
        btnConfirm.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
