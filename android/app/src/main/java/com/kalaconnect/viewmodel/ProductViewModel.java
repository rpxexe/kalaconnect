package com.kalaconnect.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.kalaconnect.models.AiProductContentRequest;
import com.kalaconnect.models.AiProductContentResponse;
import com.kalaconnect.models.Product;
import com.kalaconnect.models.ProductRequest;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.repository.ProductRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {

    private final ProductRepository productRepository;

    private final MediatorLiveData<NetworkResult<List<Product>>> myProductsResult = new MediatorLiveData<>();
    private final MediatorLiveData<NetworkResult<Product>> createProductResult = new MediatorLiveData<>();
    private final MediatorLiveData<NetworkResult<Product>> updateProductResult = new MediatorLiveData<>();
    private final MediatorLiveData<NetworkResult<Void>> deleteProductResult = new MediatorLiveData<>();
    private final MediatorLiveData<NetworkResult<Product>> statusUpdateResult = new MediatorLiveData<>();
    private final MediatorLiveData<NetworkResult<AiProductContentResponse>> aiContentResult = new MediatorLiveData<>();

    public ProductViewModel(@NonNull Application application) {
        super(application);
        this.productRepository = new ProductRepository(application);
    }

    public LiveData<NetworkResult<List<Product>>> getMyProductsResult() {
        return myProductsResult;
    }

    public LiveData<NetworkResult<Product>> getCreateProductResult() {
        return createProductResult;
    }

    public LiveData<NetworkResult<Product>> getUpdateProductResult() {
        return updateProductResult;
    }

    public LiveData<NetworkResult<Void>> getDeleteProductResult() {
        return deleteProductResult;
    }

    public LiveData<NetworkResult<Product>> getStatusUpdateResult() {
        return statusUpdateResult;
    }

    public void loadMyProducts() {
        LiveData<NetworkResult<List<Product>>> source = productRepository.getMyProducts();
        myProductsResult.addSource(source, result -> {
            myProductsResult.setValue(result);
            if (result.getStatus() != NetworkResult.Status.LOADING) {
                myProductsResult.removeSource(source);
            }
        });
    }

    public void createProduct(ProductRequest request) {
        LiveData<NetworkResult<Product>> source = productRepository.createProduct(request);
        createProductResult.addSource(source, result -> {
            createProductResult.setValue(result);
            if (result.getStatus() != NetworkResult.Status.LOADING) {
                createProductResult.removeSource(source);
            }
        });
    }

    public void updateProduct(Long id, ProductRequest request) {
        LiveData<NetworkResult<Product>> source = productRepository.updateProduct(id, request);
        updateProductResult.addSource(source, result -> {
            updateProductResult.setValue(result);
            if (result.getStatus() != NetworkResult.Status.LOADING) {
                updateProductResult.removeSource(source);
            }
        });
    }

    public void deleteProduct(Long id) {
        LiveData<NetworkResult<Void>> source = productRepository.deleteProduct(id);
        deleteProductResult.addSource(source, result -> {
            deleteProductResult.setValue(result);
            if (result.getStatus() != NetworkResult.Status.LOADING) {
                deleteProductResult.removeSource(source);
            }
        });
    }

    public void publishProduct(Long id) {
        updateStatus(id, "PUBLISHED");
    }

    public void unpublishProduct(Long id) {
        updateStatus(id, "UNPUBLISHED");
    }

    private void updateStatus(Long id, String status) {
        LiveData<NetworkResult<Product>> source = productRepository.updateProductStatus(id, status);
        statusUpdateResult.addSource(source, result -> {
            statusUpdateResult.setValue(result);
            if (result.getStatus() != NetworkResult.Status.LOADING) {
                statusUpdateResult.removeSource(source);
            }
        });
    }

    public LiveData<NetworkResult<AiProductContentResponse>> getAiContentResult() {
        return aiContentResult;
    }

    public void generateAiContent(AiProductContentRequest request) {
        LiveData<NetworkResult<AiProductContentResponse>> source = productRepository.generateAiProductContent(request);
        aiContentResult.addSource(source, result -> {
            aiContentResult.setValue(result);
            if (result.getStatus() != NetworkResult.Status.LOADING) {
                aiContentResult.removeSource(source);
            }
        });
    }

    public void clearAiContentResult() {
        aiContentResult.setValue(null);
    }
}
