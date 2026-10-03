package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class LowViewProduct implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("category")
    private String category;

    @SerializedName("artisanName")
    private String artisanName;

    @SerializedName("views")
    private int views;

    public LowViewProduct() {
    }

    public LowViewProduct(Long id, String name, String category, String artisanName, int views) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.artisanName = artisanName;
        this.views = views;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category != null ? category : "";
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getArtisanName() {
        return artisanName != null ? artisanName : "";
    }

    public void setArtisanName(String artisanName) {
        this.artisanName = artisanName;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
}
