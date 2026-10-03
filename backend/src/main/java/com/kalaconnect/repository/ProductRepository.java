package com.kalaconnect.repository;

import com.kalaconnect.model.Product;
import com.kalaconnect.model.ProductImage;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findByArtisanId(Long artisanId);

    List<Product> searchProducts(String category, String searchQuery, String status, int limit, int offset);

    List<Product> searchProductsWithFilters(
            String searchQuery,
            String category,
            String craftType,
            String state,
            String district,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            String status,
            int limit,
            int offset
    );

    long countProductsWithFilters(
            String searchQuery,
            String category,
            String craftType,
            String state,
            String district,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            String status
    );

    void addProductImage(ProductImage image);

    List<ProductImage> getProductImages(Long productId);

    void deleteProductImages(Long productId);

    void deleteById(Long id);

    void updateStatus(Long id, String status);

    void incrementViews(Long id);
}
