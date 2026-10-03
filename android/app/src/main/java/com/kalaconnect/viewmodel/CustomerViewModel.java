package com.kalaconnect.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kalaconnect.models.ArtisanProfile;
import com.kalaconnect.models.EnquiryItem;
import com.kalaconnect.models.EnquiryRequest;
import com.kalaconnect.models.PagedResult;
import com.kalaconnect.models.Product;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.repository.ArtisanRepository;
import com.kalaconnect.repository.EnquiryRepository;
import com.kalaconnect.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CustomerViewModel extends AndroidViewModel {

    private final ProductRepository productRepository;
    private final ArtisanRepository artisanRepository;
    private final EnquiryRepository enquiryRepository;

    // --- Products Browsing State ---
    private final MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isProductsLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> productsError = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> hasMoreProducts = new MutableLiveData<>(true);

    private int productPage = 0;
    private static final int PAGE_SIZE = 12;
    private boolean isFetchingProducts = false;

    // Filters for Products
    private String productQuery = "";
    private String selectedCategory = "";
    private String selectedCraftType = "";
    private String selectedState = "";
    private String selectedDistrict = "";
    private BigDecimal minPrice = null;
    private BigDecimal maxPrice = null;

    // --- Artisans Browsing State ---
    private final MutableLiveData<List<ArtisanProfile>> artisansLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isArtisansLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> artisansError = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> hasMoreArtisans = new MutableLiveData<>(true);

    private int artisanPage = 0;
    private boolean isFetchingArtisans = false;

    // Filters for Artisans
    private String artisanQuery = "";
    private String selectedSkill = "";

    public CustomerViewModel(@NonNull Application application) {
        super(application);
        this.productRepository = new ProductRepository(application);
        this.artisanRepository = new ArtisanRepository(application);
        this.enquiryRepository = new EnquiryRepository(application);
    }

    // --- Products API & Pagination ---
    public LiveData<List<Product>> getProductsLiveData() {
        return productsLiveData;
    }

    public LiveData<Boolean> getIsProductsLoading() {
        return isProductsLoading;
    }

    public LiveData<String> getProductsError() {
        return productsError;
    }

    public LiveData<Boolean> getHasMoreProducts() {
        return hasMoreProducts;
    }

    public void setProductFilters(String query, String category, String craftType, String state, String district, BigDecimal minP, BigDecimal maxP) {
        this.productQuery = query != null ? query.trim() : "";
        this.selectedCategory = category != null ? category.trim() : "";
        this.selectedCraftType = craftType != null ? craftType.trim() : "";
        this.selectedState = state != null ? state.trim() : "";
        this.selectedDistrict = district != null ? district.trim() : "";
        this.minPrice = minP;
        this.maxPrice = maxP;
        refreshProducts();
    }

    public void refreshProducts() {
        productPage = 0;
        hasMoreProducts.setValue(true);
        loadProductPage(0, true);
    }

    public void loadNextProductPage() {
        if (isFetchingProducts || Boolean.FALSE.equals(hasMoreProducts.getValue())) {
            return;
        }
        loadProductPage(productPage + 1, false);
    }

    private void loadProductPage(int page, boolean clearExisting) {
        if (isFetchingProducts) return;
        isFetchingProducts = true;
        isProductsLoading.setValue(true);
        productsError.setValue(null);

        LiveData<NetworkResult<PagedResult<Product>>> call = productRepository.getProducts(
                productQuery.isEmpty() ? null : productQuery,
                selectedCategory.isEmpty() ? null : selectedCategory,
                selectedCraftType.isEmpty() ? null : selectedCraftType,
                selectedState.isEmpty() ? null : selectedState,
                selectedDistrict.isEmpty() ? null : selectedDistrict,
                minPrice,
                maxPrice,
                page,
                PAGE_SIZE
        );

        call.observeForever(new androidx.lifecycle.Observer<>() {
            @Override
            public void onChanged(NetworkResult<PagedResult<Product>> result) {
                if (result == null || result.getStatus() == NetworkResult.Status.LOADING) {
                    return;
                }
                call.removeObserver(this);
                isFetchingProducts = false;
                isProductsLoading.setValue(false);

                if (result.getStatus() == NetworkResult.Status.SUCCESS && result.getData() != null) {
                    PagedResult<Product> pageData = result.getData();
                    productPage = pageData.getPage();
                    hasMoreProducts.setValue(pageData.isHasNext());

                    List<Product> current = clearExisting ? new ArrayList<>() : new ArrayList<>(productsLiveData.getValue() != null ? productsLiveData.getValue() : new ArrayList<>());
                    current.addAll(pageData.getItems());
                    productsLiveData.setValue(current);
                } else {
                    productsError.setValue(result.getMessage() != null ? result.getMessage() : "Failed to load products");
                }
            }
        });
    }

    // --- Artisans API & Pagination ---
    public LiveData<List<ArtisanProfile>> getArtisansLiveData() {
        return artisansLiveData;
    }

    public LiveData<Boolean> getIsArtisansLoading() {
        return isArtisansLoading;
    }

    public LiveData<String> getArtisansError() {
        return artisansError;
    }

    public LiveData<Boolean> getHasMoreArtisans() {
        return hasMoreArtisans;
    }

    public void setArtisanFilters(String query, String skill, String state, String district) {
        this.artisanQuery = query != null ? query.trim() : "";
        this.selectedSkill = skill != null ? skill.trim() : "";
        this.selectedState = state != null ? state.trim() : "";
        this.selectedDistrict = district != null ? district.trim() : "";
        refreshArtisans();
    }

    public void refreshArtisans() {
        artisanPage = 0;
        hasMoreArtisans.setValue(true);
        loadArtisanPage(0, true);
    }

    public void loadNextArtisanPage() {
        if (isFetchingArtisans || Boolean.FALSE.equals(hasMoreArtisans.getValue())) {
            return;
        }
        loadArtisanPage(artisanPage + 1, false);
    }

    private void loadArtisanPage(int page, boolean clearExisting) {
        if (isFetchingArtisans) return;
        isFetchingArtisans = true;
        isArtisansLoading.setValue(true);
        artisansError.setValue(null);

        LiveData<NetworkResult<PagedResult<ArtisanProfile>>> call = artisanRepository.getArtisans(
                artisanQuery.isEmpty() ? null : artisanQuery,
                null,
                selectedSkill.isEmpty() ? null : selectedSkill,
                selectedState.isEmpty() ? null : selectedState,
                selectedDistrict.isEmpty() ? null : selectedDistrict,
                page,
                PAGE_SIZE
        );

        call.observeForever(new androidx.lifecycle.Observer<>() {
            @Override
            public void onChanged(NetworkResult<PagedResult<ArtisanProfile>> result) {
                if (result == null || result.getStatus() == NetworkResult.Status.LOADING) {
                    return;
                }
                call.removeObserver(this);
                isFetchingArtisans = false;
                isArtisansLoading.setValue(false);

                if (result.getStatus() == NetworkResult.Status.SUCCESS && result.getData() != null) {
                    PagedResult<ArtisanProfile> pageData = result.getData();
                    artisanPage = pageData.getPage();
                    hasMoreArtisans.setValue(pageData.isHasNext());

                    List<ArtisanProfile> current = clearExisting ? new ArrayList<>() : new ArrayList<>(artisansLiveData.getValue() != null ? artisansLiveData.getValue() : new ArrayList<>());
                    current.addAll(pageData.getItems());
                    artisansLiveData.setValue(current);
                } else {
                    artisansError.setValue(result.getMessage() != null ? result.getMessage() : "Failed to load artisans");
                }
            }
        });
    }

    // --- Product & Artisan Detail ---
    public LiveData<NetworkResult<Product>> getProductDetail(Long id) {
        return productRepository.getProductById(id);
    }

    public LiveData<NetworkResult<ArtisanProfile>> getArtisanDetail(Long id) {
        return artisanRepository.getArtisanById(id);
    }

    // --- Enquiries ---
    public LiveData<NetworkResult<EnquiryItem>> sendEnquiry(EnquiryRequest request) {
        return enquiryRepository.sendEnquiry(request);
    }

    public LiveData<NetworkResult<List<EnquiryItem>>> getMyEnquiries() {
        return enquiryRepository.getMyEnquiries();
    }

    // Filter getters
    public String getProductQuery() { return productQuery; }
    public String getSelectedCategory() { return selectedCategory; }
    public String getSelectedCraftType() { return selectedCraftType; }
    public String getSelectedState() { return selectedState; }
    public String getSelectedDistrict() { return selectedDistrict; }
    public BigDecimal getMinPrice() { return minPrice; }
    public BigDecimal getMaxPrice() { return maxPrice; }
}
