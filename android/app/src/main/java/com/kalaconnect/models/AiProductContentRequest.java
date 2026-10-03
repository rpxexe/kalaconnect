package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AiProductContentRequest implements Serializable {

    @SerializedName("productName")
    private String productName;

    @SerializedName("material")
    private String material;

    @SerializedName("craftType")
    private String craftType;

    @SerializedName("location")
    private String location;

    @SerializedName("features")
    private String features;

    public AiProductContentRequest() {
    }

    public AiProductContentRequest(String productName, String material, String craftType, String location, String features) {
        this.productName = productName;
        this.material = material;
        this.craftType = craftType;
        this.location = location;
        this.features = features;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }
}
