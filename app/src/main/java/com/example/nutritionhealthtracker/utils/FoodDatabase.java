package com.example.nutritionhealthtracker.utils;

import com.example.nutritionhealthtracker.models.FoodItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FoodDatabase {

    private static final List<FoodItem> FOOD_ITEMS = new ArrayList<>();

    static {
        // --- GRAINS & INDIAN STAPLES ---
        FOOD_ITEMS.add(new FoodItem("f1", "Whole Wheat Roti", "Grains", "1 medium roti (35g)", 80, 3.0, 15.0, 0.5, 2.5, "B-Vitamins, Iron, Magnesium", "Vegan", "WeightLoss,MuscleBuilding,GeneralHealth", "Rich in complex carbohydrates and fiber for sustained digestion and energy."));
        FOOD_ITEMS.add(new FoodItem("f2", "Brown Rice", "Grains", "1 bowl cooked (150g)", 170, 4.0, 36.0, 1.5, 3.0, "Vitamin B6, Magnesium, Phosphorus", "Vegan", "WeightLoss,GeneralHealth", "Whole grain providing steady energy release without causing rapid blood sugar spikes."));
        FOOD_ITEMS.add(new FoodItem("f3", "White Rice", "Grains", "1 bowl cooked (150g)", 200, 4.2, 44.0, 0.4, 0.6, "Folate, Iron, Niacin", "Vegan", "WeightGain,MuscleBuilding", "Easily digestible source of carbohydrates, ideal for post-workout energy replenishment."));
        FOOD_ITEMS.add(new FoodItem("f4", "Vegetable Poha", "Grains", "1 plate (200g)", 220, 4.5, 42.0, 4.0, 3.5, "Iron, Vitamin C", "Vegan", "WeightLoss,GeneralHealth", "Flattened rice cooked with vegetables and peanuts; light and easy on the stomach."));
        FOOD_ITEMS.add(new FoodItem("f5", "Rava Upma", "Grains", "1 bowl (180g)", 210, 5.0, 38.0, 4.5, 2.8, "B-Vitamins, Iron", "Vegan", "GeneralHealth", "Traditional semolina dish offering quick energy and mineral support."));
        FOOD_ITEMS.add(new FoodItem("f6", "Steamed Idli", "Grains", "2 pieces (100g)", 140, 4.0, 28.0, 0.8, 2.0, "B-Vitamins, Probiotics", "Vegan", "WeightLoss,GeneralHealth", "Fermented rice and lentil batter; gentle on gut bacteria and light in calories."));
        FOOD_ITEMS.add(new FoodItem("f7", "Plain Dosa", "Grains", "1 medium (80g)", 160, 3.5, 29.0, 3.5, 1.8, "B-Vitamins", "Vegan", "GeneralHealth", "Crispy fermented crepe providing wholesome energy; pair with sambar for protein."));
        FOOD_ITEMS.add(new FoodItem("f8", "Oats Porridge", "Grains", "1 bowl cooked (250g)", 180, 6.0, 32.0, 3.0, 5.0, "Beta-Glucan, Iron, Zinc", "Vegan", "WeightLoss,GeneralHealth", "Packed with soluble fiber that lowers cholesterol and keeps you full for longer."));

        // --- PULSES & LEGUMES ---
        FOOD_ITEMS.add(new FoodItem("f9", "Yellow Dal (Tadka)", "Pulses", "1 bowl cooked (150g)", 150, 8.0, 22.0, 3.0, 5.0, "Folate, Potassium, Magnesium", "Vegan", "WeightLoss,MuscleBuilding,GeneralHealth", "Essential plant-based protein source supporting muscle recovery and tissue repair."));
        FOOD_ITEMS.add(new FoodItem("f10", "Rajma Curry (Kidney Beans)", "Legumes", "1 bowl cooked (180g)", 210, 11.0, 32.0, 3.5, 8.0, "Iron, Potassium, Folate", "Vegan", "MuscleBuilding,WeightLoss", "High-protein, fiber-dense pulse that stabilizes blood sugar and enhances endurance."));
        FOOD_ITEMS.add(new FoodItem("f11", "Chole Curry (Chickpeas)", "Legumes", "1 bowl cooked (180g)", 230, 10.5, 34.0, 5.0, 7.5, "Calcium, Magnesium, Zinc", "Vegan", "MuscleBuilding,GeneralHealth", "Rich in plant protein and dietary fiber for long-lasting digestive saturation."));
        FOOD_ITEMS.add(new FoodItem("f12", "Sprouted Moong Salad", "Pulses", "1 bowl (150g)", 120, 9.0, 18.0, 1.0, 6.0, "Vitamin C, Vitamin K, Folate", "Vegan", "WeightLoss,GeneralHealth", "Sprouting increases enzyme activity, vitamin absorption, and protein bio-availability."));

        // --- DAIRY & SOY ---
        FOOD_ITEMS.add(new FoodItem("f13", "Fresh Paneer (Cottage Cheese)", "Dairy", "100g", 265, 18.0, 4.0, 20.0, 0.0, "Calcium, Vitamin B12, Phosphorus", "Veg", "MuscleBuilding,WeightGain", "Rich source of slow-digesting casein protein and bone-building calcium."));
        FOOD_ITEMS.add(new FoodItem("f14", "Greek Yogurt / Thick Curd", "Dairy", "1 cup (150g)", 110, 10.0, 6.0, 4.0, 0.0, "Probiotics, Calcium, Vitamin B12", "Veg", "WeightLoss,MuscleBuilding,GeneralHealth", "High in gut-friendly probiotics and concentrated protein to suppress appetite."));
        FOOD_ITEMS.add(new FoodItem("f15", "Tofu (Soy Cottage Cheese)", "Dairy", "100g", 140, 14.0, 3.0, 8.0, 1.5, "Calcium, Iron, Isoflavones", "Vegan", "MuscleBuilding,WeightLoss", "Complete plant protein containing all nine essential amino acids."));
        FOOD_ITEMS.add(new FoodItem("f16", "Whole Cow Milk", "Dairy", "1 glass (250ml)", 150, 8.0, 12.0, 8.0, 0.0, "Calcium, Vitamin D, Vitamin B12", "Veg", "WeightGain,MuscleBuilding", "Provides complete protein, healthy fat, and essential bone-strengthening minerals."));
        FOOD_ITEMS.add(new FoodItem("f17", "Soy Chunks (Cooked)", "Pulses", "50g dry (cooked)", 170, 26.0, 15.0, 0.5, 6.5, "Iron, Calcium, Magnesium", "Vegan", "MuscleBuilding,WeightLoss", "Ultra-concentrated plant protein power source; ideal for bodybuilding diets."));

        // --- EGGS & MEAT & FISH ---
        FOOD_ITEMS.add(new FoodItem("f18", "Boiled Eggs", "Eggs", "2 whole eggs (100g)", 155, 13.0, 1.0, 11.0, 0.0, "Choline, Vitamin D, Vitamin B12, Selenium", "Non-Veg", "MuscleBuilding,WeightLoss,GeneralHealth", "Gold standard of bioavailable protein with essential fats for brain and muscle health."));
        FOOD_ITEMS.add(new FoodItem("f19", "Egg Whites", "Eggs", "4 egg whites (130g)", 68, 14.0, 0.8, 0.2, 0.0, "Riboflavin, Potassium", "Non-Veg", "WeightLoss,MuscleBuilding", "Pure lean protein with almost zero fat and calories; perfect for cutting cycles."));
        FOOD_ITEMS.add(new FoodItem("f20", "Grilled Chicken Breast", "Meat", "150g cooked", 245, 46.0, 0.0, 5.0, 0.0, "Niacin, Vitamin B6, Phosphorus, Selenium", "Non-Veg", "MuscleBuilding,WeightLoss", "Ultra-lean high-protein meat that accelerates muscle synthesis and metabolic rate."));
        FOOD_ITEMS.add(new FoodItem("f21", "Fish Curry / Grilled Fish", "Fish", "150g cooked", 210, 32.0, 2.0, 8.0, 0.0, "Omega-3 Fatty Acids, Vitamin D, Iodine", "Non-Veg", "GeneralHealth,MuscleBuilding", "Rich in anti-inflammatory Omega-3 fats that support heart, joint, and brain health."));

        // --- FRUITS & VEGETABLES ---
        FOOD_ITEMS.add(new FoodItem("f22", "Fresh Banana", "Fruits", "1 medium (118g)", 105, 1.3, 27.0, 0.3, 3.1, "Potassium, Vitamin B6, Vitamin C", "Vegan", "GeneralHealth,WeightGain", "Great pre-workout source of natural sugars and electrolyte potassium to prevent cramps."));
        FOOD_ITEMS.add(new FoodItem("f23", "Red Apple", "Fruits", "1 medium (180g)", 95, 0.5, 25.0, 0.3, 4.4, "Vitamin C, Quercetin, Pectin", "Vegan", "WeightLoss,GeneralHealth", "High in soluble pectin fiber that promotes fullness and supports digestive health."));
        FOOD_ITEMS.add(new FoodItem("f24", "Papaya Cubes", "Fruits", "1 bowl (150g)", 60, 0.8, 15.0, 0.2, 2.5, "Papain, Vitamin C, Vitamin A", "Vegan", "WeightLoss,GeneralHealth", "Contains digestive enzyme papain which aids protein breakdown and reduces bloating."));
        FOOD_ITEMS.add(new FoodItem("f25", "Steamed Broccoli", "Vegetables", "1 bowl (150g)", 55, 4.2, 10.0, 0.6, 3.8, "Vitamin C, Vitamin K, Sulforaphane", "Vegan", "WeightLoss,MuscleBuilding,GeneralHealth", "Nutrient-dense cruciferous vegetable packed with antioxidants and fiber."));
        FOOD_ITEMS.add(new FoodItem("f26", "Spinach Sabzi (Palak)", "Vegetables", "1 bowl cooked (150g)", 70, 3.5, 8.0, 3.0, 4.0, "Iron, Folate, Vitamin A, Vitamin C", "Vegan", "WeightLoss,GeneralHealth", "Leafy green superfood loaded with iron for red blood cell synthesis and energy."));

        // --- NUTS, SEEDS & HEALTHY FATS ---
        FOOD_ITEMS.add(new FoodItem("f27", "Raw Almonds", "Nuts", "Handful (28g / 23 nuts)", 164, 6.0, 6.0, 14.0, 3.5, "Vitamin E, Magnesium, Antioxidants", "Vegan", "GeneralHealth,MuscleBuilding", "Heart-healthy monounsaturated fats and Vitamin E that protect cells from oxidation."));
        FOOD_ITEMS.add(new FoodItem("f28", "Walnuts", "Nuts", "Handful (28g / 14 halves)", 185, 4.3, 3.9, 18.5, 1.9, "Plant Omega-3 (ALA), Copper, Manganese", "Vegan", "GeneralHealth", "Highest plant Omega-3 content among nuts, supporting cognitive and brain function."));
        FOOD_ITEMS.add(new FoodItem("f29", "Chia Seeds", "Seeds", "1 tbsp (15g)", 70, 2.5, 6.0, 4.5, 5.0, "Omega-3, Calcium, Fiber", "Vegan", "WeightLoss,GeneralHealth", "Absorbs water to form a gel in the stomach, prolonging satiety and gut hydration."));
        FOOD_ITEMS.add(new FoodItem("f30", "Peanut Butter (Unsweetened)", "Nuts", "2 tbsp (32g)", 190, 8.0, 7.0, 16.0, 2.0, "Vitamin E, Niacin, Magnesium", "Vegan", "WeightGain,MuscleBuilding", "Calorie-dense, delicious source of protein and healthy fats for clean weight gain."));

        // --- BEVERAGES & HEALTHY SNACKS ---
        FOOD_ITEMS.add(new FoodItem("f31", "Fresh Coconut Water", "Beverages", "1 glass (250ml)", 45, 1.0, 9.0, 0.2, 1.1, "Potassium, Magnesium, Sodium", "Vegan", "GeneralHealth", "Natural isotonic electrolyte drink that hydrates deeply without added artificial sugar."));
        FOOD_ITEMS.add(new FoodItem("f32", "Unsweetened Lemon Water", "Beverages", "1 glass (250ml)", 10, 0.2, 3.0, 0.0, 0.5, "Vitamin C, Citrus Bioflavonoids", "Vegan", "WeightLoss,GeneralHealth", "Refreshing low-calorie drink that promotes hydration and aids digestion."));
        FOOD_ITEMS.add(new FoodItem("f33", "Roasted Makhana (Fox Nuts)", "Snacks", "1 bowl (30g)", 110, 3.0, 20.0, 2.0, 2.5, "Magnesium, Potassium, Calcium", "Vegan", "WeightLoss,GeneralHealth", "Crunchy low-calorie, low-glycemic snack rich in antioxidants."));

        // --- ADDITIONAL INDIAN & INTERNATIONAL FOODS ---
        FOOD_ITEMS.add(new FoodItem("f34", "Butter Naan", "Grains", "1 naan (90g)", 260, 6.0, 42.0, 8.0, 1.5, "B-Vitamins, Iron", "Veg", "WeightGain", "Traditional tandoor flatbread brushed with butter."));
        FOOD_ITEMS.add(new FoodItem("f35", "Aloo Paratha", "Grains", "1 medium paratha (130g)", 290, 6.5, 45.0, 10.0, 4.0, "Vitamin C, Potassium, B-Vitamins", "Veg", "WeightGain,GeneralHealth", "Whole wheat stuffed flatbread with spiced potato filling."));
        FOOD_ITEMS.add(new FoodItem("f36", "Chapati", "Grains", "1 medium chapati (30g)", 70, 2.5, 14.0, 0.4, 2.0, "Iron, Magnesium", "Vegan", "WeightLoss,GeneralHealth", "Unleavened whole wheat Indian flatbread."));
        FOOD_ITEMS.add(new FoodItem("f37", "South Indian Sambar", "Pulses", "1 bowl (180g)", 130, 6.0, 18.0, 3.5, 4.5, "Vitamin C, Iron, Fiber", "Vegan", "WeightLoss,GeneralHealth", "Lentil and vegetable stew spiced with tamarind and sambar powder."));
        FOOD_ITEMS.add(new FoodItem("f38", "Cucumber Tomato Raita", "Dairy", "1 bowl (150g)", 85, 4.5, 6.0, 4.0, 1.0, "Calcium, Probiotics", "Veg", "GeneralHealth,WeightLoss", "Cooling yogurt side dish with fresh diced cucumber and tomatoes."));
        FOOD_ITEMS.add(new FoodItem("f39", "Paneer Butter Masala", "Dairy", "1 bowl (200g)", 340, 14.0, 12.0, 26.0, 2.0, "Calcium, Vitamin A", "Veg", "WeightGain,MuscleBuilding", "Rich cottage cheese curry in tomatoes, cream, and aromatic spices."));
        FOOD_ITEMS.add(new FoodItem("f40", "Chicken Biryani", "Meat", "1 plate (350g)", 480, 28.0, 58.0, 16.0, 3.5, "B12, Niacin, Iron", "Non-Veg", "MuscleBuilding,WeightGain", "Fragrant basmati rice cooked with marinated chicken and aromatic spices."));
        FOOD_ITEMS.add(new FoodItem("f41", "Masala Egg Omelette", "Eggs", "2 eggs omelette (120g)", 190, 13.0, 3.0, 14.0, 1.0, "Choline, Vitamin D, B12", "Non-Veg", "MuscleBuilding,WeightLoss", "Fluffy pan-fried eggs with onions, green chilies, and cilantro."));
        FOOD_ITEMS.add(new FoodItem("f42", "Fresh Garden Salad", "Vegetables", "1 bowl (150g)", 45, 1.5, 8.0, 0.3, 3.2, "Vitamin C, Vitamin A, Folate", "Vegan", "WeightLoss,GeneralHealth", "Crispy salad mix of cucumber, tomatoes, carrots, lettuce, and lemon."));
        FOOD_ITEMS.add(new FoodItem("f43", "Veg Club Sandwich", "Snacks", "1 sandwich (180g)", 250, 7.0, 42.0, 6.5, 4.0, "Fiber, B-Vitamins", "Veg", "GeneralHealth", "Whole grain bread filled with cucumber, tomato, cheese, and mint chutney."));
        FOOD_ITEMS.add(new FoodItem("f44", "Veggie Cheese Pizza", "Snacks", "2 slices (160g)", 380, 14.0, 48.0, 15.0, 3.0, "Calcium, Protein", "Veg", "WeightGain", "Oven baked pizza topped with mozzarella cheese, capsicum, onions, and corn."));
        FOOD_ITEMS.add(new FoodItem("f45", "Jowar / Rice Bhakri", "Grains", "1 medium bhakri (50g)", 120, 3.0, 25.0, 0.8, 2.5, "B-Vitamins, Iron, Fiber", "Vegan", "GeneralHealth,WeightLoss", "Unleavened millet or rice flatbread common in Maharashtra and western India."));
        FOOD_ITEMS.add(new FoodItem("f46", "Palak Paneer", "Dairy", "1 bowl (150g)", 240, 12.0, 8.0, 18.0, 3.5, "Iron, Vitamin A, Calcium, Folate", "Veg", "MuscleBuilding,GeneralHealth", "Cottage cheese cubes cooked in a rich, spiced spinach gravy."));
        FOOD_ITEMS.add(new FoodItem("f47", "Vegetable Tikki / Cutlet", "Snacks", "1 piece (60g)", 130, 3.5, 18.0, 5.0, 2.5, "Vitamin C, Potassium", "Veg", "GeneralHealth", "Pan-fried vegetable patty made with mashed potatoes, peas, and Indian spices."));
        FOOD_ITEMS.add(new FoodItem("f48", "Whole Wheat / White Bread", "Grains", "1 slice (30g)", 75, 2.5, 14.0, 1.0, 1.2, "B-Vitamins, Iron", "Veg", "GeneralHealth", "Standard bread slice commonly served with eggs or butter."));
        FOOD_ITEMS.add(new FoodItem("f49", "Indian Egg Curry", "Eggs", "1 bowl (180g)", 220, 14.0, 6.0, 15.0, 1.5, "B12, Choline, Vitamin D", "Non-Veg", "MuscleBuilding", "Hard-boiled eggs simmered in spicy onion-tomato gravy."));
        FOOD_ITEMS.add(new FoodItem("f50", "Dal Makhani", "Pulses", "1 bowl (180g)", 270, 10.0, 28.0, 13.0, 6.0, "Iron, Calcium, Protein", "Veg", "MuscleBuilding", "Slow-cooked black lentils and kidney beans with butter and cream."));
        FOOD_ITEMS.add(new FoodItem("f51", "Sabudana Khichdi", "Grains", "1 bowl (180g)", 280, 4.0, 48.0, 9.0, 2.0, "Potassium, Carbohydrates", "Veg", "GeneralHealth", "Tapioca pearls tossed with roasted peanuts, potatoes, and cumin."));
        FOOD_ITEMS.add(new FoodItem("f52", "Puri with Aloo Sabzi", "Grains", "2 puris with bowl sabzi (200g)", 360, 6.5, 52.0, 14.0, 3.5, "Vitamin C, B-Vitamins", "Vegan", "WeightGain", "Deep-fried whole wheat puffed bread served with spiced potato curry."));
    }

    public static List<FoodItem> getAllFoods() {
        return new ArrayList<>(FOOD_ITEMS);
    }

    public static List<FoodItem> getFoodsByCategory(String category) {
        List<FoodItem> list = new ArrayList<>();
        for (FoodItem item : FOOD_ITEMS) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                list.add(item);
            }
        }
        return list;
    }

    public static List<FoodItem> searchFoods(String query, String categoryFilter, String dietTypeFilter) {
        List<FoodItem> list = new ArrayList<>();
        String q = query != null ? query.trim().toLowerCase(Locale.US) : "";

        for (FoodItem item : FOOD_ITEMS) {
            boolean matchesQuery = q.isEmpty() || item.getName().toLowerCase(Locale.US).contains(q) || item.getCategory().toLowerCase(Locale.US).contains(q);
            boolean matchesCategory = categoryFilter.equals("All") || item.getCategory().equalsIgnoreCase(categoryFilter);

            boolean matchesDiet = true;
            if (dietTypeFilter.equals("Vegetarian")) {
                matchesDiet = item.getDietaryType().equals("Veg") || item.getDietaryType().equals("Vegan");
            } else if (dietTypeFilter.equals("Vegan")) {
                matchesDiet = item.getDietaryType().equals("Vegan");
            }

            if (matchesQuery && matchesCategory && matchesDiet) {
                list.add(item);
            }
        }
        return list;
    }

    public static List<FoodItem> getProteinSources() {
        List<FoodItem> list = new ArrayList<>();
        for (FoodItem item : FOOD_ITEMS) {
            if (item.getProtein() >= 5.0) {
                list.add(item);
            }
        }
        // Sort highest protein first
        list.sort((a, b) -> Double.compare(b.getProtein(), a.getProtein()));
        return list;
    }

    public static List<FoodItem> getSubstitutesFor(FoodItem baseItem, String userDietaryType) {
        List<FoodItem> substitutes = new ArrayList<>();
        for (FoodItem candidate : FOOD_ITEMS) {
            if (candidate.getId().equals(baseItem.getId())) continue;

            // Check dietary restriction
            if (userDietaryType.equals("Vegetarian") && candidate.getDietaryType().equals("Non-Veg")) continue;
            if (userDietaryType.equals("Vegan") && !candidate.getDietaryType().equals("Vegan")) continue;

            // Match nutritional purpose (similar calorie range or high protein if base is high protein)
            boolean similarCalories = Math.abs(candidate.getCalories() - baseItem.getCalories()) <= 100;
            boolean bothHighProtein = baseItem.getProtein() >= 8.0 && candidate.getProtein() >= 8.0;

            if (similarCalories || bothHighProtein) {
                substitutes.add(candidate);
            }
        }
        return substitutes;
    }

    public static FoodItem findFoodByName(String query) {
        if (query == null || query.trim().isEmpty()) return null;
        String q = query.trim().toLowerCase(Locale.US);
        for (FoodItem item : FOOD_ITEMS) {
            if (item.getName().toLowerCase(Locale.US).contains(q) || q.contains(item.getName().toLowerCase(Locale.US))) {
                return item;
            }
        }
        return null;
    }
}
