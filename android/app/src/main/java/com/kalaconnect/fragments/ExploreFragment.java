package com.kalaconnect.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kalaconnect.R;
import com.kalaconnect.adapters.ArtisanAdapter;
import com.kalaconnect.adapters.ProductAdapter;
import com.kalaconnect.models.Artisan;
import com.kalaconnect.models.Product;
import com.kalaconnect.viewmodel.CustomerViewModel;

import java.math.BigDecimal;
import java.util.List;

public class ExploreFragment extends Fragment {

    private CustomerViewModel customerViewModel;

    private EditText etExploreSearch;
    private ImageButton btnExploreClearSearch;
    private MaterialButton btnExploreFilter;
    private Chip chipTabProducts;
    private Chip chipTabArtisans;
    private View scrollActiveFilters;
    private ChipGroup chipGroupActiveFilters;

    private SwipeRefreshLayout swipeRefreshExplore;
    private RecyclerView rvExploreProducts;
    private RecyclerView rvExploreArtisans;
    private View layoutExploreLoading;
    private ProgressBar pbExplorePagination;
    private View layoutExploreEmpty;
    private View layoutExploreError;
    private TextView tvExploreErrorMsg;
    private MaterialButton btnExploreRetry;
    private MaterialButton btnExploreResetFilters;

    private ProductAdapter productAdapter;
    private ArtisanAdapter artisanAdapter;

    private boolean isBrowsingProducts = true;

    // Filter states
    private String currentCategory = "";
    private String currentCraftType = "";
    private String currentState = "";
    private String currentDistrict = "";
    private BigDecimal currentMinPrice = null;
    private BigDecimal currentMaxPrice = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        customerViewModel = new ViewModelProvider(requireActivity()).get(CustomerViewModel.class);

        initViews(view);
        setupRecyclerViews();
        setupSearchAndTabs();
        setupObservers();

