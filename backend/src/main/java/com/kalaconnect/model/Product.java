package com.kalaconnect.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private Long id;
    private Long artisanId;
    private String name;
    private String category;
    private String material;
    private String craftType;
    private String description;
    private BigDecimal price;
    private Integer quantity = 1;
    private String location;
    private String status = "AVAILABLE";
    private Integer viewsCount = 0;
    private Integer enquiriesCount = 0;
    private String aiDescription;
    private String aiCaption;
    private String aiHashtags;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Associated images helper
    private List<ProductImage> images = new ArrayList<>();

    public Product() {
    }

    public Product(Long id, Long artisanId, String name, String category, String material,
                   String craftType, String description, BigDecimal price, Integer quantity,
                   String location, String status, String aiDescription, String aiCaption,
                   String aiHashtags, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.artisanId = artisanId;
        this.name = name;
        this.category = category;
        this.material = material;
        this.craftType = craftType;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.location = location;
        this.status = status;
        this.aiDescription = aiDescription;
        this.aiCaption = aiCaption;
        this.aiHashtags = aiHashtags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArtisanId() {
        return artisanId;
    }

    public void setArtisanId(Long artisanId) {
        this.artisanId = artisanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getCraftType() {
        return craftType;
    }

    public void setCraftType(String craftType) {
        this.craftType = craftType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAiDescription() {
        return aiDescription;
    }

    public void setAiDescription(String aiDescription) {
        this.aiDescription = aiDescription;
    }

    public String getAiCaption() {
        return aiCaption;
    }

    public void setAiCaption(String aiCaption) {
        this.aiCaption = aiCaption;
    }

    public String getAiHashtags() {
        return aiHashtags;
    }

    public void setAiHashtags(String aiHashtags) {
        this.aiHashtags = aiHashtags;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getViewsCount() {
        return viewsCount != null ? viewsCount : 0;
    }

    public void setViewsCount(Integer viewsCount) {
        this.viewsCount = viewsCount;
    }

    public Integer getEnquiriesCount() {
        return enquiriesCount != null ? enquiriesCount : 0;
    }

    public void setEnquiriesCount(Integer enquiriesCount) {
        this.enquiriesCount = enquiriesCount;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }
}
