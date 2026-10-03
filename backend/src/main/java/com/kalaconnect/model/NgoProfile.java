package com.kalaconnect.model;

import java.time.OffsetDateTime;

public class NgoProfile {
    private Long id;
    private Long userId;
    private String organizationName;
    private String registrationNumber;
    private String operationalRegions;
    private String contactPerson;
    private OffsetDateTime createdAt;

    public NgoProfile() {
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

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getOperationalRegions() {
        return operationalRegions;
    }

    public void setOperationalRegions(String operationalRegions) {
        this.operationalRegions = operationalRegions;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
