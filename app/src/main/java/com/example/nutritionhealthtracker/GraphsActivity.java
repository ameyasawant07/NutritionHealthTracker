package com.example.nutritionhealthtracker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionhealthtracker.models.BMIRecord;
import com.example.nutritionhealthtracker.models.ExerciseRecord;
import com.example.nutritionhealthtracker.models.NutritionRecord;
import com.example.nutritionhealthtracker.models.SleepRecord;
import com.example.nutritionhealthtracker.models.WaterRecord;
import com.example.nutritionhealthtracker.models.WeightRecord;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphsActivity extends AppCompatActivity {

    private enum ChartType {
        WEIGHT, BMI, WATER, CALORIES, EXERCISE, SLEEP, MEALS
    }

    private ChartType currentType = ChartType.WEIGHT;

    private MaterialButtonToggleGroup toggleGroup;
    private TextView tvTitle;
    private LineChart lineChart;
    private BarChart barChart;
    private LinearLayout llEmptyState;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Health Analytics & Trends");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        toggleGroup = findViewById(R.id.toggleGroupChart);
        tvTitle = findViewById(R.id.tvChartTitle);
        lineChart = findViewById(R.id.lineChart);
        barChart = findViewById(R.id.barChart);
        llEmptyState = findViewById(R.id.llEmptyChartState);

        setupChartStyling();

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnChartWeight) currentType = ChartType.WEIGHT;
                else if (checkedId == R.id.btnChartBMI) currentType = ChartType.BMI;
                else if (checkedId == R.id.btnChartWater) currentType = ChartType.WATER;
                else if (checkedId == R.id.btnChartCalories) currentType = ChartType.CALORIES;
                else if (checkedId == R.id.btnChartExercise) currentType = ChartType.EXERCISE;
                else if (checkedId == R.id.btnChartSleep) currentType = ChartType.SLEEP;
                else if (checkedId == R.id.btnChartNutrition) currentType = ChartType.MEALS;
                renderActiveChart();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderActiveChart();
    }

    private void setupChartStyling() {
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        XAxis lineX = lineChart.getXAxis();
        lineX.setPosition(XAxis.XAxisPosition.BOTTOM);
        lineX.setGranularity(1f);

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.setTouchEnabled(true);

        XAxis barX = barChart.getXAxis();
        barX.setPosition(XAxis.XAxisPosition.BOTTOM);
        barX.setGranularity(1f);
    }

    private void renderActiveChart() {
        switch (currentType) {
            case WEIGHT: renderWeightChart(); break;
            case BMI: renderBMIChart(); break;
            case WATER: renderWaterChart(); break;
            case CALORIES: renderCaloriesChart(); break;
            case EXERCISE: renderExerciseChart(); break;
            case SLEEP: renderSleepChart(); break;
            case MEALS: renderMealsChart(); break;
        }
    }

    private void showEmptyState() {
        lineChart.setVisibility(View.GONE);
        barChart.setVisibility(View.GONE);
        llEmptyState.setVisibility(View.VISIBLE);
    }

    private void showLineChart() {
        llEmptyState.setVisibility(View.GONE);
        barChart.setVisibility(View.GONE);
        lineChart.setVisibility(View.VISIBLE);
    }

    private void showBarChart() {
        llEmptyState.setVisibility(View.GONE);
        lineChart.setVisibility(View.GONE);
        barChart.setVisibility(View.VISIBLE);
    }

    private void renderWeightChart() {
        tvTitle.setText("⚖️ Weight Progress Trend (kg)");
        List<WeightRecord> records = DataManager.getWeightHistory(this);
        if (records.size() < 2) {
            showEmptyState();
            return;
        }

        List<WeightRecord> sorted = new ArrayList<>(records);
        Collections.reverse(sorted); // Oldest first for trend

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < sorted.size(); i++) {
            WeightRecord r = sorted.get(i);
            entries.add(new Entry(i, (float) r.getWeightKg()));
            labels.add(r.getDate().length() > 6 ? r.getDate().substring(0, 6) : r.getDate());
        }

        LineDataSet dataSet = new LineDataSet(entries, "Weight (kg)");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setCircleColor(Color.parseColor("#388E3C"));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(5f);
        dataSet.setValueTextSize(10f);

        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        lineChart.setData(new LineData(dataSet));
        lineChart.invalidate();
        showLineChart();
    }

    private void renderBMIChart() {
        tvTitle.setText("⚖️ BMI Score Trend");
        List<BMIRecord> records = DataManager.getBMIHistory(this);
        if (records.size() < 2) {
            showEmptyState();
            return;
        }

        List<BMIRecord> sorted = new ArrayList<>(records);
        Collections.reverse(sorted);

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < sorted.size(); i++) {
            BMIRecord r = sorted.get(i);
            entries.add(new Entry(i, (float) r.getBmi()));
            labels.add(r.getDate().length() > 6 ? r.getDate().substring(0, 6) : r.getDate());
        }

        LineDataSet dataSet = new LineDataSet(entries, "BMI Score");
        dataSet.setColor(Color.parseColor("#2196F3"));
        dataSet.setCircleColor(Color.parseColor("#1976D2"));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(5f);

        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        lineChart.setData(new LineData(dataSet));
        lineChart.invalidate();
        showLineChart();
    }

    private void renderWaterChart() {
        tvTitle.setText("💧 Daily Water Intake (ml)");
        List<WaterRecord> records = DataManager.getWaterHistory(this);
        if (records.size() < 2) {
            showEmptyState();
            return;
        }

        List<WaterRecord> sorted = new ArrayList<>(records);
        Collections.reverse(sorted);

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < sorted.size(); i++) {
            WaterRecord r = sorted.get(i);
            entries.add(new BarEntry(i, r.getConsumedMl()));
            labels.add(r.getDate().length() > 6 ? r.getDate().substring(0, 6) : r.getDate());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Water Intake (ml)");
        dataSet.setColor(Color.parseColor("#0288D1"));

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setData(new BarData(dataSet));
        barChart.invalidate();
        showBarChart();
    }

    private void renderCaloriesChart() {
        tvTitle.setText("🔥 Daily Calorie Intake (kcal)");
        List<NutritionRecord> records = DataManager.getNutritionHistory(this);
        if (records.size() < 2) {
            showEmptyState();
            return;
        }

        List<NutritionRecord> sorted = new ArrayList<>(records);
        Collections.reverse(sorted);

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < sorted.size(); i++) {
            NutritionRecord r = sorted.get(i);
            entries.add(new BarEntry(i, r.getCalories()));
            labels.add(r.getDate().length() > 6 ? r.getDate().substring(0, 6) : r.getDate());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Calories (kcal)");
        dataSet.setColor(Color.parseColor("#FF9800"));

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setData(new BarData(dataSet));
        barChart.invalidate();
        showBarChart();
    }

    private void renderExerciseChart() {
        tvTitle.setText("🏃 Daily Exercise Duration (mins)");
        List<ExerciseRecord> records = DataManager.getExerciseHistory(this);
        if (records.size() < 2) {
            showEmptyState();
            return;
        }

        List<ExerciseRecord> sorted = new ArrayList<>(records);
        Collections.reverse(sorted);

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < sorted.size(); i++) {
            ExerciseRecord r = sorted.get(i);
            entries.add(new BarEntry(i, r.getDurationMinutes()));
            labels.add(r.getDate().length() > 6 ? r.getDate().substring(0, 6) : r.getDate());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Exercise (minutes)");
        dataSet.setColor(Color.parseColor("#9C27B0"));

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setData(new BarData(dataSet));
        barChart.invalidate();
        showBarChart();
    }

    private void renderSleepChart() {
        tvTitle.setText("😴 Daily Sleep Duration (hours)");
        List<SleepRecord> records = DataManager.getSleepHistory(this);
        if (records.size() < 2) {
            showEmptyState();
            return;
        }

        List<SleepRecord> sorted = new ArrayList<>(records);
        Collections.reverse(sorted);

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < sorted.size(); i++) {
            SleepRecord r = sorted.get(i);
            entries.add(new BarEntry(i, (float) r.getDurationHours()));
            labels.add(r.getDate().length() > 6 ? r.getDate().substring(0, 6) : r.getDate());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Sleep (hours)");
        dataSet.setColor(Color.parseColor("#3F51B5"));

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setData(new BarData(dataSet));
        barChart.invalidate();
        showBarChart();
    }

    private void renderMealsChart() {
        tvTitle.setText("🥗 Daily Meal Completion (%)");
        List<NutritionRecord> records = DataManager.getNutritionHistory(this);
        if (records.size() < 2) {
            showEmptyState();
            return;
        }

        List<NutritionRecord> sorted = new ArrayList<>(records);
        Collections.reverse(sorted);

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < sorted.size(); i++) {
            NutritionRecord r = sorted.get(i);
            int count = 0;
            if (!r.getBreakfast().isEmpty()) count++;
            if (!r.getLunch().isEmpty()) count++;
            if (!r.getDinner().isEmpty()) count++;
            if (!r.getSnacks().isEmpty()) count++;

            int percent = (int) ((count / 4.0) * 100);
            entries.add(new BarEntry(i, percent));
            labels.add(r.getDate().length() > 6 ? r.getDate().substring(0, 6) : r.getDate());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Meal Log Completion (%)");
        dataSet.setColor(Color.parseColor("#009688"));

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setData(new BarData(dataSet));
        barChart.invalidate();
        showBarChart();
    }
}
