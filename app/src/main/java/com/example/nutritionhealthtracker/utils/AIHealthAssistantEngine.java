package com.example.nutritionhealthtracker.utils;

import android.content.Context;

import com.example.nutritionhealthtracker.models.FoodItem;
import com.example.nutritionhealthtracker.models.NutritionRecord;
import com.example.nutritionhealthtracker.models.SleepRecord;
import com.example.nutritionhealthtracker.models.WeightRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AIHealthAssistantEngine {

    public static class ContextSummary {
        public String userName;
        public double weightKg;
        public double heightCm;
        public double bmi;
        public String bmiCategory;
        public String dietGoal;
        public String dietaryType;
        public String allergies;
        public String foodsToAvoid;
        
        public int targetCalories;
        public int targetProteinGrams;
        public int targetWaterMl;
        
        public int consumedCaloriesToday;
        public int consumedWaterToday;
        public int exerciseMinsToday;
        public double sleepHoursToday;
        
        public String breakfastToday;
        public String lunchToday;
        public String dinnerToday;
        public String snacksToday;
    }

    public static ContextSummary getUserContext(Context context) {
        ContextSummary summary = new ContextSummary();
        String name = DataManager.getProfileName(context);
        summary.userName = (name != null && !name.trim().isEmpty()) ? name : "User";
        
        summary.weightKg = DataManager.getProfileWeight(context);
        summary.heightCm = DataManager.getProfileHeight(context);
        
        if (summary.weightKg <= 0) summary.weightKg = 65.0;
        if (summary.heightCm <= 0) summary.heightCm = 170.0;
        
        double heightM = summary.heightCm / 100.0;
        summary.bmi = summary.weightKg / (heightM * heightM);
        if (summary.bmi < 18.5) summary.bmiCategory = "Underweight";
        else if (summary.bmi < 24.9) summary.bmiCategory = "Normal Weight";
        else if (summary.bmi < 29.9) summary.bmiCategory = "Overweight";
        else summary.bmiCategory = "Obese";
        
        summary.dietGoal = DataManager.getUserGoal(context);
        summary.dietaryType = DataManager.getDietaryType(context);
        summary.allergies = DataManager.getAllergies(context);
        summary.foodsToAvoid = DataManager.getFoodsToAvoid(context);
        
        DietGenerator.EnergyRequirements reqs = DietGenerator.calculateRequirements(context);
        summary.targetCalories = reqs.targetCalories;
        summary.targetProteinGrams = reqs.targetProteinGrams;
        summary.targetWaterMl = DataManager.getUserWaterTarget(context);
        
        String todayKey = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        
        NutritionRecord recordToday = DataManager.getNutritionRecord(context, todayKey);
        if (recordToday != null) {
            summary.consumedCaloriesToday = recordToday.getCalories();
            summary.breakfastToday = recordToday.getBreakfast();
            summary.lunchToday = recordToday.getLunch();
            summary.dinnerToday = recordToday.getDinner();
            summary.snacksToday = recordToday.getSnacks();
        } else {
            summary.consumedCaloriesToday = 0;
            summary.breakfastToday = "";
            summary.lunchToday = "";
            summary.dinnerToday = "";
            summary.snacksToday = "";
        }
        
        summary.consumedWaterToday = DataManager.getWaterIntake(context, todayKey);
        summary.exerciseMinsToday = DataManager.getExerciseMinutesForDate(context, todayKey);
        
        SleepRecord latestSleep = DataManager.getLatestSleepRecord(context);
        summary.sleepHoursToday = (latestSleep != null) ? latestSleep.getDurationHours() : 0.0;
        
        return summary;
    }

    public static String generateResponse(Context context, String userQuery) {
        if (userQuery == null || userQuery.trim().isEmpty()) {
            return "Hello! I am your AI Health & Nutrition Assistant. How can I help you today?";
        }
        
        String q = userQuery.trim().toLowerCase(Locale.US);
        ContextSummary ctx = getUserContext(context);
        
        // Medical Safety check
        if (q.contains("doctor") || q.contains("disease") || q.contains("fever") || q.contains("infection") ||
            q.contains("prescription") || q.contains("medicine") || q.contains("eating disorder") ||
            q.contains("bulimia") || q.contains("anorexia") || q.contains("pain") || q.contains("severe allergy")) {
            return "⚠️ **Medical Notice**: I am an AI Health Assistant, not a medical doctor. I cannot diagnose medical conditions or prescribe treatments. For medical concerns, symptoms, severe allergies, or eating disorders, please consult a qualified healthcare professional.\n\nFor general nutrition, I can gladly help you with balanced meal suggestions, calorie targets, and macro tracking!";
        }
        
        // Safety check for extreme diets / starvation
        if (q.contains("starve") || q.contains("crash diet") || q.contains("fast for 5 days") || q.contains("skip all meals") || q.contains("lose 10kg in 2 days")) {
            return "🛑 **Health Safety Warning**: Extreme calorie restriction, starvation, or crash diets are dangerous to your health and lead to muscle loss and nutrient deficiencies. A safe, sustainable weight loss target is 0.5–1 kg per week with a balanced diet.\n\nYour target intake is **" + ctx.targetCalories + " kcal/day**. Let's focus on healthy, nutrient-dense foods!";
        }

        // Question 1: What should I eat for breakfast today?
        if (q.contains("breakfast")) {
            String bSuggestion = ctx.dietaryType.equalsIgnoreCase("Non-Veg") ?
                    "2 Boiled Eggs or Egg Omelette with whole wheat toast and fruit." :
                    (ctx.dietaryType.equalsIgnoreCase("Vegan") ? "Vegetable Poha or Oats Porridge with chia seeds and almonds." : "Steamed Idli/Dosa with sambar or Vegetable Poha with curd.");
            return "🥣 **Breakfast Suggestion for " + ctx.userName + "**:\n\nBased on your preference (" + ctx.dietaryType + ") and goal (" + ctx.dietGoal + "), a great breakfast option is:\n• " + bSuggestion + "\n\nThis provides ~300-400 kcal and sustained energy for your morning!";
        }

        // Question 2: Suggest healthy dinner
        if (q.contains("dinner")) {
            String dSuggestion = ctx.dietaryType.equalsIgnoreCase("Non-Veg") ?
                    "Grilled Chicken Breast or Fish Curry with a small portion of Brown Rice and Garden Salad." :
                    (ctx.dietaryType.equalsIgnoreCase("Vegan") ? "Tofu stir-fry with mixed vegetables and 2 whole wheat Chapatis." : "Fresh Paneer curry or Dal Tadka with 2 Chapatis and Raita.");
            return "🍛 **Healthy Dinner Suggestion**:\n\nFor a balanced dinner aligned with your " + ctx.dietGoal + " goal:\n• " + dSuggestion + "\n\nTip: Keep dinner lighter than lunch and try to finish 2 hours before sleep.";
        }

        // Question 3: Paneer substitutes
        if (q.contains("instead of paneer") || q.contains("substitute for paneer") || q.contains("replace paneer")) {
            return "🧀 **Healthy Substitutes for Paneer**:\n\nIf you want to replace Paneer while keeping protein high, try:\n" +
                    "1. **Tofu** (14g protein/100g, lower in fat, 100% plant-based)\n" +
                    "2. **Soy Chunks** (26g protein/50g dry cooked — ultra high protein!)\n" +
                    "3. **Greek Yogurt / Thick Curd** (10g protein/cup, great for gut health)\n" +
                    "4. **Rajma / Chole** (10-11g protein per bowl)\n" +
                    "5. **Boiled Eggs** (13g protein per 2 whole eggs, if non-veg)";
        }

        // Question 4: Protein intake / High protein foods
        if (q.contains("increase protein") || q.contains("high in protein") || q.contains("protein intake") || q.contains("more protein") || q.contains("reach my target")) {
            return "💪 **High-Protein Foods & Target Strategy**:\n\nYour daily protein target is **~" + ctx.targetProteinGrams + "g**.\n\nTop Protein Sources:\n" +
                    "• **Soy Chunks**: 26g protein per 50g cooked\n" +
                    "• **Grilled Chicken Breast**: 46g protein per 150g\n" +
                    "• **Fresh Paneer**: 18g protein per 100g\n" +
                    "• **Boiled Eggs**: 13g protein per 2 eggs\n" +
                    "• **Rajma / Chole / Yellow Dal**: 8-11g protein per bowl\n" +
                    "• **Greek Yogurt**: 10g protein per cup\n" +
                    "• **Roasted Makhana & Almonds**: 3-6g per serving\n\nAdd one protein source to every meal to hit your target easily!";
        }

        // Question 5: Explain my nutrition today / Why calories low / tracked progress
        if (q.contains("explain my nutrition") || q.contains("nutrition today") || q.contains("my progress") || q.contains("low today") || q.contains("calories low")) {
            int calRemaining = ctx.targetCalories - ctx.consumedCaloriesToday;
            StringBuilder sb = new StringBuilder();
            sb.append("📊 **Nutrition Breakdown Today for ").append(ctx.userName).append("**:\n\n");
            sb.append("• **Weight**: ").append(String.format(Locale.US, "%.1f kg", ctx.weightKg)).append(" (BMI: ").append(String.format(Locale.US, "%.1f", ctx.bmi)).append(" - ").append(ctx.bmiCategory).append(")\n");
            sb.append("• **Goal**: ").append(ctx.dietGoal).append("\n");
            sb.append("• **Calories Logged**: ").append(ctx.consumedCaloriesToday).append(" / ").append(ctx.targetCalories).append(" kcal\n");
            sb.append("• **Water Intake**: ").append(ctx.consumedWaterToday).append(" / ").append(ctx.targetWaterMl).append(" ml\n");
            sb.append("• **Exercise**: ").append(ctx.exerciseMinsToday).append(" mins\n");
            sb.append("• **Sleep**: ").append(String.format(Locale.US, "%.1f hrs", ctx.sleepHoursToday)).append("\n\n");
            
            if (calRemaining > 400) {
                sb.append("💡 **Insight**: Your logged calories are currently lower than your daily target by ").append(calRemaining).append(" kcal. Ensure you eat a balanced meal so you don't feel fatigued later!");
            } else if (calRemaining < -300) {
                sb.append("💡 **Insight**: You have exceeded your calorie target slightly today. You can balance this with a light walk or lighter meal tomorrow!");
            } else {
                sb.append("💡 **Insight**: You are right on track with your calorie target today! Keep it up.");
            }
            return sb.toString();
        }

        // Question 6: Post workout meal
        if (q.contains("post-workout") || q.contains("after exercise") || q.contains("after workout")) {
            return "🏋️ **Post-Workout Recovery Meals**:\n\nWithin 30–60 minutes after exercise, eat a mix of fast-digesting protein and carbs:\n" +
                    "1. **Banana + 2 Boiled Eggs** (or 1 glass milk/whey)\n" +
                    "2. **Greek Yogurt with Honey & Almonds**\n" +
                    "3. **Paneer / Tofu Sandwich on whole wheat bread**\n" +
                    "4. **Sprouted Moong Salad with Lemon**\n\nThis replenishes muscle glycogen and speeds up muscle protein synthesis!";
        }

        // Question 7: High calorie foods / foods to limit / weight gain / weight loss
        if (q.contains("high in calories") || q.contains("weight gain") || q.contains("foods to limit")) {
            if (q.contains("limit") || q.contains("avoid")) {
                return "🛑 **Foods to Limit in Moderation**:\n\n• Deep-fried snacks (samosas, pakoras)\n• Sugary sodas and fruit juices with added sugar\n• Refined flour (Maida) products\n• Highly processed fast food (commercial chips, donuts)\n\nNote: You don't have to eliminate foods completely — focus on 80% whole nutrition and 20% moderation!";
            } else {
                return "🥔 **Nutrient-Dense High-Calorie Foods** (Great for Clean Weight Gain):\n\n• **Peanut Butter**: 190 kcal per 2 tbsp\n• **Raw Almonds & Walnuts**: 160-185 kcal per handful\n• **Bananas**: 105 kcal each\n• **Fresh Paneer & Whole Milk**: 150-265 kcal\n• **Aloo Paratha & Rice with Ghee**";
            }
        }

        // Question 8: Hydration / Water / Sleep
        if (q.contains("water") || q.contains("hydration") || q.contains("sleep")) {
            return "💧 **Hydration & Sleep Guidelines**:\n\n" +
                    "• **Water Target**: You have logged " + ctx.consumedWaterToday + " / " + ctx.targetWaterMl + " ml today. Proper hydration boosts metabolism, aids digestion, and improves focus.\n" +
                    "• **Sleep Target**: Aim for 7–8 hours of quality sleep nightly. Sleep is when your body releases growth hormones for tissue repair and fat metabolism.";
        }

        // Generic intelligent fallback
        return "💡 **Health Guidance for " + ctx.userName + "**:\n\n" +
                "Your current goal is **" + ctx.dietGoal + "** (" + ctx.dietaryType + " diet).\n" +
                "• Daily Calorie Target: **" + ctx.targetCalories + " kcal**\n" +
                "• Daily Protein Target: **~" + ctx.targetProteinGrams + "g**\n" +
                "• Water Target: **" + ctx.targetWaterMl + " ml**\n\n" +
                "Feel free to ask me specific questions like:\n" +
                "• \"What should I eat today?\"\n" +
                "• \"How can I increase protein?\"\n" +
                "• \"Suggest a healthy dinner\"\n" +
                "• \"What can I eat instead of paneer?\"";
    }
}
