package com.kalaconnect.models;

import java.io.Serializable;
import java.util.List;

public class LearningModule implements Serializable {

    private String id;
    private String title;
    private String subtitle;
    private String category;
    private String duration;
    private String readTime;
    private String badgeText;
    private String summary;
    private List<String> keyPoints;
    private String detailedContent;
    private String practicalTip;
    private boolean isCompleted;

    public LearningModule() {}

    public LearningModule(String id, String title, String subtitle, String category,
                          String duration, String readTime, String badgeText,
                          String summary, List<String> keyPoints,
                          String detailedContent, String practicalTip) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.category = category;
        this.duration = duration;
        this.readTime = readTime;
        this.badgeText = badgeText;
        this.summary = summary;
        this.keyPoints = keyPoints;
        this.detailedContent = detailedContent;
        this.practicalTip = practicalTip;
        this.isCompleted = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getReadTime() { return readTime; }
    public void setReadTime(String readTime) { this.readTime = readTime; }

    public String getBadgeText() { return badgeText; }
    public void setBadgeText(String badgeText) { this.badgeText = badgeText; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getKeyPoints() { return keyPoints; }
    public void setKeyPoints(List<String> keyPoints) { this.keyPoints = keyPoints; }

    public String getDetailedContent() { return detailedContent; }
    public void setDetailedContent(String detailedContent) { this.detailedContent = detailedContent; }

    public String getPracticalTip() { return practicalTip; }
    public void setPracticalTip(String practicalTip) { this.practicalTip = practicalTip; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}
