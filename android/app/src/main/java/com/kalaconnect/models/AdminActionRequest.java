package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AdminActionRequest implements Serializable {

    @SerializedName("reason")
    private String reason;

    public AdminActionRequest() {
    }

    public AdminActionRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
