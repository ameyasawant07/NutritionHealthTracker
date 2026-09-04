package com.example.nutritionhealthtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nutritionhealthtracker.models.NutritionRecord;
import com.example.nutritionhealthtracker.models.SleepRecord;
import com.example.nutritionhealthtracker.models.WeightRecord;
import com.example.nutritionhealthtracker.utils.AlarmScheduler;
import com.example.nutritionhealthtracker.utils.CloudSyncEngine;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.example.nutritionhealthtracker.utils.NotificationHelper;
import com.example.nutritionhealthtracker.utils.OfflineBannerHelper;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvGreeting, tvDashStreak, tvProgressPercent;
    private TextView tvWeight, tvBMI, tvWater, tvWaterSub, tvCalories, tvMeals, tvExercise, tvSleep;
    private ProgressBar pbProgress;

    private MaterialCardView cardStreakBadge, cardWeight, cardWater, cardNutrition, cardExercise, cardSleep;
    private MaterialCardView cardGraphs, cardDietHub, cardReports, cardHistory, cardExport, cardBackup, cardProfile, cardTips;
    private MaterialCardView cardAIAssistant, cardFoodScanner;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    AlarmScheduler.scheduleUserAlarms(MainActivity.this);
                }
            });

    private OfflineBannerHelper offlineBannerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DataManager.applyTheme(this); // Apply saved theme before layout inflation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View mainRoot = findViewById(R.id.mainRoot);
        if (mainRoot != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainRoot, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }

        NotificationHelper.createNotificationChannels(this);
        requestNotificationPermissionIfNeeded();
        AlarmScheduler.scheduleUserAlarms(this);

        initViews();
        setupListeners();

        offlineBannerHelper = new OfflineBannerHelper(this);
        offlineBannerHelper.setupBanner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardData();
        CloudSyncEngine.triggerAutoSyncIfAvailable(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (offlineBannerHelper != null) {
            offlineBannerHelper.unregister();
        }
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        tvDashStreak = findViewById(R.id.tvDashStreak);
        tvProgressPercent = findViewById(R.id.tvDashProgressPercent);
        pbProgress = findViewById(R.id.pbDashProgress);

        tvWeight = findViewById(R.id.tvDashWeight);
        tvBMI = findViewById(R.id.tvDashBMI);
        tvWater = findViewById(R.id.tvDashWater);
        tvWaterSub = findViewById(R.id.tvDashWaterSub);
        tvCalories = findViewById(R.id.tvDashCalories);
        tvMeals = findViewById(R.id.tvDashMeals);
        tvExercise = findViewById(R.id.tvDashExercise);
        tvSleep = findViewById(R.id.tvDashSleep);

        cardStreakBadge = findViewById(R.id.cardStreakBadge);
        cardWeight = findViewById(R.id.cardWeight);
        cardWater = findViewById(R.id.cardWater);
        cardNutrition = findViewById(R.id.cardNutrition);
        cardExercise = findViewById(R.id.cardExercise);
        cardSleep = findViewById(R.id.cardSleep);
        cardGraphs = findViewById(R.id.cardGraphs);
        cardDietHub = findViewById(R.id.cardDietHub);
        cardReports = findViewById(R.id.cardReports);
        cardHistory = findViewById(R.id.cardHistory);
        cardExport = findViewById(R.id.cardExport);
        cardBackup = findViewById(R.id.cardBackup);
        cardProfile = findViewById(R.id.cardProfile);
        cardTips = findViewById(R.id.cardTips);
        cardAIAssistant = findViewById(R.id.cardAIAssistant);
        cardFoodScanner = findViewById(R.id.cardFoodScanner);
    }

    private void updateDashboardData() {
        String todayKey = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        // Greeting & Streak
        String name = DataManager.getProfileName(this);
        if (name != null && !name.trim().isEmpty()) {
            tvGreeting.setText("Welcome back, " + name + "!");
        } else {
            tvGreeting.setText("Welcome to Nutrition & Health Tracker!");
        }

        int currentStreak = DataManager.getCurrentStreak(this);
        tvDashStreak.setText("🔥 " + currentStreak + " Days");

        // 1. Weight & BMI
        WeightRecord latestWeight = DataManager.getLatestWeightRecord(this);
        if (latestWeight != null) {
            tvWeight.setText(String.format(Locale.US, "%.1f kg", latestWeight.getWeightKg()));
            double heightCm = DataManager.getProfileHeight(this);
            if (heightCm > 0) {
                double heightM = heightCm / 100.0;
                double bmi = latestWeight.getWeightKg() / (heightM * heightM);
                tvBMI.setText(String.format(Locale.US, "BMI: %.1f", bmi));
            } else {
                tvBMI.setText("BMI: --");
            }
        } else {
            tvWeight.setText("-- kg");
            tvBMI.setText("BMI: --");
        }

        // 2. Water
        int waterIntake = DataManager.getWaterIntake(this, todayKey);
        int waterTarget = DataManager.getUserWaterTarget(this);
        tvWater.setText(waterIntake + " / " + waterTarget + " ml");

        int waterPercent = targetPercent(waterIntake, waterTarget);
        tvWaterSub.setText(waterPercent + "% goal met");

        // 3. Nutrition & Meals
        NutritionRecord nutritionToday = DataManager.getNutritionRecord(this, todayKey);
        int calories = (nutritionToday != null) ? nutritionToday.getCalories() : 0;
        tvCalories.setText(calories + " kcal");

        boolean hasB = nutritionToday != null && !nutritionToday.getBreakfast().isEmpty();
        boolean hasL = nutritionToday != null && !nutritionToday.getLunch().isEmpty();
        boolean hasD = nutritionToday != null && !nutritionToday.getDinner().isEmpty();

        tvMeals.setText("B: " + (hasB ? "✓" : "✗") + " | L: " + (hasL ? "✓" : "✗") + " | D: " + (hasD ? "✓" : "✗"));

        // 4. Exercise
        int exerciseMins = DataManager.getExerciseMinutesForDate(this, todayKey);
        tvExercise.setText(exerciseMins + " mins");

        // 5. Sleep
        SleepRecord latestSleep = DataManager.getLatestSleepRecord(this);
        if (latestSleep != null) {
            tvSleep.setText(String.format(Locale.US, "%.1f hrs", latestSleep.getDurationHours()));
        } else {
            tvSleep.setText("0.0 hrs");
        }

        // Overall Daily Progress Calculation (4 weighted items: Water, Meals, Exercise, Sleep)
        int completedTasks = 0;
        if (waterIntake >= waterTarget) completedTasks++;
        if (hasB && hasL && hasD) completedTasks++;
        if (exerciseMins >= 20) completedTasks++;
        if (latestSleep != null && latestSleep.getDurationHours() >= 7.0) completedTasks++;

        int totalProgressPercent = (completedTasks * 25);
        tvProgressPercent.setText(totalProgressPercent + "%");
        pbProgress.setProgress(totalProgressPercent);
    }

    private int targetPercent(int current, int target) {
        if (target <= 0) return 0;
        return (int) (((double) current / target) * 100);
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void setupListeners() {
        cardStreakBadge.setOnClickListener(v -> startActivity(new Intent(this, StreakActivity.class)));
        cardWeight.setOnClickListener(v -> startActivity(new Intent(this, WeightTrackerActivity.class)));
        cardWater.setOnClickListener(v -> startActivity(new Intent(this, WaterTrackerActivity.class)));
        cardNutrition.setOnClickListener(v -> startActivity(new Intent(this, NutritionActivity.class)));
        cardExercise.setOnClickListener(v -> startActivity(new Intent(this, ExerciseTrackerActivity.class)));
        cardSleep.setOnClickListener(v -> startActivity(new Intent(this, SleepTrackerActivity.class)));
        cardGraphs.setOnClickListener(v -> startActivity(new Intent(this, GraphsActivity.class)));
        cardDietHub.setOnClickListener(v -> startActivity(new Intent(this, DietHubActivity.class)));
        cardReports.setOnClickListener(v -> startActivity(new Intent(this, ReportsActivity.class)));
        cardHistory.setOnClickListener(v -> startActivity(new Intent(this, HealthHistoryActivity.class)));
        cardExport.setOnClickListener(v -> startActivity(new Intent(this, ExportDataActivity.class)));
        cardBackup.setOnClickListener(v -> startActivity(new Intent(this, BackupRestoreActivity.class)));
        cardProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        cardTips.setOnClickListener(v -> startActivity(new Intent(this, HealthTipsActivity.class)));
        if (cardAIAssistant != null) {
            cardAIAssistant.setOnClickListener(v -> startActivity(new Intent(this, AIHealthAssistantActivity.class)));
        }
        if (cardFoodScanner != null) {
            cardFoodScanner.setOnClickListener(v -> startActivity(new Intent(this, FoodScanActivity.class)));
        }
    }
}
