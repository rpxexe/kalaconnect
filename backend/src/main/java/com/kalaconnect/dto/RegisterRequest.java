package com.kalaconnect.dto;

import com.kalaconnect.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String name;
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "User role is required (ARTISAN, CUSTOMER, NGO_ADMIN)")
    private UserRole role;

    // Optional artisan fields
    private String shgName;
    private String craftType;

    // Optional NGO fields
    private String organizationName;

    public RegisterRequest() {
    }

    public String getName() {
        if (name != null && !name.isBlank()) {
            return name;
        }
        return fullName;
    }

    public void setName(String name) {
        this.name = name;
        if (this.fullName == null) {
            this.fullName = name;
        }
    }

    public String getFullName() {
        if (fullName != null && !fullName.isBlank()) {
            return fullName;
        }
        return name;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        if (this.name == null) {
            this.name = fullName;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        if (phone != null && !phone.isBlank()) {
            return phone;
        }
        return phoneNumber;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        if (this.phoneNumber == null) {
            this.phoneNumber = phone;
        }
    }

    public String getPhoneNumber() {
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            return phoneNumber;
        }
        return phone;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        if (this.phone == null) {
            this.phone = phoneNumber;
        }
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
