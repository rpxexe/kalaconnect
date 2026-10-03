package com.kalaconnect.model;

import java.time.OffsetDateTime;

public class ProductImage {
    private Long id;
    private Long productId;
    private String imageUrl;
    private boolean primary = false;
    private Integer displayOrder = 0;
    private OffsetDateTime createdAt;

    public ProductImage() {
    }

    public ProductImage(Long id, Long productId, String imageUrl, boolean primary, Integer displayOrder, OffsetDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.primary = primary;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
