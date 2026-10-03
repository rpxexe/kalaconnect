package com.kalaconnect.dto;

public class AdminActionRequestDto {

    private String reason;

    public AdminActionRequestDto() {
    }

    public AdminActionRequestDto(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
