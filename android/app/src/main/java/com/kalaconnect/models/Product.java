package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("artisanId")
    private Long artisanId;

    @SerializedName("artisanName")
    private String artisanName;

    @SerializedName("name")
    private String name;

    @SerializedName("category")
    private String category;

    @SerializedName("material")
    private String material;

    @SerializedName("craftType")
    private String craftType;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private String price;

    @SerializedName("quantity")
    private Integer quantity = 1;

    @SerializedName("location")
    private String location;

    @SerializedName("status")
    private String status = "PUBLISHED";

    @SerializedName("views")
    private Integer views = 0;

    @SerializedName("enquiries")
    private Integer enquiries = 0;

    @SerializedName("shgName")
    private String shgName;

    @SerializedName("artisanProfileId")
    private Long artisanProfileId;

    @SerializedName("availability")
    private String availability;

    @SerializedName("aiDescription")
    private String aiDescription;

    @SerializedName("aiCaption")
    private String aiCaption;

    @SerializedName("aiHashtags")
    private String aiHashtags;

    @SerializedName("photos")
    private List<String> photos = new ArrayList<>();

    @SerializedName("primaryPhoto")
    private String primaryPhoto;

    @SerializedName("imageUrl")
    private String imageUrl;

    private int imageResId;

    public Product() {
    }

    public Product(Long id, String name, String artisanName, String price, String location, int imageResId, String category) {
        this.id = id;
        this.name = name;
        this.artisanName = artisanName;
        this.price = price;
        this.location = location;
        this.imageResId = imageResId;
        this.category = category;
        this.status = "PUBLISHED";
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity != null ? quantity : 1;
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
        return status != null ? status : "PUBLISHED";
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
    }

    public String getPrimaryPhoto() {
        if (primaryPhoto != null && !primaryPhoto.isEmpty()) {
            return primaryPhoto;
        }
        if (photos != null && !photos.isEmpty()) {
            return photos.get(0);
        }
        return imageUrl;
    }

    public void setPrimaryPhoto(String primaryPhoto) {
        this.primaryPhoto = primaryPhoto;
    }

    public String getImageUrl() {
        return getPrimaryPhoto();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public boolean isPublished() {
        return "PUBLISHED".equalsIgnoreCase(status) || "AVAILABLE".equalsIgnoreCase(status);
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
}
