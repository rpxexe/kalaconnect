package com.kalaconnect.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kalaconnect.R;
import com.kalaconnect.adapters.ArtisanAdapter;
import com.kalaconnect.adapters.CategoryAdapter;
import com.kalaconnect.adapters.ProductAdapter;
import com.kalaconnect.models.Artisan;
import com.kalaconnect.models.CraftCategory;
import com.kalaconnect.models.Product;
import com.kalaconnect.viewmodel.AuthViewModel;
import com.kalaconnect.viewmodel.CustomerViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private AuthViewModel authViewModel;
    private CustomerViewModel customerViewModel;

    private TextView tvHomeGreeting;
    private RecyclerView rvCraftCategories;
    private RecyclerView rvFeaturedProducts;
    private RecyclerView rvFeaturedArtisans;
    private MaterialButton btnSeeAllProducts;
    private MaterialButton btnSeeAllArtisans;

    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private ArtisanAdapter artisanAdapter;

    private final List<Product> allProducts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        customerViewModel = new ViewModelProvider(requireActivity()).get(CustomerViewModel.class);

        tvHomeGreeting = view.findViewById(R.id.tvHomeGreeting);
        rvCraftCategories = view.findViewById(R.id.rvCraftCategories);
        rvFeaturedProducts = view.findViewById(R.id.rvFeaturedProducts);
        rvFeaturedArtisans = view.findViewById(R.id.rvFeaturedArtisans);
        btnSeeAllProducts = view.findViewById(R.id.btnSeeAllProducts);
        btnSeeAllArtisans = view.findViewById(R.id.btnSeeAllArtisans);

        String userName = authViewModel.getLoggedInUserName();
        if (userName != null && !userName.trim().isEmpty()) {
            tvHomeGreeting.setText(getString(R.string.greeting_namaste, userName.toUpperCase()));
        }

        setupCategories();
        setupProducts();
        setupArtisans();
        setupSearch(view);
        setupActions();
        observeLiveData();
    }

    private void setupActions() {
        if (btnSeeAllProducts != null) {
            btnSeeAllProducts.setOnClickListener(v -> navigateToExplore(null));
        }
        if (btnSeeAllArtisans != null) {
            btnSeeAllArtisans.setOnClickListener(v -> navigateToExplore(null));
        }
    }

    private void observeLiveData() {
        customerViewModel.getProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                productAdapter.setProducts(products);
            }
        });

        customerViewModel.getArtisansLiveData().observe(getViewLifecycleOwner(), artisans -> {
            if (artisans != null && !artisans.isEmpty()) {
                artisanAdapter.setArtisanProfiles(artisans);
            }
        });
    }

    private void setupCategories() {
        categoryAdapter = new CategoryAdapter();
        rvCraftCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCraftCategories.setAdapter(categoryAdapter);

        List<CraftCategory> categories = new ArrayList<>();
        categories.add(new CraftCategory("all", "All Traditions", true));
        categories.add(new CraftCategory("terracotta", "Terracotta Pottery", false));
        categories.add(new CraftCategory("madhubani", "Madhubani Folk Art", false));
        categories.add(new CraftCategory("handloom", "Handloom & Ikat", false));
        categories.add(new CraftCategory("dhokra", "Dhokra Metalcraft", false));
        categories.add(new CraftCategory("wood", "Walnut Woodcraft", false));

        categoryAdapter.setCategories(categories);

        categoryAdapter.setOnCategoryClickListener((category, position) -> {
            if ("all".equalsIgnoreCase(category.getId())) {
                productAdapter.setProducts(new ArrayList<>(allProducts));
            } else {
                navigateToExplore(category.getName());
            }
        });
    }

    private void setupProducts() {
        productAdapter = new ProductAdapter();
        rvFeaturedProducts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFeaturedProducts.setAdapter(productAdapter);

        allProducts.clear();
        allProducts.add(new Product(
                1L,
                "Terracotta Chai Vessel Set",
                "By Gorakhpur Clay Masters",
                "₹1,450",
                "Gorakhpur, UP",
                R.drawable.ic_craft,
                "terracotta"
        ));
        allProducts.add(new Product(
                2L,
                "Tree of Life Canvas",
                "By Lakshmi Mahila SHG",
                "₹3,800",
                "Madhubani, Bihar",
                R.drawable.ic_kala_logo,
                "madhubani"
        ));
        allProducts.add(new Product(
                3L,
                "Sambalpuri Ikat Silk Saree",
                "By Odisha Handloom Guild",
                "₹5,200",
                "Bargarh, Odisha",
                R.drawable.ic_craft,
                "handloom"
        ));
        allProducts.add(new Product(
                4L,
                "Dhokra Bell Metal Tribal Horse",
                "By Bastar Tribal Guild",
                "₹2,750",
                "Bastar, Chhattisgarh",
                R.drawable.ic_kala_logo,
                "dhokra"
        ));
        allProducts.add(new Product(
                5L,
                "Carved Walnut Keepsake Box",
                "By Kashmir Heritage Wood",
                "₹1,900",
                "Srinagar, Kashmir",
                R.drawable.ic_craft,
                "wood"
        ));

        productAdapter.setProducts(new ArrayList<>(allProducts));

        productAdapter.setOnProductClickListener(this::openProductDetail);
    }

    private void setupArtisans() {
        artisanAdapter = new ArtisanAdapter();
        rvFeaturedArtisans.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFeaturedArtisans.setAdapter(artisanAdapter);

        List<Artisan> artisans = new ArrayList<>();
        artisans.add(new Artisan(
                101L,
                "Lakshmi Mahila SHG",
                "Madhubani Folk Art",
                "Madhubani, Bihar",
                "Women's collective preserving centuries-old natural plant pigment canvases.",
                true,
                18,
                R.drawable.ic_role_artisan
        ));
        artisans.add(new Artisan(
                102L,
                "Gorakhpur Clay Masters",
                "Terracotta Pottery",
                "Gorakhpur, UP",
                "Generational potters shaping GI-tagged red clay homeware and vessels.",
                true,
                26,
                R.drawable.ic_role_artisan
        ));
        artisans.add(new Artisan(
                103L,
                "Bastar Tribal Guild",
                "Dhokra Bell Metal",
                "Bastar, Chhattisgarh",
                "Ancient lost-wax bronze castings preserving Gond and tribal folklore.",
                true,
                14,
                R.drawable.ic_role_ngo
        ));

        artisanAdapter.setArtisans(artisans);

        artisanAdapter.setOnArtisanClickListener(this::openArtisanDetail);
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
        if (artisan.getId() != null) {
            args.putLong("artisanId", artisan.getId());
        }
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.artisanDetailFragment, args);
    }

    private void navigateToExplore(String category) {
        Fragment parent = getParentFragment();
        if (parent instanceof CustomerDashboardFragment) {
            ((CustomerDashboardFragment) parent).switchToExploreTab(category);
        }
    }

    private void setupSearch(View view) {
        EditText etSearch = view.findViewById(R.id.etSearchInput);
        if (etSearch != null) {
            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    navigateToExplore(null);
                    return true;
                }
                return false;
            });

            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterProductsByQuery(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void filterProductsByQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            productAdapter.setProducts(new ArrayList<>(allProducts));
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        List<Product> matches = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(lowerQuery) ||
                product.getArtisanName().toLowerCase().contains(lowerQuery) ||
                product.getLocation().toLowerCase().contains(lowerQuery)) {
                matches.add(product);
            }
        }
        productAdapter.setProducts(matches);
    }
}
