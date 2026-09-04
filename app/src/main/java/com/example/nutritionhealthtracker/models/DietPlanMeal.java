package com.example.nutritionhealthtracker.models;

public class DietPlanMeal {
    private String mealType; // "Early Morning", "Breakfast", "Mid-Morning", "Lunch", "Evening Snack", "Dinner", "Bedtime"
    private FoodItem foodItem;
    private String portion;
    private String explanation;

    public DietPlanMeal(String mealType, FoodItem foodItem, String portion, String explanation) {
        this.mealType = mealType;
        this.foodItem = foodItem;
        this.portion = portion;
        this.explanation = explanation;
    }

    public String getMealType() { return mealType; }
    public FoodItem getFoodItem() { return foodItem; }
    public String getPortion() { return portion; }
    public String getExplanation() { return explanation; }

    public void setFoodItem(FoodItem foodItem) { this.foodItem = foodItem; }
    public void setPortion(String portion) { this.portion = portion; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
