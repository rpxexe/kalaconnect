package com.kalaconnect.dto;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsDashboardResponseDto {

    private long totalArtisans;
    private long totalShgs;
    private long totalProducts;
    private long totalEnquiries;

    private List<AnalyticsMetricItemDto> productsPerDistrict = new ArrayList<>();
    private List<AnalyticsMetricItemDto> artisansPerDistrict = new ArrayList<>();
    private List<AnalyticsMetricItemDto> productsByCraftCategory = new ArrayList<>();
    private List<AnalyticsMetricItemDto> enquiriesByProduct = new ArrayList<>();
    private List<AnalyticsMetricItemDto> enquiriesByArtisan = new ArrayList<>();
    private List<LowViewProductDto> productsWithLowViews = new ArrayList<>();
    private List<InactiveArtisanDto> inactiveArtisans = new ArrayList<>();
    private List<AnalyticsMetricItemDto> districtsWithLowParticipation = new ArrayList<>();
    private List<AnalyticsMetricItemDto> skillsWithLowRepresentation = new ArrayList<>();

    public AnalyticsDashboardResponseDto() {
    }

    public long getTotalArtisans() {
        return totalArtisans;
    }

    public void setTotalArtisans(long totalArtisans) {
        this.totalArtisans = totalArtisans;
    }

    public long getTotalShgs() {
        return totalShgs;
    }

    public void setTotalShgs(long totalShgs) {
        this.totalShgs = totalShgs;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalEnquiries() {
        return totalEnquiries;
    }

    public void setTotalEnquiries(long totalEnquiries) {
        this.totalEnquiries = totalEnquiries;
    }

    public List<AnalyticsMetricItemDto> getProductsPerDistrict() {
        return productsPerDistrict;
    }

    public void setProductsPerDistrict(List<AnalyticsMetricItemDto> productsPerDistrict) {
        this.productsPerDistrict = productsPerDistrict != null ? productsPerDistrict : new ArrayList<>();
    }

    public List<AnalyticsMetricItemDto> getArtisansPerDistrict() {
        return artisansPerDistrict;
    }

    public void setArtisansPerDistrict(List<AnalyticsMetricItemDto> artisansPerDistrict) {
        this.artisansPerDistrict = artisansPerDistrict != null ? artisansPerDistrict : new ArrayList<>();
    }

    public List<AnalyticsMetricItemDto> getProductsByCraftCategory() {
        return productsByCraftCategory;
    }

    public void setProductsByCraftCategory(List<AnalyticsMetricItemDto> productsByCraftCategory) {
        this.productsByCraftCategory = productsByCraftCategory != null ? productsByCraftCategory : new ArrayList<>();
    }

    public List<AnalyticsMetricItemDto> getEnquiriesByProduct() {
        return enquiriesByProduct;
    }

    public void setEnquiriesByProduct(List<AnalyticsMetricItemDto> enquiriesByProduct) {
        this.enquiriesByProduct = enquiriesByProduct != null ? enquiriesByProduct : new ArrayList<>();
    }

    public List<AnalyticsMetricItemDto> getEnquiriesByArtisan() {
        return enquiriesByArtisan;
    }

    public void setEnquiriesByArtisan(List<AnalyticsMetricItemDto> enquiriesByArtisan) {
        this.enquiriesByArtisan = enquiriesByArtisan != null ? enquiriesByArtisan : new ArrayList<>();
    }

    public List<LowViewProductDto> getProductsWithLowViews() {
        return productsWithLowViews;
    }

    public void setProductsWithLowViews(List<LowViewProductDto> productsWithLowViews) {
        this.productsWithLowViews = productsWithLowViews != null ? productsWithLowViews : new ArrayList<>();
    }

    public List<InactiveArtisanDto> getInactiveArtisans() {
        return inactiveArtisans;
    }

    public void setInactiveArtisans(List<InactiveArtisanDto> inactiveArtisans) {
        this.inactiveArtisans = inactiveArtisans != null ? inactiveArtisans : new ArrayList<>();
    }

    public List<AnalyticsMetricItemDto> getDistrictsWithLowParticipation() {
        return districtsWithLowParticipation;
    }

    public void setDistrictsWithLowParticipation(List<AnalyticsMetricItemDto> districtsWithLowParticipation) {
        this.districtsWithLowParticipation = districtsWithLowParticipation != null ? districtsWithLowParticipation : new ArrayList<>();
    }

    public List<AnalyticsMetricItemDto> getSkillsWithLowRepresentation() {
        return skillsWithLowRepresentation;
    }

    public void setSkillsWithLowRepresentation(List<AnalyticsMetricItemDto> skillsWithLowRepresentation) {
        this.skillsWithLowRepresentation = skillsWithLowRepresentation != null ? skillsWithLowRepresentation : new ArrayList<>();
    }
}
