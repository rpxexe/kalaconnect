package com.kalaconnect.dto;

public class AnalyticsMetricItemDto {

    private String label;
    private long count;
    private double percentage;

    public AnalyticsMetricItemDto() {
    }

    public AnalyticsMetricItemDto(String label, long count) {
        this.label = label;
        this.count = count;
    }

    public AnalyticsMetricItemDto(String label, long count, double percentage) {
        this.label = label;
        this.count = count;
        this.percentage = percentage;
    }

    public String getLabel() {
        return label;
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
