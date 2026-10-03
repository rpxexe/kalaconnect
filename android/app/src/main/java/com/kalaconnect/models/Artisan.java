package com.kalaconnect.models;

import java.io.Serializable;

public class Artisan implements Serializable {

    private Long id;
    private String name;
    private String craftSpecialty;
    private String location;
    private String bio;
    private boolean verified;
    private int productCount;
    private int avatarResId;
    private String avatarUrl;

    public Artisan() {
    }

    public Artisan(Long id, String name, String craftSpecialty, String location, String bio, boolean verified, int productCount, int avatarResId) {
        this.id = id;
        this.name = name;
        this.craftSpecialty = craftSpecialty;
        this.location = location;
        this.bio = bio;
        this.verified = verified;
        this.productCount = productCount;
        this.avatarResId = avatarResId;
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

    public String getCraftSpecialty() {
        return craftSpecialty;
    }

    public void setCraftSpecialty(String craftSpecialty) {
        this.craftSpecialty = craftSpecialty;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public void setAvatarResId(int avatarResId) {
        this.avatarResId = avatarResId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public static Artisan fromProfile(ArtisanProfile profile) {
        if (profile == null) return null;
        Artisan a = new Artisan();
        a.setId(profile.getId());
        String name = profile.getArtisanName();
        if (name == null || name.isBlank()) {
            name = profile.getShgName() != null ? profile.getShgName() : "Master Artisan";
        }
        a.setName(name);

        String specialty = "Traditional Handicraft";
        if (profile.getSkills() != null && !profile.getSkills().isEmpty()) {
            specialty = String.join(" • ", profile.getSkills());
        }
        a.setCraftSpecialty(specialty);

        StringBuilder loc = new StringBuilder();
        if (profile.getVillageCity() != null && !profile.getVillageCity().isBlank()) {
            loc.append(profile.getVillageCity());
        }
        if (profile.getDistrict() != null && !profile.getDistrict().isBlank()) {
            if (loc.length() > 0) loc.append(", ");
            loc.append(profile.getDistrict());
        }
        if (profile.getState() != null && !profile.getState().isBlank()) {
            if (loc.length() > 0) loc.append(", ");
            loc.append(profile.getState());
        }
        a.setLocation(loc.length() > 0 ? loc.toString() : "India");
        a.setBio(profile.getBio() != null ? profile.getBio() : "Traditional artisan collective preserving heritage handicraft.");
        a.setVerified(profile.isVerified());
        a.setProductCount(profile.getProducts() != null ? profile.getProducts().size() : 8);
        a.setAvatarUrl(profile.getProfilePhoto());
        return a;
    }
}
