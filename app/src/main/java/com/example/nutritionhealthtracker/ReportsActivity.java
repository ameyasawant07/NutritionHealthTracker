package com.example.nutritionhealthtracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.adapters.SavedReportsAdapter;
import com.example.nutritionhealthtracker.models.BMIRecord;
import com.example.nutritionhealthtracker.models.ExerciseRecord;
import com.example.nutritionhealthtracker.models.NutritionRecord;
import com.example.nutritionhealthtracker.models.SavedReport;
import com.example.nutritionhealthtracker.models.SleepRecord;
import com.example.nutritionhealthtracker.models.WaterRecord;
import com.example.nutritionhealthtracker.models.WeightRecord;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity implements SavedReportsAdapter.OnReportClickListener {

    private boolean isWeekly = true;
    private String currentArchiveFilter = "ALL"; // "ALL", "WEEKLY", "MONTHLY"

    // Views — Main Mode
    private MaterialButtonToggleGroup toggleMainMode;
    private MaterialButton btnModeLive, btnModeSaved;
    private View layoutLiveBreakdown, layoutSavedArchive;

    // Views — Live Breakdown
    private MaterialButtonToggleGroup toggleGroupReport;
    private TextView tvHeader, tvStartWeight, tvCurrentWeight, tvWeightChange, tvAvgBMI;
    private TextView tvAvgWater, tvAvgCalories, tvAvgSleep, tvTotalExercise;
    private TextView tvNutritionDays, tvStreak;
    private MaterialButton btnSaveReportSnapshot;

    // Views — Saved Archive
    private MaterialButtonToggleGroup toggleFilterArchive;
    private TextView tvEmptySavedReports;
    private RecyclerView rvSavedReports;
    private SavedReportsAdapter savedReportsAdapter;

    private Toolbar toolbar;

    // Computed Current Live Metrics (for snapshot saving)
    private double liveStartWeight = 0, liveCurrentWeight = 0, liveWeightChange = 0, liveAvgBMI = 0;
    private int liveAvgWater = 0, liveAvgCalories = 0, liveNutDays = 0, liveTotalDays = 7;
    private double liveAvgSleep = 0;
    private int liveTotalExercise = 0, liveCurrentStreak = 0, liveLongestStreak = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Health Reports");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Mode views
        toggleMainMode = findViewById(R.id.toggleMainMode);
        btnModeLive = findViewById(R.id.btnModeLive);
        btnModeSaved = findViewById(R.id.btnModeSaved);
        layoutLiveBreakdown = findViewById(R.id.layoutLiveBreakdown);
        layoutSavedArchive = findViewById(R.id.layoutSavedArchive);

        // Live views
        toggleGroupReport = findViewById(R.id.toggleGroupReport);
        tvHeader = findViewById(R.id.tvReportHeader);
        tvStartWeight = findViewById(R.id.tvRepStartWeight);
        tvCurrentWeight = findViewById(R.id.tvRepCurrentWeight);
        tvWeightChange = findViewById(R.id.tvRepWeightChange);
        tvAvgBMI = findViewById(R.id.tvRepAvgBMI);

        tvAvgWater = findViewById(R.id.tvRepAvgWater);
        tvAvgCalories = findViewById(R.id.tvRepAvgCalories);
        tvAvgSleep = findViewById(R.id.tvRepAvgSleep);
        tvTotalExercise = findViewById(R.id.tvRepTotalExercise);

        tvNutritionDays = findViewById(R.id.tvRepNutritionDays);
        tvStreak = findViewById(R.id.tvRepStreak);
        btnSaveReportSnapshot = findViewById(R.id.btnSaveReportSnapshot);

        // Saved views
        toggleFilterArchive = findViewById(R.id.toggleFilterArchive);
        tvEmptySavedReports = findViewById(R.id.tvEmptySavedReports);
        rvSavedReports = findViewById(R.id.rvSavedReports);

        // Adapter setup
        savedReportsAdapter = new SavedReportsAdapter(this);
        rvSavedReports.setLayoutManager(new LinearLayoutManager(this));
        rvSavedReports.setAdapter(savedReportsAdapter);

        // Mode switch listener
        toggleMainMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnModeLive) {
                    layoutLiveBreakdown.setVisibility(View.VISIBLE);
                    layoutSavedArchive.setVisibility(View.GONE);
                } else if (checkedId == R.id.btnModeSaved) {
                    layoutLiveBreakdown.setVisibility(View.GONE);
                    layoutSavedArchive.setVisibility(View.VISIBLE);
                    loadSavedReports();
                }
            }
        });

        // Weekly / Monthly switch listener
        toggleGroupReport.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                isWeekly = (checkedId == R.id.btnReportWeekly);
                calculateAndRenderReport();
            }
        });

        // Filter archive switch listener
        toggleFilterArchive.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnFilterWeekly) {
                    currentArchiveFilter = "WEEKLY";
                } else if (checkedId == R.id.btnFilterMonthly) {
                    currentArchiveFilter = "MONTHLY";
                } else {
                    currentArchiveFilter = "ALL";
                }
                loadSavedReports();
            }
        });

        // Save Report button
        btnSaveReportSnapshot.setOnClickListener(v -> saveCurrentReportSnapshot());

        calculateAndRenderReport();
        updateSavedArchiveBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        calculateAndRenderReport();
        loadSavedReports();
    }

    private void calculateAndRenderReport() {
        liveTotalDays = isWeekly ? 7 : 30;
        tvHeader.setText(isWeekly ? "📊 Weekly Health Breakdown (7 Days)" : "🗓️ Monthly Health Breakdown (30 Days)");

        // 1. Weight & Net Change & BMI
        List<WeightRecord> weights = DataManager.getWeightHistory(this);
        int weightCount = Math.min(weights.size(), liveTotalDays);
        if (weightCount > 0) {
            WeightRecord latest = weights.get(0);
            WeightRecord oldestInPeriod = weights.get(weightCount - 1);

            liveCurrentWeight = latest.getWeightKg();
            liveStartWeight = oldestInPeriod.getWeightKg();
            liveWeightChange = liveCurrentWeight - liveStartWeight;

            tvCurrentWeight.setText(String.format(Locale.US, "%.1f kg", liveCurrentWeight));
            tvStartWeight.setText(String.format(Locale.US, "%.1f kg", liveStartWeight));

            String prefix = liveWeightChange > 0 ? "+" : "";
            tvWeightChange.setText(String.format(Locale.US, "%s%.1f kg", prefix, liveWeightChange));

            double heightCm = DataManager.getProfileHeight(this);
            if (heightCm > 0) {
                double totalBMI = 0;
                for (int i = 0; i < weightCount; i++) {
                    double hM = heightCm / 100.0;
                    totalBMI += weights.get(i).getWeightKg() / (hM * hM);
                }
                liveAvgBMI = totalBMI / weightCount;
                tvAvgBMI.setText(String.format(Locale.US, "Average Period BMI: %.1f", liveAvgBMI));
            } else {
                liveAvgBMI = 0;
                tvAvgBMI.setText("Average Period BMI: --");
            }
        } else {
            liveStartWeight = 0;
            liveCurrentWeight = 0;
            liveWeightChange = 0;
            liveAvgBMI = 0;
            tvStartWeight.setText("-- kg");
            tvCurrentWeight.setText("-- kg");
            tvWeightChange.setText("0.0 kg");
            tvAvgBMI.setText("Average Period BMI: --");
        }

        // 2. Water Average
        List<WaterRecord> waterList = DataManager.getWaterHistory(this);
        int waterDays = Math.min(waterList.size(), liveTotalDays);
        if (waterDays > 0) {
            int sum = 0;
            for (int i = 0; i < waterDays; i++) sum += waterList.get(i).getConsumedMl();
            liveAvgWater = sum / waterDays;
            tvAvgWater.setText(liveAvgWater + " ml/day");
        } else {
            liveAvgWater = 0;
            tvAvgWater.setText("0 ml/day");
        }

        // 3. Calorie Average & Nutrition Logged Days
        List<NutritionRecord> nutList = DataManager.getNutritionHistory(this);
        liveNutDays = Math.min(nutList.size(), liveTotalDays);
        if (liveNutDays > 0) {
            int sumCals = 0;
            for (int i = 0; i < liveNutDays; i++) sumCals += nutList.get(i).getCalories();
            liveAvgCalories = sumCals / liveNutDays;
            tvAvgCalories.setText(liveAvgCalories + " kcal/day");
            tvNutritionDays.setText("Nutrition Logged: " + liveNutDays + " / " + liveTotalDays + " days");
        } else {
            liveAvgCalories = 0;
            tvAvgCalories.setText("0 kcal/day");
            tvNutritionDays.setText("Nutrition Logged: 0 / " + liveTotalDays + " days");
        }

        // 4. Sleep Average
        List<SleepRecord> sleepList = DataManager.getSleepHistory(this);
        int sleepDays = Math.min(sleepList.size(), liveTotalDays);
        if (sleepDays > 0) {
            double sumHrs = 0;
            for (int i = 0; i < sleepDays; i++) sumHrs += sleepList.get(i).getDurationHours();
            liveAvgSleep = sumHrs / sleepDays;
            tvAvgSleep.setText(String.format(Locale.US, "%.1f hrs/night", liveAvgSleep));
        } else {
            liveAvgSleep = 0;
            tvAvgSleep.setText("0.0 hrs/night");
        }

        // 5. Total Exercise Minutes
        List<ExerciseRecord> exList = DataManager.getExerciseHistory(this);
        int exCount = Math.min(exList.size(), liveTotalDays);
        liveTotalExercise = 0;
        for (int i = 0; i < exCount; i++) liveTotalExercise += exList.get(i).getDurationMinutes();
        tvTotalExercise.setText(liveTotalExercise + " mins");

        // 6. Streak Status
        liveCurrentStreak = DataManager.getCurrentStreak(this);
        liveLongestStreak = DataManager.getLongestStreak(this);
        tvStreak.setText("Current Streak: " + liveCurrentStreak + " days (Best: " + liveLongestStreak + " days)");
    }

    private void saveCurrentReportSnapshot() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        SimpleDateFormat sdfTime = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);

        Calendar calEnd = Calendar.getInstance();
        Calendar calStart = Calendar.getInstance();
        calStart.add(Calendar.DAY_OF_YEAR, -(liveTotalDays - 1));

        String startDateStr = sdfDate.format(calStart.getTime());
        String endDateStr = sdfDate.format(calEnd.getTime());
        String dateRange = startDateStr + " - " + endDateStr;

        String type = isWeekly ? "WEEKLY" : "MONTHLY";
        String title = (isWeekly ? "Weekly Report (" : "Monthly Report (") + dateRange + ")";
        String reportId = "report_" + type.toLowerCase() + "_" + System.currentTimeMillis();
        String createdAt = sdfTime.format(calEnd.getTime());

        SavedReport report = new SavedReport(
                reportId,
                type,
                title,
                dateRange,
                createdAt,
                liveStartWeight,
                liveCurrentWeight,
                liveWeightChange,
                liveAvgBMI,
                liveAvgWater,
                liveAvgCalories,
                liveNutDays,
                liveTotalDays,
                liveAvgSleep,
                liveTotalExercise,
                liveCurrentStreak,
                liveLongestStreak
        );

        DataManager.saveReport(this, report);
        Toast.makeText(this, "✅ Report saved to Saved Archive!", Toast.LENGTH_SHORT).show();
        updateSavedArchiveBadge();
    }

    private void loadSavedReports() {
        List<SavedReport> allReports = DataManager.getSavedReports(this);
        List<SavedReport> filtered = new ArrayList<>();

        for (SavedReport r : allReports) {
            if ("ALL".equals(currentArchiveFilter)) {
                filtered.add(r);
            } else if (currentArchiveFilter.equalsIgnoreCase(r.getReportType())) {
                filtered.add(r);
            }
        }

        savedReportsAdapter.setReports(filtered);

        if (filtered.isEmpty()) {
            rvSavedReports.setVisibility(View.GONE);
            tvEmptySavedReports.setVisibility(View.VISIBLE);
        } else {
            rvSavedReports.setVisibility(View.VISIBLE);
            tvEmptySavedReports.setVisibility(View.GONE);
        }

        updateSavedArchiveBadge();
    }

    private void updateSavedArchiveBadge() {
        int count = DataManager.getSavedReports(this).size();
        btnModeSaved.setText("📂 Saved Archive (" + count + ")");
    }

    @Override
    public void onReportClick(SavedReport report) {
        showReportDetailDialog(report);
    }

    @Override
    public void onReportDelete(SavedReport report) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Report")
                .setMessage("Are you sure you want to delete this saved report snapshot?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    DataManager.deleteReport(this, report.getId());
                    Toast.makeText(this, "Report deleted", Toast.LENGTH_SHORT).show();
                    loadSavedReports();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showReportDetailDialog(SavedReport report) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(report.getTitle());

        StringBuilder sb = new StringBuilder();
        sb.append("📋 TYPE: ").append(report.getReportType()).append("\n");
        sb.append("📅 PERIOD: ").append(report.getDateRange()).append("\n");
        sb.append("🕒 SAVED: ").append(report.getCreatedAt()).append("\n\n");

        sb.append("⚖️ WEIGHT & BMI:\n");
        if (report.getStartWeight() > 0 || report.getCurrentWeight() > 0) {
            sb.append(String.format(Locale.US, "  • Starting Weight: %.1f kg\n", report.getStartWeight()));
            sb.append(String.format(Locale.US, "  • Ending Weight: %.1f kg\n", report.getCurrentWeight()));
            String prefix = report.getWeightChange() > 0 ? "+" : "";
            sb.append(String.format(Locale.US, "  • Net Change: %s%.1f kg\n", prefix, report.getWeightChange()));
            if (report.getAvgBMI() > 0) {
                sb.append(String.format(Locale.US, "  • Average Period BMI: %.1f\n", report.getAvgBMI()));
            }
        } else {
            sb.append("  • No weight data recorded\n");
        }

        sb.append("\n💧 WATER INTAKE:\n");
        sb.append("  • Average: ").append(report.getAvgWaterMl()).append(" ml / day\n");

        sb.append("\n🔥 NUTRITION & CALORIES:\n");
        sb.append("  • Average Calories: ").append(report.getAvgCaloriesKcal()).append(" kcal / day\n");
        sb.append("  • Logging Consistency: ").append(report.getLoggedNutritionDays()).append(" / ").append(report.getTotalPeriodDays()).append(" days\n");

        sb.append("\n😴 SLEEP:\n");
        sb.append(String.format(Locale.US, "  • Average Sleep: %.1f hrs / night\n", report.getAvgSleepHours()));

        sb.append("\n🏃 EXERCISE:\n");
        sb.append("  • Total Exercise: ").append(report.getTotalExerciseMins()).append(" minutes\n");

        sb.append("\n🎯 STREAKS:\n");
        sb.append("  • Streak Status: ").append(report.getCurrentStreak()).append(" days (Best: ").append(report.getLongestStreak()).append(" days)\n");

        builder.setMessage(sb.toString());

        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.setNegativeButton("Delete Report", (dialog, which) -> onReportDelete(report));

        builder.show();
    }
}
