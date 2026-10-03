package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductRequest implements Serializable {

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
    private BigDecimal price;

    @SerializedName("quantity")
    private Integer quantity = 1;

    @SerializedName("location")
    private String location;

    @SerializedName("status")
    private String status = "PUBLISHED";

    @SerializedName("photos")
    private List<String> photos = new ArrayList<>();

    @SerializedName("aiDescription")
    private String aiDescription;

    @SerializedName("aiCaption")
    private String aiCaption;

    @SerializedName("aiHashtags")
    private String aiHashtags;

    public ProductRequest() {
    }

    public ProductRequest(String name, String category, String material, String craftType,
                          String description, BigDecimal price, Integer quantity,
                          String location, String status, List<String> photos) {
        this.name = name;
        this.category = category;
        this.material = material;
        this.craftType = craftType;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.location = location;
        this.status = status;
        this.photos = photos;
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
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getPhotos() {
        return photos != null ? photos : new ArrayList<>();
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
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
