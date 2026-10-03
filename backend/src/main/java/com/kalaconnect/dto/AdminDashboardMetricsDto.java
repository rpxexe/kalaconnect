package com.kalaconnect.dto;

public class AdminDashboardMetricsDto {

    private long totalShgs;
    private long totalArtisans;
    private long totalProducts;
    private long totalEnquiries;
    private long pendingApprovals;
    private long activeArtisans;

    private long pendingEnquiries;
    private long contactedEnquiries;
    private long resolvedEnquiries;
    private long closedEnquiries;
    private long rejectedArtisans;

    public AdminDashboardMetricsDto() {
    }

    public AdminDashboardMetricsDto(long totalShgs, long totalArtisans, long totalProducts,
                                    long totalEnquiries, long pendingApprovals, long activeArtisans) {
        this.totalShgs = totalShgs;
        this.totalArtisans = totalArtisans;
        this.totalProducts = totalProducts;
        this.totalEnquiries = totalEnquiries;
        this.pendingApprovals = pendingApprovals;
        this.activeArtisans = activeArtisans;
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
