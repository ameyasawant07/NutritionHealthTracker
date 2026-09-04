package com.example.nutritionhealthtracker.models;

public class NutritionRecord {
    private String date;
    private String breakfast;
    private String lunch;
    private String dinner;
    private String snacks;
    private int calories;

    public NutritionRecord(String date, String breakfast, String lunch, String dinner, String snacks, int calories) {
        this.date = date;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.snacks = snacks;
        this.calories = calories;
    }

    public String getDate() {
        return date;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public String getLunch() {
        return lunch;
    }

    public String getDinner() {
        return dinner;
    }

    public String getSnacks() {
        return snacks;
    }

    public int getCalories() {
        return calories;
    }
}
