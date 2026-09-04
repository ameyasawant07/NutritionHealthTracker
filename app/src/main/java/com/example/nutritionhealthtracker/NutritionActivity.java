package com.example.nutritionhealthtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionhealthtracker.models.NutritionRecord;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NutritionActivity extends AppCompatActivity {

    private TextView tvTitleDate;
    private TextInputEditText etBreakfast, etLunch, etDinner, etSnacks, etCalories;
    private MaterialButton btnSave;
    private com.google.android.material.card.MaterialCardView cardDietPlan, cardSmartRecs;
    private Toolbar toolbar;
    private String todayDateKey;
    private String displayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Nutrition Tracker");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        tvTitleDate = findViewById(R.id.tvNutritionDate);
        etBreakfast = findViewById(R.id.etBreakfast);
        etLunch = findViewById(R.id.etLunch);
        etDinner = findViewById(R.id.etDinner);
        etSnacks = findViewById(R.id.etSnacks);
        etCalories = findViewById(R.id.etCalories);
        btnSave = findViewById(R.id.btnSaveNutrition);
        cardDietPlan = findViewById(R.id.cardViewDietPlanShortcut);
        cardSmartRecs = findViewById(R.id.cardSmartRecsShortcut);

        todayDateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        displayDate = new SimpleDateFormat("dd MMM yyyy", Locale.US).format(new Date());
        tvTitleDate.setText("🥗 Daily Nutrition (" + displayDate + ")");

        loadTodayNutrition();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTodayNutrition();
            }
        });

        if (cardDietPlan != null) {
            cardDietPlan.setOnClickListener(v -> startActivity(new Intent(this, DietHubActivity.class)));
        }
        if (cardSmartRecs != null) {
            cardSmartRecs.setOnClickListener(v -> startActivity(new Intent(this, SmartRecommendationsActivity.class)));
        }
    }

    private void loadTodayNutrition() {
        NutritionRecord record = DataManager.getNutritionRecord(this, todayDateKey);
        if (record != null) {
            etBreakfast.setText(record.getBreakfast());
            etLunch.setText(record.getLunch());
            etDinner.setText(record.getDinner());
            etSnacks.setText(record.getSnacks());
            if (record.getCalories() > 0) {
                etCalories.setText(String.valueOf(record.getCalories()));
            }
        }
    }

    private void saveTodayNutrition() {
        String breakfast = etBreakfast.getText() != null ? etBreakfast.getText().toString().trim() : "";
        String lunch = etLunch.getText() != null ? etLunch.getText().toString().trim() : "";
        String dinner = etDinner.getText() != null ? etDinner.getText().toString().trim() : "";
        String snacks = etSnacks.getText() != null ? etSnacks.getText().toString().trim() : "";
        String caloriesStr = etCalories.getText() != null ? etCalories.getText().toString().trim() : "";

        int calories = 0;
        if (!caloriesStr.isEmpty()) {
            try {
                calories = Integer.parseInt(caloriesStr);
                if (calories < 0) {
                    etCalories.setError("Calories cannot be negative.");
                    etCalories.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                etCalories.setError("Invalid calories format.");
                etCalories.requestFocus();
                return;
            }
        }

        // Save with todayDateKey (yyyy-MM-dd) so streak lookup and history queries match correctly
        NutritionRecord record = new NutritionRecord(todayDateKey, breakfast, lunch, dinner, snacks, calories);
        DataManager.saveNutritionRecord(this, record, displayDate);
        DataManager.updateStreak(this);

        Toast.makeText(this, "Nutrition log saved successfully for today!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
