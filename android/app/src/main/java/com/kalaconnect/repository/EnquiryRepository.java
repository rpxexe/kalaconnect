package com.kalaconnect.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kalaconnect.models.ApiResponse;
import com.kalaconnect.models.EnquiryItem;
import com.kalaconnect.models.EnquiryRequest;
import com.kalaconnect.network.ApiClient;
import com.kalaconnect.network.ApiService;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.utils.ErrorUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnquiryRepository {

    private final ApiService apiService;

    public EnquiryRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    public LiveData<NetworkResult<EnquiryItem>> sendEnquiry(EnquiryRequest request) {
        MutableLiveData<NetworkResult<EnquiryItem>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.sendEnquiry(request).enqueue(new Callback<ApiResponse<EnquiryItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<EnquiryItem>> call, Response<ApiResponse<EnquiryItem>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<EnquiryItem>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<List<EnquiryItem>>> getMyEnquiries() {
        return getEnquiries(null);
    }

    public LiveData<NetworkResult<List<EnquiryItem>>> getEnquiries(String status) {
        MutableLiveData<NetworkResult<List<EnquiryItem>>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getEnquiries(status).enqueue(new Callback<ApiResponse<List<EnquiryItem>>>() {
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

    public LiveData<NetworkResult<EnquiryItem>> updateEnquiryStatus(Long id, String status) {
        MutableLiveData<NetworkResult<EnquiryItem>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        com.kalaconnect.models.StatusUpdateRequest request = new com.kalaconnect.models.StatusUpdateRequest(status);
        apiService.updateEnquiryStatus(id, request).enqueue(new Callback<ApiResponse<EnquiryItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<EnquiryItem>> call, Response<ApiResponse<EnquiryItem>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<EnquiryItem>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }
}
