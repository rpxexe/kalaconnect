package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EnquiryRequest implements Serializable {

    @SerializedName("productId")
    private Long productId;

    @SerializedName("artisanId")
    private Long artisanId;

    @SerializedName("message")
    private String message;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("customerEmail")
    private String customerEmail;

    @SerializedName("customerPhone")
    private String customerPhone;

    public EnquiryRequest() {
    }

    public EnquiryRequest(Long productId, Long artisanId, String message) {
        this.productId = productId;
        this.artisanId = artisanId;
        this.message = message;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
}
