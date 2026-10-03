package com.kalaconnect.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kalaconnect.models.AiProductContentRequest;
import com.kalaconnect.models.AiProductContentResponse;
import com.kalaconnect.models.ApiResponse;
import com.kalaconnect.models.Product;
import com.kalaconnect.models.ProductRequest;
import com.kalaconnect.network.ApiClient;
import com.kalaconnect.network.ApiService;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.utils.ErrorUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {

    private final ApiService apiService;

    public ProductRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    public LiveData<NetworkResult<Product>> createProduct(ProductRequest request) {
        MutableLiveData<NetworkResult<Product>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.createProduct(request).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<List<Product>>> getMyProducts() {
        MutableLiveData<NetworkResult<List<Product>>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getMyProducts().enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<Product>> getProductById(Long id) {
        MutableLiveData<NetworkResult<Product>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getProductById(id).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<Product>> updateProduct(Long id, ProductRequest request) {
        MutableLiveData<NetworkResult<Product>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.updateProduct(id, request).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<Void>> deleteProduct(Long id) {
        MutableLiveData<NetworkResult<Void>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.deleteProduct(id).enqueue(new Callback<ApiResponse<Void>>() {
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

    public LiveData<NetworkResult<Product>> updateProductStatus(Long id, String status) {
        MutableLiveData<NetworkResult<Product>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        Map<String, String> body = new HashMap<>();
        body.put("status", status);

        apiService.updateProductStatus(id, body).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<AiProductContentResponse>> generateAiProductContent(AiProductContentRequest request) {
        MutableLiveData<NetworkResult<AiProductContentResponse>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.generateAiProductContent(request).enqueue(new Callback<AiProductContentResponse>() {
            @Override
            public void onResponse(Call<AiProductContentResponse> call, Response<AiProductContentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AiProductContentResponse content = response.body();
                    if ((content.getDescription() == null || content.getDescription().trim().isEmpty()) &&
                            (content.getCaption() == null || content.getCaption().trim().isEmpty())) {
                        result.setValue(NetworkResult.error("Gemini AI generated an empty response. Please adjust details and retry."));
                    } else {
                        result.setValue(NetworkResult.success(content));
                    }
                } else if (response.code() == 429) {
                    result.setValue(NetworkResult.error("Gemini AI rate limit reached. Please wait a moment before retrying."));
                } else if (response.code() == 504) {
                    result.setValue(NetworkResult.error("Gemini AI request timed out. Please retry."));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                }
            }

            @Override
            public void onFailure(Call<AiProductContentResponse> call, Throwable t) {
                if (t instanceof java.net.SocketTimeoutException) {
                    result.setValue(NetworkResult.error("Gemini AI request timed out. Please check your connection and retry."));
                } else {
                    result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
                }
            }
        });

        return result;
    }

    public LiveData<NetworkResult<com.kalaconnect.models.PagedResult<Product>>> getProducts(
            String search,
            String category,
            String craftType,
            String state,
            String district,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            int page,
            int size) {

        MutableLiveData<NetworkResult<com.kalaconnect.models.PagedResult<Product>>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getProducts(search, category, craftType, state, district, minPrice, maxPrice, page, size)
                .enqueue(new Callback<ApiResponse<com.kalaconnect.models.PagedResult<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<com.kalaconnect.models.PagedResult<Product>>> call,
                                           Response<ApiResponse<com.kalaconnect.models.PagedResult<Product>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            result.setValue(NetworkResult.success(response.body().getData()));
                        } else {
                            result.setValue(NetworkResult.error(ErrorUtils.parseHttpError(response)));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<com.kalaconnect.models.PagedResult<Product>>> call, Throwable t) {
                        result.setValue(NetworkResult.error(ErrorUtils.getNetworkErrorMessage(t)));
                    }
                });

        return result;
    }
}
