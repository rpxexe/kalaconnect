package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArtisanProfile implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("artisanName")
    private String artisanName;

    @SerializedName("shgName")
    private String shgName;

    @SerializedName("profilePhoto")
    private String profilePhoto;

    @SerializedName("bio")
    private String bio;

    @SerializedName("villageCity")
    private String villageCity;

    @SerializedName("district")
    private String district;

    @SerializedName("state")
    private String state;

    @SerializedName("skills")
    private List<String> skills = new ArrayList<>();

    @SerializedName("experience")
    private Integer experience = 0;

    @SerializedName("contactPreference")
    private String contactPreference = "PHONE";

    @SerializedName("approvalStatus")
    private String approvalStatus = "PENDING";

    @SerializedName("verified")
    private boolean verified = false;

    @SerializedName("completionPercentage")
    private Integer completionPercentage = 0;

    @SerializedName("products")
    private List<Product> products = new ArrayList<>();

    public ArtisanProfile() {
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

    public List<Product> getProducts() {
        return products != null ? products : new ArrayList<>();
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
    }
}
