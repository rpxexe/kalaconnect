package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("artisanId")
    private Long artisanId;

    @SerializedName("artisanName")
    private String artisanName;

    @SerializedName("name")
    private String name;

    @SerializedName("category")
    private String category;

    @SerializedName("material")
    private String material;

    @SerializedName("craftType")
    private String craftType;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private String price;

    @SerializedName("quantity")
    private Integer quantity = 1;

    @SerializedName("location")
    private String location;

    @SerializedName("status")
    private String status = "PUBLISHED";

    @SerializedName("views")
    private Integer views = 0;

    @SerializedName("enquiries")
    private Integer enquiries = 0;

    @SerializedName("shgName")
    private String shgName;

    @SerializedName("artisanProfileId")
    private Long artisanProfileId;

    @SerializedName("availability")
    private String availability;

    @SerializedName("aiDescription")
    private String aiDescription;

    @SerializedName("aiCaption")
    private String aiCaption;

    @SerializedName("aiHashtags")
    private String aiHashtags;

    @SerializedName("photos")
    private List<String> photos = new ArrayList<>();

    @SerializedName("primaryPhoto")
    private String primaryPhoto;

    @SerializedName("imageUrl")
    private String imageUrl;

    private int imageResId;

    public Product() {
    }

    public Product(Long id, String name, String artisanName, String price, String location, int imageResId, String category) {
        this.id = id;
        this.name = name;
        this.artisanName = artisanName;
        this.price = price;
        this.location = location;
        this.imageResId = imageResId;
        this.category = category;
        this.status = "PUBLISHED";
        this.imageUrl = getCategoryFallbackImageUrl();
        this.primaryPhoto = this.imageUrl;
    }

    public Product(Long id, String name, String artisanName, String price, String location, String imageUrl, String category) {
        this.id = id;
        this.name = name;
        this.artisanName = artisanName;
        this.price = price;
        this.location = location;
        this.imageUrl = (imageUrl != null && !imageUrl.isEmpty()) ? imageUrl : getCategoryFallbackImageUrl();
        this.primaryPhoto = this.imageUrl;
        this.category = category;
        this.status = "PUBLISHED";
        this.photos = new ArrayList<>();
        if (this.imageUrl != null) {
            this.photos.add(this.imageUrl);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArtisanId() {
        return artisanId;
    }

    public void setArtisanId(Long artisanId) {
        this.artisanId = artisanId;
    }

    public String getArtisanName() {
        return artisanName;
    }

    public void setArtisanName(String artisanName) {
        this.artisanName = artisanName;
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

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getCraftType() {
        return craftType;
    }

    public void setCraftType(String craftType) {
        this.craftType = craftType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity != null ? quantity : 1;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status != null ? status : "PUBLISHED";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getViews() {
        return views != null ? views : 0;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getEnquiries() {
        return enquiries != null ? enquiries : 0;
    }

    public void setEnquiries(Integer enquiries) {
        this.enquiries = enquiries;
    }

    public List<String> getPhotos() {
        if (photos == null || photos.isEmpty()) {
            List<String> list = new ArrayList<>();
            list.add(getPrimaryPhoto());
            return list;
        }
        List<String> resolved = new ArrayList<>();
        for (String p : photos) {
            String r = resolveImageUrl(p);
            if (r != null && !r.isEmpty()) {
                resolved.add(r);
            }
        }
        return resolved;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos != null ? photos : new ArrayList<>();
    }

    public static String resolveImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        String trimmed = url.trim();
        if (trimmed.startsWith("content://") || trimmed.startsWith("file://") || trimmed.startsWith("data:")) {
            return trimmed;
        }
        // Normalize any /api/images/{id} or http(s)://.../api/images/{id} to use current Constants.BASE_URL
        if (trimmed.contains("/api/images/")) {
            int idx = trimmed.indexOf("/api/images/");
            String subpath = trimmed.substring(idx + 1); // "api/images/{id}"
            String base = com.kalaconnect.utils.Constants.BASE_URL;
            if (!base.endsWith("/")) base = base + "/";
            return base + subpath;
        }
        if (trimmed.startsWith("/")) {
            String base = com.kalaconnect.utils.Constants.BASE_URL;
            if (!base.endsWith("/")) base = base + "/";
            return base + trimmed.substring(1);
        }
        return trimmed;
    }

    public String getPrimaryPhoto() {
        if (primaryPhoto != null && !primaryPhoto.trim().isEmpty()) {
            return resolveImageUrl(primaryPhoto.trim());
        }
        if (photos != null && !photos.isEmpty()) {
            for (String p : photos) {
                if (p != null && !p.trim().isEmpty()) {
                    return resolveImageUrl(p.trim());
                }
            }
        }
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            return resolveImageUrl(imageUrl.trim());
        }
        return getCategoryFallbackImageUrl();
    }

    public void setPrimaryPhoto(String primaryPhoto) {
        this.primaryPhoto = primaryPhoto;
    }

    public String getImageUrl() {
        return getPrimaryPhoto();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategoryFallbackImageUrl() {
        String cat = (category != null ? category : "").toLowerCase();
        String craft = (craftType != null ? craftType : "").toLowerCase();
        String mat = (material != null ? material : "").toLowerCase();
        String n = (name != null ? name : "").toLowerCase();
        String combined = cat + " " + craft + " " + mat + " " + n;

        if (combined.contains("saree") || combined.contains("silk") || combined.contains("ikat")
                || combined.contains("handloom") || combined.contains("cotton") || combined.contains("textile")
                || combined.contains("dupatta") || combined.contains("fabric") || combined.contains("weav")) {
            return "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=800&auto=format&fit=crop&q=80";
        }
        if (combined.contains("terracotta") || combined.contains("pottery") || combined.contains("clay")
                || combined.contains("vessel") || combined.contains("pot") || combined.contains("ceramic")
                || combined.contains("earthen")) {
            return "https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=800&auto=format&fit=crop&q=80";
        }
        if (combined.contains("madhubani") || combined.contains("painting") || combined.contains("canvas")
                || combined.contains("folk") || combined.contains("warli") || combined.contains("pattachitra")
                || combined.contains("art")) {
            return "https://images.unsplash.com/photo-1582562124811-c09040d0a901?w=800&auto=format&fit=crop&q=80";
        }
        if (combined.contains("dhokra") || combined.contains("bell metal") || combined.contains("bronze")
                || combined.contains("brass") || combined.contains("metal") || combined.contains("tribal")) {
            return "https://images.unsplash.com/photo-1590736969955-71cc94801759?w=800&auto=format&fit=crop&q=80";
        }
        if (combined.contains("wood") || combined.contains("wooden") || combined.contains("teak")
                || combined.contains("walnut") || combined.contains("carv") || combined.contains("keepsake")) {
            return "https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=800&auto=format&fit=crop&q=80";
        }
        if (combined.contains("jewel") || combined.contains("ring") || combined.contains("necklace")
                || combined.contains("earring") || combined.contains("bangle") || combined.contains("bracelet")
                || combined.contains("bead") || combined.contains("ornament")) {
            return "https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=800&auto=format&fit=crop&q=80";
        }
        if (combined.contains("leather") || combined.contains("wallet") || combined.contains("bag")
                || combined.contains("belt") || combined.contains("salmon") || combined.contains("skin")) {
            return "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=800&auto=format&fit=crop&q=80";
        }
        if (combined.contains("paper") || combined.contains("diary") || combined.contains("journal")
                || combined.contains("stationery") || combined.contains("card")) {
            return "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800&auto=format&fit=crop&q=80";
        }
        if (combined.contains("bamboo") || combined.contains("cane") || combined.contains("basket")) {
            return "https://images.unsplash.com/photo-1590736969955-71cc94801759?w=800&auto=format&fit=crop&q=80";
        }

        // Generic authentic Indian handicraft fallback
        return "https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=800&auto=format&fit=crop&q=80";
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public boolean isPublished() {
        return "PUBLISHED".equalsIgnoreCase(status) || "AVAILABLE".equalsIgnoreCase(status);
    }

    public String getShgName() {
        return shgName;
    }

    public void setShgName(String shgName) {
        this.shgName = shgName;
    }

    public Long getArtisanProfileId() {
        return artisanProfileId;
    }

    public void setArtisanProfileId(Long artisanProfileId) {
        this.artisanProfileId = artisanProfileId;
    }

    public String getAvailability() {
        return availability != null ? availability : status;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getAiDescription() {
        return aiDescription;
    }

    public void setAiDescription(String aiDescription) {
        this.aiDescription = aiDescription;
    }

    public String getAiCaption() {
        return aiCaption;
    }

    public void setAiCaption(String aiCaption) {
        this.aiCaption = aiCaption;
    }

    public String getAiHashtags() {
        return aiHashtags;
    }

    public void setAiHashtags(String aiHashtags) {
        this.aiHashtags = aiHashtags;
    }
}
