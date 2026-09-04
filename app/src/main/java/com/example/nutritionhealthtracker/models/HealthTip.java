package com.example.nutritionhealthtracker.models;

public class HealthTip {
    private String title;
    private String description;
    private String category;
    private String iconEmoji;

    public HealthTip(String title, String description, String category, String iconEmoji) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.iconEmoji = iconEmoji;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getIconEmoji() {
        return iconEmoji;
    }
}
