package com.kalaconnect.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kalaconnect.models.ApiResponse;
import com.kalaconnect.models.ArtisanProfile;
import com.kalaconnect.network.ApiClient;
import com.kalaconnect.network.ApiService;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.utils.ErrorUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtisanRepository {

    private final ApiService apiService;

    public ArtisanRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    public LiveData<NetworkResult<ArtisanProfile>> getProfile() {
        MutableLiveData<NetworkResult<ArtisanProfile>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getArtisanProfile().enqueue(new Callback<ApiResponse<ArtisanProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<ArtisanProfile>> call, Response<ApiResponse<ArtisanProfile>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    String errorMsg = ErrorUtils.parseHttpError(response);
                    result.setValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ArtisanProfile>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<ArtisanProfile>> updateProfile(ArtisanProfile profile) {
        MutableLiveData<NetworkResult<ArtisanProfile>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.updateArtisanProfile(profile).enqueue(new Callback<ApiResponse<ArtisanProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<ArtisanProfile>> call, Response<ApiResponse<ArtisanProfile>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    String errorMsg = ErrorUtils.parseHttpError(response);
                    result.setValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ArtisanProfile>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<List<String>>> getAvailableSkills() {
        MutableLiveData<NetworkResult<List<String>>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getAvailableSkills().enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    // Fallback to core skills on network fallback
                    result.setValue(NetworkResult.error("Unable to load remote skills"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<com.kalaconnect.models.PagedResult<ArtisanProfile>>> getArtisans(
            String search,
            String craftCategory,
            String skill,
            String state,
            String district,
            int page,
            int size) {

        MutableLiveData<NetworkResult<com.kalaconnect.models.PagedResult<ArtisanProfile>>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getArtisans(search, craftCategory, skill, state, district, page, size)
                .enqueue(new Callback<ApiResponse<com.kalaconnect.models.PagedResult<ArtisanProfile>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<com.kalaconnect.models.PagedResult<ArtisanProfile>>> call,
                                           Response<ApiResponse<com.kalaconnect.models.PagedResult<ArtisanProfile>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            result.setValue(NetworkResult.success(response.body().getData()));
                        } else {
                            result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<com.kalaconnect.models.PagedResult<ArtisanProfile>>> call, Throwable t) {
                        result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
                    }
                });

        return result;
    }

    public LiveData<NetworkResult<ArtisanProfile>> getArtisanById(Long id) {
        MutableLiveData<NetworkResult<ArtisanProfile>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getArtisanById(id).enqueue(new Callback<ApiResponse<ArtisanProfile>>() {
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
}
