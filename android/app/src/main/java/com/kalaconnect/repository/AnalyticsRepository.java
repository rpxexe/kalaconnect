package com.kalaconnect.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kalaconnect.models.AnalyticsData;
import com.kalaconnect.models.ApiResponse;
import com.kalaconnect.network.ApiClient;
import com.kalaconnect.network.ApiService;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalyticsRepository {

    private final ApiService apiService;

    public AnalyticsRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    public LiveData<NetworkResult<AnalyticsData>> getAnalytics() {
        MutableLiveData<NetworkResult<AnalyticsData>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getAnalytics().enqueue(new Callback<ApiResponse<AnalyticsData>>() {
            @Override
            public void onResponse(Call<ApiResponse<AnalyticsData>> call, Response<ApiResponse<AnalyticsData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AnalyticsData>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }
}
