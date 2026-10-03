package com.kalaconnect.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kalaconnect.models.ApiResponse;
import com.kalaconnect.models.NotificationItem;
import com.kalaconnect.network.ApiClient;
import com.kalaconnect.network.ApiService;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.utils.ErrorUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRepository {

    private final ApiService apiService;

    public NotificationRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    public LiveData<NetworkResult<List<NotificationItem>>> getNotifications(Boolean unreadOnly) {
        MutableLiveData<NetworkResult<List<NotificationItem>>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getNotifications(unreadOnly).enqueue(new Callback<ApiResponse<List<NotificationItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<NotificationItem>>> call, Response<ApiResponse<List<NotificationItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<NotificationItem>>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<Void>> markAsRead(Long id) {
        MutableLiveData<NetworkResult<Void>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.markNotificationAsRead(id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    result.setValue(NetworkResult.success(null));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }
}
