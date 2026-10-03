package com.kalaconnect.dto;

public class InactiveArtisanDto {

    private Long id;
    private String artisanName;
    private String shgName;
    private String district;
    private int productCount;

    public InactiveArtisanDto() {
    }

    public InactiveArtisanDto(Long id, String artisanName, String shgName, String district, int productCount) {
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

    public String getDistrict() {
        return district;
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
