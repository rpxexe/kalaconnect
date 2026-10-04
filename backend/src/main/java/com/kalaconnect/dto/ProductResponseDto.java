package com.kalaconnect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponseDto {

    private Long id;
    private Long artisanId;
    private String artisanName;
    private String shgName;
    private Long artisanProfileId;
    private String name;
    private String category;
    private String material;
    private String craftType;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String location;
    private String status;
    private String availability;
    private Integer views = 0;
    private Integer enquiries = 0;
    private List<String> photos = new ArrayList<>();
    private String primaryPhoto;
    private String aiDescription;
    private String aiCaption;
    private String aiHashtags;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public ProductResponseDto() {
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

    public String getArtisanName() {
        return artisanName;
    }

    public void setArtisanName(String artisanName) {
        this.artisanName = artisanName;
    }

    public String getShgName() {
        return shgName;
    }

    public void setShgName(String shgName) {
        this.shgName = shgName;
    }

    public Long getArtisanProfileId() {
        return artisanProfileId;
    }

    public void setArtisanProfileId(Long artisanProfileId) {
        this.artisanProfileId = artisanProfileId;
    }

    public String getAvailability() {
        return availability != null ? availability : status;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
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

    public Integer getViews() {
        return views != null ? views : 0;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getEnquiries() {
        return enquiries != null ? enquiries : 0;
    }

    public void setEnquiries(Integer enquiries) {
        this.enquiries = enquiries;
    }

    public List<String> getPhotos() {
        return photos != null ? photos : new ArrayList<>();
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos != null ? photos : new ArrayList<>();
        if (!this.photos.isEmpty() && (this.primaryPhoto == null || this.primaryPhoto.isEmpty())) {
            this.primaryPhoto = this.photos.get(0);
        }
    }

    public String getPrimaryPhoto() {
        return primaryPhoto;
    }

    public void setPrimaryPhoto(String primaryPhoto) {
        this.primaryPhoto = primaryPhoto;
    }

    public String getImageUrl() {
        return primaryPhoto;
    }

    public void setImageUrl(String imageUrl) {
        if (this.primaryPhoto == null || this.primaryPhoto.isEmpty()) {
            this.primaryPhoto = imageUrl;
        }
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
}
