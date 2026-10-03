package com.kalaconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class StatusUpdateRequestDto {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(?i)(PENDING|CONTACTED|RESOLVED|CLOSED|RESPONDED)$",
            message = "Status must be PENDING, CONTACTED, RESOLVED, or CLOSED")
    private String status;

    public StatusUpdateRequestDto() {
    }

    public StatusUpdateRequestDto(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
