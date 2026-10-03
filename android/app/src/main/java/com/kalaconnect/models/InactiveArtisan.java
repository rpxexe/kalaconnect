package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class InactiveArtisan implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("artisanName")
    private String artisanName;

    @SerializedName("shgName")
    private String shgName;

    @SerializedName("district")
    private String district;

    @SerializedName("productCount")
    private int productCount;

    public InactiveArtisan() {
    }

    public InactiveArtisan(Long id, String artisanName, String shgName, String district, int productCount) {
        this.id = id;
        this.artisanName = artisanName;
        this.shgName = shgName;
        this.district = district;
        this.productCount = productCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArtisanName() {
        return artisanName != null ? artisanName : "";
    }

    public void setArtisanName(String artisanName) {
        this.artisanName = artisanName;
    }

    public String getShgName() {
        return shgName != null ? shgName : "";
    }

    public void setShgName(String shgName) {
        this.shgName = shgName;
    }

    public String getDistrict() {
        return district != null ? district : "";
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }
}
