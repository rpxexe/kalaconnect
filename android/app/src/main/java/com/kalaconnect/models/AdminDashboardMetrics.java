package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AdminDashboardMetrics implements Serializable {

    @SerializedName("totalShgs")
    private long totalShgs;

    @SerializedName("totalArtisans")
    private long totalArtisans;

    @SerializedName("totalProducts")
    private long totalProducts;

    @SerializedName("totalEnquiries")
    private long totalEnquiries;

    @SerializedName("pendingApprovals")
    private long pendingApprovals;

    @SerializedName("activeArtisans")
    private long activeArtisans;

    @SerializedName("pendingEnquiries")
    private long pendingEnquiries;

    @SerializedName("contactedEnquiries")
    private long contactedEnquiries;

    @SerializedName("resolvedEnquiries")
    private long resolvedEnquiries;

    @SerializedName("closedEnquiries")
    private long closedEnquiries;

    @SerializedName("rejectedArtisans")
    private long rejectedArtisans;

    public AdminDashboardMetrics() {
    }

    public long getTotalShgs() {
        return totalShgs;
    }

    public void setTotalShgs(long totalShgs) {
        this.totalShgs = totalShgs;
    }

    public long getTotalArtisans() {
        return totalArtisans;
    }

    public void setTotalArtisans(long totalArtisans) {
        this.totalArtisans = totalArtisans;
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

    public long getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(long pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }

    public long getActiveArtisans() {
        return activeArtisans;
    }

    public void setActiveArtisans(long activeArtisans) {
        this.activeArtisans = activeArtisans;
    }

    public long getPendingEnquiries() {
        return pendingEnquiries;
    }

    public void setPendingEnquiries(long pendingEnquiries) {
        this.pendingEnquiries = pendingEnquiries;
    }

    public long getContactedEnquiries() {
        return contactedEnquiries;
    }

    public void setContactedEnquiries(long contactedEnquiries) {
        this.contactedEnquiries = contactedEnquiries;
    }

    public long getResolvedEnquiries() {
        return resolvedEnquiries;
    }

    public void setResolvedEnquiries(long resolvedEnquiries) {
        this.resolvedEnquiries = resolvedEnquiries;
    }

    public long getClosedEnquiries() {
        return closedEnquiries;
    }

    public void setClosedEnquiries(long closedEnquiries) {
        this.closedEnquiries = closedEnquiries;
    }

    public long getRejectedArtisans() {
        return rejectedArtisans;
    }

    public void setRejectedArtisans(long rejectedArtisans) {
        this.rejectedArtisans = rejectedArtisans;
    }
}
