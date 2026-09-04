package com.example.nutritionhealthtracker.utils;

import android.content.Context;

import com.example.nutritionhealthtracker.models.DietPlanMeal;
import com.example.nutritionhealthtracker.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class DietGenerator {

    public static class EnergyRequirements {
        public int targetCalories;
        public int targetProteinGrams;
        public int targetCarbsGrams;
        public int targetFatGrams;
        public int targetFiberGrams;

        public EnergyRequirements(int calories, int protein, int carbs, int fat, int fiber) {
            this.targetCalories = calories;
            this.targetProteinGrams = protein;
            this.targetCarbsGrams = carbs;
            this.targetFatGrams = fat;
            this.targetFiberGrams = fiber;
        }
    }

    public static EnergyRequirements calculateRequirements(Context context) {
        double weight = DataManager.getProfileWeight(context);
        double height = DataManager.getProfileHeight(context);
        int age = DataManager.getProfileAge(context);
        String gender = DataManager.getProfileGender(context);
        String goal = DataManager.getUserGoal(context);

        // Fallback defaults if profile is incomplete
        if (weight <= 0) weight = 65.0;
        if (height <= 0) height = 170.0;
        if (age <= 0) age = 25;

        // BMR (Mifflin-St Jeor)
        double bmr = (10 * weight) + (6.25 * height) - (5 * age);
        if ("Male".equalsIgnoreCase(gender)) {
            bmr += 5;
        } else {
            bmr -= 161;
        }

        // TDEE (1.375 moderate activity factor)
        double tdee = bmr * 1.375;

        double targetCal = tdee;
        double proteinRatio = 0.20; // 20% protein
        double carbsRatio = 0.50;   // 50% carbs
        double fatRatio = 0.30;     // 30% fat

        switch (goal) {
            case "Weight Loss":
                targetCal = Math.max(1200, tdee - 500);
                proteinRatio = 0.30;
                carbsRatio = 0.40;
                fatRatio = 0.30;
                break;
            case "Muscle Building":
                targetCal = tdee + 350;
                proteinRatio = 0.30;
                carbsRatio = 0.45;
                fatRatio = 0.25;
                break;
            case "Healthy Weight Gain":
                targetCal = tdee + 500;
                proteinRatio = 0.25;
                carbsRatio = 0.50;
                fatRatio = 0.25;
                break;
            case "Healthy / Balanced Diet":
            default:
                targetCal = tdee;
                break;
        }

        int cal = (int) Math.round(targetCal);
        int protein = (int) Math.round((cal * proteinRatio) / 4.0);
        int carbs = (int) Math.round((cal * carbsRatio) / 4.0);
        int fat = (int) Math.round((cal * fatRatio) / 9.0);
        int fiber = 30; // standard healthy fiber target

        return new EnergyRequirements(cal, protein, carbs, fat, fiber);
    }

    public static List<DietPlanMeal> generateDailyPlan(Context context) {
        String dietType = DataManager.getDietaryType(context);
        String goal = DataManager.getUserGoal(context);
        List<FoodItem> allFoods = FoodDatabase.getAllFoods();

        List<DietPlanMeal> meals = new ArrayList<>();

        // Helper filter to get food matching diet & non-allergic
        FoodItem earlyMorning = findFood(allFoods, "Unsweetened Lemon Water", dietType);
        FoodItem breakfast = findFood(allFoods, dietType.equals("Non-Veg") ? "Boiled Eggs" : "Vegetable Poha", dietType);
        FoodItem midMorning = findFood(allFoods, "Fresh Banana", dietType);
        FoodItem lunch = findFood(allFoods, "Yellow Dal (Tadka)", dietType);
        FoodItem eveningSnack = findFood(allFoods, "Roasted Makhana (Fox Nuts)", dietType);
        FoodItem dinner = findFood(allFoods, dietType.equals("Non-Veg") ? "Grilled Chicken Breast" : "Fresh Paneer (Cottage Cheese)", dietType);
        FoodItem bedtime = findFood(allFoods, dietType.equals("Vegan") ? "Raw Almonds" : "Whole Cow Milk", dietType);

        meals.add(new DietPlanMeal("Early Morning", earlyMorning, "1 glass (250ml)", "Kickstarts metabolism and hydrates the digestive tract after sleep."));
        meals.add(new DietPlanMeal("Breakfast", breakfast, "1 plate / 2 eggs", "Provides sustained glucose and protein for morning cognitive and physical energy."));
        meals.add(new DietPlanMeal("Mid-Morning Snack", midMorning, "1 medium fruit", "Refuels glycogen stores and provides natural vitamins without digestive heaviness."));
        meals.add(new DietPlanMeal("Lunch", lunch, "1 bowl with 2 Rotis", "Balanced mix of complex carbs, plant protein, and essential fiber for afternoon stamina."));
        meals.add(new DietPlanMeal("Evening Snack", eveningSnack, "1 bowl (30g)", "Low-calorie crunchy snack to curb mid-day hunger cravings cleanly."));
        meals.add(new DietPlanMeal("Dinner", dinner, "150g with Salad", "Protein-dense meal supporting overnight muscle repair and cellular rejuvenation."));
        meals.add(new DietPlanMeal("Bedtime", bedtime, "1 glass / 5 almonds", "Slow-release protein/healthy fats to prevent night-time catabolism and promote deep sleep."));

        return meals;
    }

    private static FoodItem findFood(List<FoodItem> foods, String name, String dietType) {
        for (FoodItem item : foods) {
            if (item.getName().equalsIgnoreCase(name)) {
                if (dietType.equals("Vegetarian") && item.getDietaryType().equals("Non-Veg")) continue;
                if (dietType.equals("Vegan") && !item.getDietaryType().equals("Vegan")) continue;
                return item;
            }
        }
        // Fallback to first matching item
        for (FoodItem item : foods) {
            if (dietType.equals("Vegetarian") && !item.getDietaryType().equals("Non-Veg")) return item;
            if (dietType.equals("Vegan") && item.getDietaryType().equals("Vegan")) return item;
            if (dietType.equals("Non-Veg")) return item;
        }
        return foods.get(0);
    }
}
