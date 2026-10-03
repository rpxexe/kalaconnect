package com.kalaconnect.models;

public class OnboardingSlide {

    private final String title;
    private final String description;
    private final int iconResId;

    public OnboardingSlide(String title, String description, int iconResId) {
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIconResId() {
        return iconResId;
    }
}
