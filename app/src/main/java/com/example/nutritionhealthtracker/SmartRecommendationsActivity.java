package com.example.nutritionhealthtracker;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionhealthtracker.models.NutritionRecord;
import com.example.nutritionhealthtracker.utils.DataManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmartRecommendationsActivity extends AppCompatActivity {

    private TextView tvTip, tvProteinStatus, tvProteinRecs, tvFiberStatus, tvFiberRecs, tvHydrationStatus;
    private Toolbar toolbar;

    private static final String[] DAILY_TIPS = {
            "\"Add a quality protein source like Paneer, Tofu, Eggs, or Dal to your meals to support muscle repair.\"",
            "\"Choose whole fruits like Apple or Papaya over processed sugary drinks to get natural fiber and hydration.\"",
            "\"Include green vegetables in both your lunch and dinner to support digestion and vital micro-nutrients.\"",
            "\"Nuts like Almonds and Walnuts provide healthy fats and protein, but keep portions controlled as they are calorie-dense.\"",
            "\"Drink water regularly throughout the day. Dehydration can often be mistaken for hunger!\""
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_recommendations);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Smart Food Recommendations");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvTip = findViewById(R.id.tvDailyNutritionTip);
        tvProteinStatus = findViewById(R.id.tvProteinGapStatus);
        tvProteinRecs = findViewById(R.id.tvProteinRecs);
        tvFiberStatus = findViewById(R.id.tvFiberGapStatus);
        tvFiberRecs = findViewById(R.id.tvFiberRecs);
        tvHydrationStatus = findViewById(R.id.tvHydrationStatus);

        loadAnalysis();
    }

    private void loadAnalysis() {
        int dayIndex = (int) (System.currentTimeMillis() / (1000 * 60 * 60 * 24)) % DAILY_TIPS.length;
        tvTip.setText(DAILY_TIPS[Math.abs(dayIndex)]);

        String todayKey = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        NutritionRecord record = DataManager.getNutritionRecord(this, todayKey);
        int calories = record != null ? record.getCalories() : 0;
        int water = DataManager.getWaterIntake(this, todayKey);
        int targetWater = DataManager.getUserWaterTarget(this);

        String dietType = DataManager.getDietaryType(this);

        if (calories == 0) {
            tvProteinStatus.setText("You haven't logged any meals today yet.");
            tvFiberStatus.setText("No meal records found for today.");
        } else {
            tvProteinStatus.setText("Logged Calories: " + calories + " kcal. Ensure you're hitting your daily protein target.");
            tvFiberStatus.setText("Ensure at least 25g - 30g of fiber from whole grains, legumes, and fresh produce.");
        }

        if (dietType.equals("Vegetarian")) {
            tvProteinRecs.setText("• Paneer, Tofu, Curd/Greek Yogurt, Yellow Dal, Rajma, Chole, Soy Chunks");
            tvFiberRecs.setText("• Papaya, Apple, Whole Wheat Roti, Oats, Spinach Sabzi, Sprouted Moong");
        } else if (dietType.equals("Vegan")) {
            tvProteinRecs.setText("• Tofu, Soy Chunks, Yellow Dal, Rajma, Chole, Chia Seeds, Roasted Peanuts");
            tvFiberRecs.setText("• Papaya, Apple, Whole Wheat Roti, Oats, Spinach Sabzi, Roasted Makhana");
        } else {
            tvProteinRecs.setText("• Boiled Eggs, Grilled Chicken Breast, Fish Curry, Paneer, Tofu, Greek Yogurt");
            tvFiberRecs.setText("• Fresh Papaya, Apple, Whole Wheat Roti, Broccoli, Oats, Sprouted Moong");
        }

        tvHydrationStatus.setText("Water Intake: " + water + " / " + targetWater + " ml (" + (int) (((double) water / targetWater) * 100) + "% met)");
    }
}
