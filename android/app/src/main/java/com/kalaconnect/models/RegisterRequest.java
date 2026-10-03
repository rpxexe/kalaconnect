package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

    @SerializedName(value = "name", alternate = {"fullName"})
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName(value = "phone", alternate = {"phoneNumber"})
    private String phone;

    @SerializedName("password")
    private String password;

    @SerializedName("role")
    private UserRole role;

    @SerializedName("shgName")
    private String shgName;

    @SerializedName("craftType")
    private String craftType;

    @SerializedName("organizationName")
    private String organizationName;

    public RegisterRequest() {
    }

    public RegisterRequest(String name, String email, String phone, String password, UserRole role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return name;
    }

    public void setFullName(String fullName) {
        this.name = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneNumber() {
        return phone;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phone = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getShgName() {
        return shgName;
    }

    public void setShgName(String shgName) {
        this.shgName = shgName;
    }

    public String getCraftType() {
        return craftType;
    }

    public void setCraftType(String craftType) {
        this.craftType = craftType;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
