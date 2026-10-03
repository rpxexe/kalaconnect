package com.kalaconnect.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.kalaconnect.models.AdminDashboardMetrics;
import com.kalaconnect.models.ArtisanProfile;
import com.kalaconnect.models.EnquiryItem;
import com.kalaconnect.models.PagedResult;
import com.kalaconnect.models.Product;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.repository.AdminRepository;

import java.util.List;

public class AdminViewModel extends AndroidViewModel {

    private final AdminRepository adminRepository;

    private final MediatorLiveData<NetworkResult<AdminDashboardMetrics>> dashboardMetricsLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<NetworkResult<PagedResult<ArtisanProfile>>> artisansLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<NetworkResult<PagedResult<Product>>> productsLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<NetworkResult<List<EnquiryItem>>> enquiriesLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<NetworkResult<ArtisanProfile>> artisanActionLiveData = new MediatorLiveData<>();

    public AdminViewModel(@NonNull Application application) {
        super(application);
        this.adminRepository = new AdminRepository(application.getApplicationContext());
    }

    public LiveData<NetworkResult<AdminDashboardMetrics>> getDashboardMetricsLiveData() {
        return dashboardMetricsLiveData;
    }

    public LiveData<NetworkResult<PagedResult<ArtisanProfile>>> getArtisansLiveData() {
        return artisansLiveData;
    }

    public LiveData<NetworkResult<PagedResult<Product>>> getProductsLiveData() {
        return productsLiveData;
    }

    public LiveData<NetworkResult<List<EnquiryItem>>> getEnquiriesLiveData() {
        return enquiriesLiveData;
    }

    public LiveData<NetworkResult<ArtisanProfile>> getArtisanActionLiveData() {
        return artisanActionLiveData;
    }

    public void loadDashboardMetrics() {
        dashboardMetricsLiveData.addSource(adminRepository.getDashboardMetrics(), result -> {
            dashboardMetricsLiveData.setValue(result);
        });
    }

    public void loadArtisans(String search, String status, int page, int size) {
        artisansLiveData.addSource(adminRepository.getArtisans(search, status, page, size), result -> {
            artisansLiveData.setValue(result);
        });
    }

    public void approveArtisan(Long id, String reason) {
        artisanActionLiveData.addSource(adminRepository.approveArtisan(id, reason), result -> {
            artisanActionLiveData.setValue(result);
            if (result != null && result.getStatus() == NetworkResult.Status.SUCCESS) {
                // Refresh dashboard and artisan list
                loadDashboardMetrics();
            }
        });
    }

    public void rejectArtisan(Long id, String reason) {
        artisanActionLiveData.addSource(adminRepository.rejectArtisan(id, reason), result -> {
            artisanActionLiveData.setValue(result);
            if (result != null && result.getStatus() == NetworkResult.Status.SUCCESS) {
                // Refresh dashboard and artisan list
                loadDashboardMetrics();
            }
        });
    }

    public void loadProducts(String search, String category, String status, int page, int size) {
        productsLiveData.addSource(adminRepository.getProducts(search, category, status, page, size), result -> {
            productsLiveData.setValue(result);
        });
    }

    public void loadEnquiries(String status) {
        enquiriesLiveData.addSource(adminRepository.getEnquiries(status), result -> {
            enquiriesLiveData.setValue(result);
        });
    }
}
