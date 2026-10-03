package com.kalaconnect.dto;

public class LowViewProductDto {

    private Long id;
    private String name;
    private String category;
    private String artisanName;
    private int views;

    public LowViewProductDto() {
    }

    public LowViewProductDto(Long id, String name, String category, String artisanName, int views) {
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

    public String getArtisanName() {
        return artisanName;
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
