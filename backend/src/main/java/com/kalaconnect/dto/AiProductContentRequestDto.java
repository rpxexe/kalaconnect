package com.kalaconnect.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public class AiProductContentRequestDto {

    @NotBlank(message = "Product name is required for AI generation")
    @JsonAlias({"product_name", "name"})
    private String productName;

    private String material;

    @JsonAlias({"craft_type", "craftType"})
    private String craftType;

    private String location;

    private String features;

    public AiProductContentRequestDto() {
    }

    public AiProductContentRequestDto(String productName, String material, String craftType, String location, String features) {
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
