package com.example.nutritionhealthtracker.models;

public class FoodScanRecord {
    private String id;
    private String date; // e.g. "04 Sep 2026"
    private String dateKey; // e.g. "2026-09-04"
    private String time; // e.g. "14:30"
    private String photoUri; // Local URI or empty if photo deleted
    private String detectedFoodsSummary; // e.g. "Rice, Dal, Paneer, Salad"
    private int calories;
    private double protein;
    private double carbs;
    private double fat;
    private double fiber;
    private String mealSlot; // "Breakfast", "Lunch", "Dinner", "Snacks"

    public FoodScanRecord(String id, String date, String dateKey, String time, String photoUri,
                          String detectedFoodsSummary, int calories, double protein, double carbs,
                          double fat, double fiber, String mealSlot) {
        this.id = id;
        this.date = date;
        this.dateKey = dateKey;
        this.time = time;
        this.photoUri = photoUri;
        this.detectedFoodsSummary = detectedFoodsSummary;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;
        this.mealSlot = mealSlot;
    }

    public String getId() { return id; }
    public String getDate() { return date; }
    public String getDateKey() { return dateKey; }
    public String getTime() { return time; }
    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }
    public String getDetectedFoodsSummary() { return detectedFoodsSummary; }
    public int getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getCarbs() { return carbs; }
    public double getFat() { return fat; }
    public double getFiber() { return fiber; }
    public String getMealSlot() { return mealSlot; }
}
