package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AnalyticsData implements Serializable {

    @SerializedName("totalArtisans")
    private long totalArtisans;

    @SerializedName("totalShgs")
    private long totalShgs;

    @SerializedName("totalProducts")
    private long totalProducts;

    @SerializedName("totalEnquiries")
    private long totalEnquiries;

    @SerializedName("productsPerDistrict")
    private List<AnalyticsMetricItem> productsPerDistrict = new ArrayList<>();

    @SerializedName("artisansPerDistrict")
    private List<AnalyticsMetricItem> artisansPerDistrict = new ArrayList<>();

    @SerializedName("productsByCraftCategory")
    private List<AnalyticsMetricItem> productsByCraftCategory = new ArrayList<>();

    @SerializedName("enquiriesByProduct")
    private List<AnalyticsMetricItem> enquiriesByProduct = new ArrayList<>();

    @SerializedName("enquiriesByArtisan")
    private List<AnalyticsMetricItem> enquiriesByArtisan = new ArrayList<>();

    @SerializedName("productsWithLowViews")
    private List<LowViewProduct> productsWithLowViews = new ArrayList<>();

    @SerializedName("inactiveArtisans")
    private List<InactiveArtisan> inactiveArtisans = new ArrayList<>();

    @SerializedName("districtsWithLowParticipation")
    private List<AnalyticsMetricItem> districtsWithLowParticipation = new ArrayList<>();

    @SerializedName("skillsWithLowRepresentation")
    private List<AnalyticsMetricItem> skillsWithLowRepresentation = new ArrayList<>();

    public AnalyticsData() {
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

    public List<AnalyticsMetricItem> getProductsPerDistrict() {
        return productsPerDistrict != null ? productsPerDistrict : new ArrayList<>();
    }

    public void setProductsPerDistrict(List<AnalyticsMetricItem> productsPerDistrict) {
        this.productsPerDistrict = productsPerDistrict != null ? productsPerDistrict : new ArrayList<>();
    }

    public List<AnalyticsMetricItem> getArtisansPerDistrict() {
        return artisansPerDistrict != null ? artisansPerDistrict : new ArrayList<>();
    }

    public void setArtisansPerDistrict(List<AnalyticsMetricItem> artisansPerDistrict) {
        this.artisansPerDistrict = artisansPerDistrict != null ? artisansPerDistrict : new ArrayList<>();
    }

    public List<AnalyticsMetricItem> getProductsByCraftCategory() {
        return productsByCraftCategory != null ? productsByCraftCategory : new ArrayList<>();
    }

    public void setProductsByCraftCategory(List<AnalyticsMetricItem> productsByCraftCategory) {
        this.productsByCraftCategory = productsByCraftCategory != null ? productsByCraftCategory : new ArrayList<>();
    }

    public List<AnalyticsMetricItem> getEnquiriesByProduct() {
        return enquiriesByProduct != null ? enquiriesByProduct : new ArrayList<>();
    }

    public void setEnquiriesByProduct(List<AnalyticsMetricItem> enquiriesByProduct) {
        this.enquiriesByProduct = enquiriesByProduct != null ? enquiriesByProduct : new ArrayList<>();
    }

    public List<AnalyticsMetricItem> getEnquiriesByArtisan() {
        return enquiriesByArtisan != null ? enquiriesByArtisan : new ArrayList<>();
    }

    public void setEnquiriesByArtisan(List<AnalyticsMetricItem> enquiriesByArtisan) {
        this.enquiriesByArtisan = enquiriesByArtisan != null ? enquiriesByArtisan : new ArrayList<>();
    }

    public List<LowViewProduct> getProductsWithLowViews() {
        return productsWithLowViews != null ? productsWithLowViews : new ArrayList<>();
    }

    public void setProductsWithLowViews(List<LowViewProduct> productsWithLowViews) {
        this.productsWithLowViews = productsWithLowViews != null ? productsWithLowViews : new ArrayList<>();
    }

    public List<InactiveArtisan> getInactiveArtisans() {
        return inactiveArtisans != null ? inactiveArtisans : new ArrayList<>();
    }

    public void setInactiveArtisans(List<InactiveArtisan> inactiveArtisans) {
        this.inactiveArtisans = inactiveArtisans != null ? inactiveArtisans : new ArrayList<>();
    }

    public List<AnalyticsMetricItem> getDistrictsWithLowParticipation() {
        return districtsWithLowParticipation != null ? districtsWithLowParticipation : new ArrayList<>();
    }

    public void setDistrictsWithLowParticipation(List<AnalyticsMetricItem> districtsWithLowParticipation) {
        this.districtsWithLowParticipation = districtsWithLowParticipation != null ? districtsWithLowParticipation : new ArrayList<>();
    }

    public List<AnalyticsMetricItem> getSkillsWithLowRepresentation() {
        return skillsWithLowRepresentation != null ? skillsWithLowRepresentation : new ArrayList<>();
    }

    public void setSkillsWithLowRepresentation(List<AnalyticsMetricItem> skillsWithLowRepresentation) {
        this.skillsWithLowRepresentation = skillsWithLowRepresentation != null ? skillsWithLowRepresentation : new ArrayList<>();
    }
}
