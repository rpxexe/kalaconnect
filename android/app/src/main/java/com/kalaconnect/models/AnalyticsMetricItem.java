package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AnalyticsMetricItem implements Serializable {

    @SerializedName("label")
    private String label;

    @SerializedName("count")
    private long count;

    @SerializedName("percentage")
    private double percentage;

    public AnalyticsMetricItem() {
    }

    public AnalyticsMetricItem(String label, long count, double percentage) {
        this.label = label;
        this.count = count;
        this.percentage = percentage;
    }

    public String getLabel() {
        return label != null ? label : "";
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
