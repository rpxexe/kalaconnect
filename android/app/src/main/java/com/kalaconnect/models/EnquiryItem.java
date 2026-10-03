package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EnquiryItem implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("productId")
    private Long productId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("customerEmail")
    private String customerEmail;

    @SerializedName("customerPhone")
    private String customerPhone;

    @SerializedName("artisanId")
    private Long artisanId;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status = "PENDING"; // PENDING, CONTACTED, RESOLVED, CLOSED

    @SerializedName("createdAt")
    private String createdAt;

    public EnquiryItem() {
    }

    public EnquiryItem(Long id, Long productId, String productName, String customerName, String message, String status, String createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.customerName = customerName;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public EnquiryItem(Long id, Long productId, String productName, Long artisanId, String customerName, String message, String status, String createdAt, String customerEmail, String customerPhone) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.artisanId = artisanId;
        this.customerName = customerName;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Long getArtisanId() {
        return artisanId;
    }

    public void setArtisanId(Long artisanId) {
        this.artisanId = artisanId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
