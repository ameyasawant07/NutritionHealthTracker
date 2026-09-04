package com.example.nutritionhealthtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.adapters.BMIHistoryAdapter;
import com.example.nutritionhealthtracker.adapters.ExerciseHistoryAdapter;
import com.example.nutritionhealthtracker.adapters.NutritionHistoryAdapter;
import com.example.nutritionhealthtracker.adapters.SleepHistoryAdapter;
import com.example.nutritionhealthtracker.adapters.WaterHistoryAdapter;
import com.example.nutritionhealthtracker.adapters.WeightHistoryAdapter;
import com.example.nutritionhealthtracker.models.BMIRecord;
import com.example.nutritionhealthtracker.models.ExerciseRecord;
import com.example.nutritionhealthtracker.models.NutritionRecord;
import com.example.nutritionhealthtracker.models.SleepRecord;
import com.example.nutritionhealthtracker.models.WaterRecord;
import com.example.nutritionhealthtracker.models.WeightRecord;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.List;

public class HealthHistoryActivity extends AppCompatActivity {

    private enum TabState {
        BMI, WEIGHT, NUTRITION, WATER, EXERCISE, SLEEP
    }

    private TabState currentTab = TabState.BMI;

    private MaterialButtonToggleGroup toggleGroup;
    private RecyclerView rvHistory;
    private LinearLayout llEmptyState;
    private TextView tvHeaderTitle, tvClearHistory, tvEmptyIcon, tvEmptyMessage;
    private MaterialButton btnActionButton;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_history);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Health History");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        toggleGroup = findViewById(R.id.toggleGroupHistory);
        rvHistory = findViewById(R.id.rvHealthHistory);
        llEmptyState = findViewById(R.id.llEmptyHistoryState);
        tvHeaderTitle = findViewById(R.id.tvHistoryHeaderTitle);
        tvClearHistory = findViewById(R.id.tvClearHistory);
        tvEmptyIcon = findViewById(R.id.tvEmptyIcon);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        btnActionButton = findViewById(R.id.btnGoToBMI);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnTabBMI) currentTab = TabState.BMI;
                else if (checkedId == R.id.btnTabWeight) currentTab = TabState.WEIGHT;
                else if (checkedId == R.id.btnTabNutrition) currentTab = TabState.NUTRITION;
                else if (checkedId == R.id.btnTabWater) currentTab = TabState.WATER;
                else if (checkedId == R.id.btnTabExercise) currentTab = TabState.EXERCISE;
                else if (checkedId == R.id.btnTabSleep) currentTab = TabState.SLEEP;
                loadTabHistory();
            }
        });

        tvClearHistory.setOnClickListener(v -> confirmClearCurrentTabHistory());

        btnActionButton.setOnClickListener(v -> {
            switch (currentTab) {
                case BMI: startActivity(new Intent(this, BMICalculatorActivity.class)); break;
                case WEIGHT: startActivity(new Intent(this, WeightTrackerActivity.class)); break;
                case NUTRITION: startActivity(new Intent(this, NutritionActivity.class)); break;
                case WATER: startActivity(new Intent(this, WaterTrackerActivity.class)); break;
                case EXERCISE: startActivity(new Intent(this, ExerciseTrackerActivity.class)); break;
                case SLEEP: startActivity(new Intent(this, SleepTrackerActivity.class)); break;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTabHistory();
    }

    private void loadTabHistory() {
        switch (currentTab) {
            case BMI: loadBMIHistory(); break;
            case WEIGHT: loadWeightHistory(); break;
            case NUTRITION: loadNutritionHistory(); break;
            case WATER: loadWaterHistory(); break;
            case EXERCISE: loadExerciseHistory(); break;
            case SLEEP: loadSleepHistory(); break;
        }
    }

    private void loadBMIHistory() {
        tvHeaderTitle.setText("📊 BMI Calculation Logs");
        List<BMIRecord> list = DataManager.getBMIHistory(this);
        if (list.isEmpty()) {
            showEmptyState("⚖️", "No BMI records available yet.\nCalculate your BMI to start tracking your health.", "Calculate BMI Now");
        } else {
            showContentState();
            rvHistory.setAdapter(new BMIHistoryAdapter(this, list));
        }
    }

    private void loadWeightHistory() {
        tvHeaderTitle.setText("⚖️ Weight Tracking Logs");
        List<WeightRecord> list = DataManager.getWeightHistory(this);
        if (list.isEmpty()) {
            showEmptyState("⚖️", "No weight records available yet.\nLog your current weight to start tracking.", "Log Weight Now");
        } else {
            showContentState();
            rvHistory.setAdapter(new WeightHistoryAdapter(this, list));
        }
    }

    private void loadNutritionHistory() {
        tvHeaderTitle.setText("🥗 Daily Nutrition Logs");
        List<NutritionRecord> list = DataManager.getNutritionHistory(this);
        if (list.isEmpty()) {
            showEmptyState("🥗", "No nutrition records available yet.\nRecord your daily meals to start tracking.", "Log Nutrition Now");
        } else {
            showContentState();
            rvHistory.setAdapter(new NutritionHistoryAdapter(this, list));
        }
    }

    private void loadWaterHistory() {
        tvHeaderTitle.setText("💧 Daily Water Intake Logs");
        List<WaterRecord> list = DataManager.getWaterHistory(this);
        if (list.isEmpty()) {
            showEmptyState("💧", "No water intake records available yet.\nTrack your daily hydration to get started.", "Track Water Now");
        } else {
            showContentState();
            rvHistory.setAdapter(new WaterHistoryAdapter(this, list));
        }
    }

    private void loadExerciseHistory() {
        tvHeaderTitle.setText("🏃 Exercise Activity Logs");
        List<ExerciseRecord> list = DataManager.getExerciseHistory(this);
        if (list.isEmpty()) {
            showEmptyState("🏃", "No exercise records available yet.\nRecord your daily workouts to get started.", "Track Exercise Now");
        } else {
            showContentState();
            rvHistory.setAdapter(new ExerciseHistoryAdapter(this, list));
        }
    }

    private void loadSleepHistory() {
        tvHeaderTitle.setText("😴 Sleep Duration Logs");
        List<SleepRecord> list = DataManager.getSleepHistory(this);
        if (list.isEmpty()) {
            showEmptyState("😴", "No sleep records available yet.\nLog your daily sleep schedule to get started.", "Track Sleep Now");
        } else {
            showContentState();
            rvHistory.setAdapter(new SleepHistoryAdapter(this, list));
        }
    }

    private void showEmptyState(String icon, String message, String actionText) {
        rvHistory.setVisibility(View.GONE);
        llEmptyState.setVisibility(View.VISIBLE);
        tvClearHistory.setVisibility(View.GONE);
        tvEmptyIcon.setText(icon);
        tvEmptyMessage.setText(message);
        btnActionButton.setText(actionText);
    }

    private void showContentState() {
        llEmptyState.setVisibility(View.GONE);
        rvHistory.setVisibility(View.VISIBLE);
        tvClearHistory.setVisibility(View.VISIBLE);
    }

    private void confirmClearCurrentTabHistory() {
        String title = "Clear History";
        String message = "Are you sure you want to delete all saved records in this category?";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Clear All", (dialog, which) -> {
                    switch (currentTab) {
                        case BMI: DataManager.clearBMIHistory(this); break;
                        case WEIGHT: DataManager.clearWeightHistory(this); break;
                        case NUTRITION: DataManager.clearNutritionHistory(this); break;
                        case WATER: DataManager.clearWaterHistory(this); break;
                        case EXERCISE: DataManager.clearExerciseHistory(this); break;
                        case SLEEP: DataManager.clearSleepHistory(this); break;
                    }
                    Toast.makeText(this, "History cleared.", Toast.LENGTH_SHORT).show();
                    loadTabHistory();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
