package com.kalaconnect.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kalaconnect.models.AdminActionRequest;
import com.kalaconnect.models.AdminDashboardMetrics;
import com.kalaconnect.models.ApiResponse;
import com.kalaconnect.models.ArtisanProfile;
import com.kalaconnect.models.EnquiryItem;
import com.kalaconnect.models.PagedResult;
import com.kalaconnect.models.Product;
import com.kalaconnect.network.ApiClient;
import com.kalaconnect.network.ApiService;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.utils.ErrorUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRepository {

    private final ApiService apiService;

    public AdminRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    public LiveData<NetworkResult<AdminDashboardMetrics>> getDashboardMetrics() {
        MutableLiveData<NetworkResult<AdminDashboardMetrics>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getAdminDashboard().enqueue(new Callback<ApiResponse<AdminDashboardMetrics>>() {
            @Override
            public void onResponse(Call<ApiResponse<AdminDashboardMetrics>> call, Response<ApiResponse<AdminDashboardMetrics>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AdminDashboardMetrics>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<PagedResult<ArtisanProfile>>> getArtisans(String search, String status, int page, int size) {
        MutableLiveData<NetworkResult<PagedResult<ArtisanProfile>>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getAdminArtisans(search, status, page, size).enqueue(new Callback<ApiResponse<PagedResult<ArtisanProfile>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PagedResult<ArtisanProfile>>> call, Response<ApiResponse<PagedResult<ArtisanProfile>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PagedResult<ArtisanProfile>>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<ArtisanProfile>> approveArtisan(Long id, String reason) {
        MutableLiveData<NetworkResult<ArtisanProfile>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        AdminActionRequest req = new AdminActionRequest(reason != null ? reason : "Approved by NGO Admin");
        apiService.approveArtisan(id, req).enqueue(new Callback<ApiResponse<ArtisanProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<ArtisanProfile>> call, Response<ApiResponse<ArtisanProfile>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ArtisanProfile>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<ArtisanProfile>> rejectArtisan(Long id, String reason) {
        MutableLiveData<NetworkResult<ArtisanProfile>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        AdminActionRequest req = new AdminActionRequest(reason != null ? reason : "Rejected by NGO Admin");
        apiService.rejectArtisan(id, req).enqueue(new Callback<ApiResponse<ArtisanProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<ArtisanProfile>> call, Response<ApiResponse<ArtisanProfile>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ArtisanProfile>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<PagedResult<Product>>> getProducts(String search, String category, String status, int page, int size) {
        MutableLiveData<NetworkResult<PagedResult<Product>>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getAdminProducts(search, category, status, page, size).enqueue(new Callback<ApiResponse<PagedResult<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PagedResult<Product>>> call, Response<ApiResponse<PagedResult<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PagedResult<Product>>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<List<EnquiryItem>>> getEnquiries(String status) {
        MutableLiveData<NetworkResult<List<EnquiryItem>>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getAdminEnquiries(status).enqueue(new Callback<ApiResponse<List<EnquiryItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<EnquiryItem>>> call, Response<ApiResponse<List<EnquiryItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<EnquiryItem>>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }
}
