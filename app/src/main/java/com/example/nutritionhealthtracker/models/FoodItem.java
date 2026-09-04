package com.example.nutritionhealthtracker.models;

public class FoodItem {
    private String id;
    private String name;
    private String category; // Fruits, Vegetables, Grains, Pulses, Dairy, Eggs, Meat, Fish, Nuts, Seeds, Healthy Fats, Snacks, Beverages
    private String servingSize; // e.g. "1 medium (100g)", "1 bowl (150g)"
    private int calories;
    private double protein; // grams
    private double carbs; // grams
    private double fat; // grams
    private double fiber; // grams
    private String vitamins; // e.g. "Vitamin C, Potassium, B6"
    private String dietaryType; // "Veg", "Non-Veg", "Vegan"
    private String goalTags; // "WeightLoss", "MuscleBuilding", "WeightGain", "GeneralHealth"
    private String whyUseful; // Educational benefit explanation

    public FoodItem(String id, String name, String category, String servingSize, int calories, double protein, double carbs, double fat, double fiber, String vitamins, String dietaryType, String goalTags, String whyUseful) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.servingSize = servingSize;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;
        this.vitamins = vitamins;
        this.dietaryType = dietaryType;
        this.goalTags = goalTags;
        this.whyUseful = whyUseful;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getServingSize() { return servingSize; }
    public int getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getCarbs() { return carbs; }
    public double getFat() { return fat; }
    public double getFiber() { return fiber; }
    public String getVitamins() { return vitamins; }
    public String getDietaryType() { return dietaryType; }
    public String getGoalTags() { return goalTags; }
    public String getWhyUseful() { return whyUseful; }
}
