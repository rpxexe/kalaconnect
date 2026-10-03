package com.kalaconnect.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtisanProfileDto {

    private Long id;
    private Long userId;

    @JsonAlias({"name", "artisan_name"})
    private String artisanName;

    @JsonAlias({"shg", "shg_name"})
    private String shgName;

    @JsonAlias({"profile_photo", "profile_image", "avatar"})
    private String profilePhoto;

    private String bio;

    @JsonAlias({"location", "village_city", "city"})
    private String villageCity;

    private String district;
    private String state;

    private List<String> skills = new ArrayList<>();
    private Integer experience = 0;

    @JsonAlias({"contact_preference", "contactPreference"})
    private String contactPreference = "PHONE";

    private String approvalStatus = "PENDING";
    private boolean verified = false;

    @JsonAlias({"completion_percentage", "completionPercentage"})
    private Integer completionPercentage = 0;

    private List<ProductResponseDto> products = new ArrayList<>();

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public ArtisanProfileDto() {
    }

    public List<ProductResponseDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductResponseDto> products) {
        this.products = products != null ? products : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getVillageCity() {
        return villageCity;
    }

    public void setVillageCity(String villageCity) {
        this.villageCity = villageCity;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getSkills() {
        return skills != null ? skills : new ArrayList<>();
    }

    public void setSkills(List<String> skills) {
        this.skills = skills != null ? skills : new ArrayList<>();
    }

    public Integer getExperience() {
        return experience != null ? experience : 0;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public String getContactPreference() {
        return contactPreference != null ? contactPreference : "PHONE";
    }

    public void setContactPreference(String contactPreference) {
        this.contactPreference = contactPreference;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public boolean isVerified() {
        return verified || "APPROVED".equalsIgnoreCase(approvalStatus);
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Integer getCompletionPercentage() {
        return completionPercentage != null ? completionPercentage : 0;
    }

    public void setCompletionPercentage(Integer completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
