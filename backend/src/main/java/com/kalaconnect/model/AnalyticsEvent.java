package com.kalaconnect.model;

import java.time.OffsetDateTime;

public class AnalyticsEvent {
    private Long id;
    private Long userId;
    private String eventType;
    private String eventData;
    private String ipAddress;
    private OffsetDateTime createdAt;

    public AnalyticsEvent() {
    }

    public AnalyticsEvent(Long id, Long userId, String eventType, String eventData, String ipAddress, OffsetDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.eventType = eventType;
        this.eventData = eventData;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
