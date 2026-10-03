package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class StatusUpdateRequest implements Serializable {

    @SerializedName("status")
    private String status;

    public StatusUpdateRequest() {
    }

    public StatusUpdateRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