        // Initial fetch
        customerViewModel.refreshProducts();
        customerViewModel.refreshArtisans();
    }

    private void initViews(View view) {
        etExploreSearch = view.findViewById(R.id.etExploreSearch);
        btnExploreClearSearch = view.findViewById(R.id.btnExploreClearSearch);
        btnExploreFilter = view.findViewById(R.id.btnExploreFilter);
        chipTabProducts = view.findViewById(R.id.chipTabProducts);
        chipTabArtisans = view.findViewById(R.id.chipTabArtisans);
        scrollActiveFilters = view.findViewById(R.id.scrollActiveFilters);
        chipGroupActiveFilters = view.findViewById(R.id.chipGroupActiveFilters);

        swipeRefreshExplore = view.findViewById(R.id.swipeRefreshExplore);
        rvExploreProducts = view.findViewById(R.id.rvExploreProducts);
        rvExploreArtisans = view.findViewById(R.id.rvExploreArtisans);
        layoutExploreLoading = view.findViewById(R.id.layoutExploreLoading);
        pbExplorePagination = view.findViewById(R.id.pbExplorePagination);
        layoutExploreEmpty = view.findViewById(R.id.layoutExploreEmpty);
        layoutExploreError = view.findViewById(R.id.layoutExploreError);
        tvExploreErrorMsg = view.findViewById(R.id.tvExploreErrorMsg);
        btnExploreRetry = view.findViewById(R.id.btnExploreRetry);
        btnExploreResetFilters = view.findViewById(R.id.btnExploreResetFilters);

        btnExploreFilter.setOnClickListener(v -> showFilterDialog());
        btnExploreResetFilters.setOnClickListener(v -> resetFilters());
        btnExploreRetry.setOnClickListener(v -> {
            if (isBrowsingProducts) {
                customerViewModel.refreshProducts();
            } else {
                customerViewModel.refreshArtisans();
            }
        });

        swipeRefreshExplore.setColorSchemeResources(R.color.primary);
        swipeRefreshExplore.setOnRefreshListener(() -> {
            if (isBrowsingProducts) {
                customerViewModel.refreshProducts();
            } else {
                customerViewModel.refreshArtisans();
            }
        });
    }

    private void setupRecyclerViews() {
        productAdapter = new ProductAdapter();
        rvExploreProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvExploreProducts.setAdapter(productAdapter);

        productAdapter.setOnProductClickListener(this::openProductDetail);

        // Infinite scroll pagination for products
        rvExploreProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && isBrowsingProducts) {
                    GridLayoutManager glm = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (glm != null) {
                        int visibleCount = glm.getChildCount();
                        int totalCount = glm.getItemCount();
                        int firstVisible = glm.findFirstVisibleItemPosition();

                        if ((visibleCount + firstVisible) >= totalCount - 4 && totalCount > 0) {
                            customerViewModel.loadNextProductPage();
                        }
                    }
                }
            }
        });

        artisanAdapter = new ArtisanAdapter();
        rvExploreArtisans.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvExploreArtisans.setAdapter(artisanAdapter);

        artisanAdapter.setOnArtisanClickListener(this::openArtisanDetail);

        // Infinite scroll pagination for artisans
        rvExploreArtisans.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && !isBrowsingProducts) {
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (llm != null) {
                        int visibleCount = llm.getChildCount();
                        int totalCount = llm.getItemCount();
                        int firstVisible = llm.findFirstVisibleItemPosition();

                        if ((visibleCount + firstVisible) >= totalCount - 3 && totalCount > 0) {
                            customerViewModel.loadNextArtisanPage();
                        }
                    }
                }
            }
        });
    }

    private void setupSearchAndTabs() {
        chipTabProducts.setOnClickListener(v -> {
            chipTabProducts.setChecked(true);
            chipTabArtisans.setChecked(false);
            isBrowsingProducts = true;
            rvExploreProducts.setVisibility(View.VISIBLE);
            rvExploreArtisans.setVisibility(View.GONE);
            updateUIState();
        });

        chipTabArtisans.setOnClickListener(v -> {
            chipTabArtisans.setChecked(true);
            chipTabProducts.setChecked(false);
            isBrowsingProducts = false;
            rvExploreProducts.setVisibility(View.GONE);
            rvExploreArtisans.setVisibility(View.VISIBLE);
            updateUIState();
        });

        etExploreSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                applySearch();
                return true;
            }
            return false;
        });

        etExploreSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnExploreClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                if (s.length() == 0) {
                    applySearch();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnExploreClearSearch.setOnClickListener(v -> {
            etExploreSearch.setText("");
            applySearch();
        });
    }

    private void applySearch() {
        String query = etExploreSearch.getText().toString().trim();
        customerViewModel.setProductFilters(query, currentCategory, currentCraftType, currentState, currentDistrict, currentMinPrice, currentMaxPrice);
        customerViewModel.setArtisanFilters(query, "", currentState, currentDistrict);
        updateActiveFilterChips();
    }

    private void setupObservers() {
        // Products Observers
        customerViewModel.getProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            productAdapter.setProducts(products);
            swipeRefreshExplore.setRefreshing(false);
            updateUIState();
        });

        customerViewModel.getIsProductsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (isBrowsingProducts) {
                boolean hasItems = productAdapter.getItemCount() > 0;
                layoutExploreLoading.setVisibility(loading && !hasItems ? View.VISIBLE : View.GONE);
                pbExplorePagination.setVisibility(loading && hasItems ? View.VISIBLE : View.GONE);
            }
        });

        customerViewModel.getProductsError().observe(getViewLifecycleOwner(), error -> {
            if (isBrowsingProducts && error != null && productAdapter.getItemCount() == 0) {
                tvExploreErrorMsg.setText(error);
                layoutExploreError.setVisibility(View.VISIBLE);
                layoutExploreEmpty.setVisibility(View.GONE);
            } else if (error == null) {
                layoutExploreError.setVisibility(View.GONE);
            }
            swipeRefreshExplore.setRefreshing(false);
        });

        // Artisans Observers
        customerViewModel.getArtisansLiveData().observe(getViewLifecycleOwner(), artisans -> {
            artisanAdapter.setArtisanProfiles(artisans);
            swipeRefreshExplore.setRefreshing(false);
            updateUIState();
        });

        customerViewModel.getIsArtisansLoading().observe(getViewLifecycleOwner(), loading -> {
            if (!isBrowsingProducts) {
                boolean hasItems = artisanAdapter.getItemCount() > 0;
                layoutExploreLoading.setVisibility(loading && !hasItems ? View.VISIBLE : View.GONE);
                pbExplorePagination.setVisibility(loading && hasItems ? View.VISIBLE : View.GONE);
            }
        });

        customerViewModel.getArtisansError().observe(getViewLifecycleOwner(), error -> {
            if (!isBrowsingProducts && error != null && artisanAdapter.getItemCount() == 0) {
                tvExploreErrorMsg.setText(error);
                layoutExploreError.setVisibility(View.VISIBLE);
                layoutExploreEmpty.setVisibility(View.GONE);
            } else if (error == null) {
                layoutExploreError.setVisibility(View.GONE);
            }
            swipeRefreshExplore.setRefreshing(false);
        });
    }

    private void updateUIState() {
        int count = isBrowsingProducts ? productAdapter.getItemCount() : artisanAdapter.getItemCount();
        boolean isLoading = Boolean.TRUE.equals(isBrowsingProducts ? customerViewModel.getIsProductsLoading().getValue() : customerViewModel.getIsArtisansLoading().getValue());
        boolean hasError = (isBrowsingProducts ? customerViewModel.getProductsError().getValue() : customerViewModel.getArtisansError().getValue()) != null;

        if (count == 0 && !isLoading && !hasError) {
            layoutExploreEmpty.setVisibility(View.VISIBLE);
            layoutExploreError.setVisibility(View.GONE);
        } else if (count > 0) {
            layoutExploreEmpty.setVisibility(View.GONE);
            layoutExploreError.setVisibility(View.GONE);
        }
    }

    private void showFilterDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter_explore, null);
        dialog.setContentView(dialogView);

        ChipGroup chipGroupCat = dialogView.findViewById(R.id.chipGroupCategories);
        EditText etMinP = dialogView.findViewById(R.id.etFilterMinPrice);
        EditText etMaxP = dialogView.findViewById(R.id.etFilterMaxPrice);
        EditText etState = dialogView.findViewById(R.id.etFilterState);
        EditText etDistrict = dialogView.findViewById(R.id.etFilterDistrict);
        EditText etCraftType = dialogView.findViewById(R.id.etFilterCraftType);
        MaterialButton btnApply = dialogView.findViewById(R.id.btnFilterApply);
        MaterialButton btnReset = dialogView.findViewById(R.id.btnFilterReset);
        View btnClose = dialogView.findViewById(R.id.btnFilterClose);

        // Prepopulate dialog
        if (currentMinPrice != null) etMinP.setText(currentMinPrice.toPlainString());
        if (currentMaxPrice != null) etMaxP.setText(currentMaxPrice.toPlainString());
        if (!currentState.isEmpty()) etState.setText(currentState);
        if (!currentDistrict.isEmpty()) etDistrict.setText(currentDistrict);
        if (!currentCraftType.isEmpty()) etCraftType.setText(currentCraftType);

        // Select matching category chip
        for (int i = 0; i < chipGroupCat.getChildCount(); i++) {
            View child = chipGroupCat.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (chip.getText().toString().equalsIgnoreCase(currentCategory)) {
                    chip.setChecked(true);
                    break;
                }
            }
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnReset.setOnClickListener(v -> {
            resetFilters();
            dialog.dismiss();
        });

        btnApply.setOnClickListener(v -> {
            int selectedChipId = chipGroupCat.getCheckedChipId();
            if (selectedChipId != View.NO_ID) {
                Chip selectedChip = dialogView.findViewById(selectedChipId);
                String catText = selectedChip.getText().toString();
                currentCategory = "All".equalsIgnoreCase(catText) ? "" : catText;
            } else {
                currentCategory = "";
            }

            String minStr = etMinP.getText().toString().trim();
            String maxStr = etMaxP.getText().toString().trim();
            currentMinPrice = minStr.isEmpty() ? null : new BigDecimal(minStr);
            currentMaxPrice = maxStr.isEmpty() ? null : new BigDecimal(maxStr);

            currentState = etState.getText().toString().trim();
            currentDistrict = etDistrict.getText().toString().trim();
            currentCraftType = etCraftType.getText().toString().trim();

            String query = etExploreSearch.getText().toString().trim();
            customerViewModel.setProductFilters(query, currentCategory, currentCraftType, currentState, currentDistrict, currentMinPrice, currentMaxPrice);
            customerViewModel.setArtisanFilters(query, "", currentState, currentDistrict);

            updateActiveFilterChips();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void resetFilters() {
        currentCategory = "";
        currentCraftType = "";
        currentState = "";
        currentDistrict = "";
        currentMinPrice = null;
        currentMaxPrice = null;
        String query = etExploreSearch.getText().toString().trim();

        customerViewModel.setProductFilters(query, "", "", "", "", null, null);
        customerViewModel.setArtisanFilters(query, "", "", "");
        updateActiveFilterChips();
    }

    private void updateActiveFilterChips() {
        chipGroupActiveFilters.removeAllViews();
        boolean hasFilters = false;

        if (!currentCategory.isEmpty()) {
            addActiveFilterChip("Category: " + currentCategory, () -> {
                currentCategory = "";
                applySearch();
            });
            hasFilters = true;
        }

        if (currentMinPrice != null || currentMaxPrice != null) {
            String label = "₹" + (currentMinPrice != null ? currentMinPrice : "0") + " - " + (currentMaxPrice != null ? "₹" + currentMaxPrice : "Any");
            addActiveFilterChip(label, () -> {
                currentMinPrice = null;
                currentMaxPrice = null;
                applySearch();
            });
            hasFilters = true;
        }

        if (!currentState.isEmpty()) {
            addActiveFilterChip("State: " + currentState, () -> {
                currentState = "";
                applySearch();
            });
            hasFilters = true;
        }

        if (!currentDistrict.isEmpty()) {
            addActiveFilterChip("District: " + currentDistrict, () -> {
                currentDistrict = "";
                applySearch();
            });
            hasFilters = true;
        }

        if (!currentCraftType.isEmpty()) {
            addActiveFilterChip("Craft: " + currentCraftType, () -> {
                currentCraftType = "";
                applySearch();
            });
            hasFilters = true;
        }

        scrollActiveFilters.setVisibility(hasFilters ? View.VISIBLE : View.GONE);
    }

    private void addActiveFilterChip(String text, Runnable onRemove) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> onRemove.run());
        chipGroupActiveFilters.addView(chip);
    }

    private void openProductDetail(Product product) {
        Bundle args = new Bundle();
        args.putSerializable("product", product);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.productDetailFragment, args);
    }

    private void openArtisanDetail(Artisan artisan) {
        Bundle args = new Bundle();
        args.putSerializable("artisan", artisan);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.artisanDetailFragment, args);
    }

    public void selectCategory(String category) {
        this.currentCategory = category != null ? category : "";
        applySearch();
    }
}
