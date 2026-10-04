package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class CertificateItem implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("artisanId")
    private Long artisanId;

    @SerializedName("artisanName")
    private String artisanName;

    @SerializedName("shgName")
    private String shgName;

    @SerializedName("score")
    private int score;

    @SerializedName("totalQuestions")
    private int totalQuestions;

    @SerializedName("percentage")
    private int percentage;

    @SerializedName("status")
    private String status; // PENDING_APPROVAL, APPROVED, REJECTED

    @SerializedName("adminNotes")
    private String adminNotes;

    @SerializedName("approvedBy")
    private String approvedBy;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("approvedAt")
    private String approvedAt;

    public CertificateItem() {}

    public CertificateItem(Long artisanId, String artisanName, String shgName, int score, int totalQuestions, int percentage) {
        this.artisanId = artisanId;
        this.artisanName = artisanName;
        this.shgName = shgName;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.status = "PENDING_APPROVAL";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getArtisanId() { return artisanId; }
    public void setArtisanId(Long artisanId) { this.artisanId = artisanId; }

    public String getArtisanName() { return artisanName; }
    public void setArtisanName(String artisanName) { this.artisanName = artisanName; }

    public String getShgName() { return shgName; }
    public void setShgName(String shgName) { this.shgName = shgName; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getPercentage() { return percentage; }
    public void setPercentage(int percentage) { this.percentage = percentage; }

    public String getStatus() { return status != null ? status : "PENDING_APPROVAL"; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getApprovedAt() { return approvedAt; }
    public void setApprovedAt(String approvedAt) { this.approvedAt = approvedAt; }

    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(status);
    }

    public boolean isPending() {
        return "PENDING_APPROVAL".equalsIgnoreCase(status) || "PENDING".equalsIgnoreCase(status);
    }

    public boolean isRejected() {
        return "REJECTED".equalsIgnoreCase(status);
    }
}
