package com.example.nutritionhealthtracker.models;

import org.json.JSONException;
import org.json.JSONObject;

public class SavedReport {
    private String id;
    private String reportType; // "WEEKLY" or "MONTHLY"
    private String title;
    private String dateRange;
    private String createdAt;
    private double startWeight;
    private double currentWeight;
    private double weightChange;
    private double avgBMI;
    private int avgWaterMl;
    private int avgCaloriesKcal;
    private int loggedNutritionDays;
    private int totalPeriodDays;
    private double avgSleepHours;
    private int totalExerciseMins;
    private int currentStreak;
    private int longestStreak;

    public SavedReport() {
    }

    public SavedReport(String id, String reportType, String title, String dateRange, String createdAt,
                       double startWeight, double currentWeight, double weightChange, double avgBMI,
                       int avgWaterMl, int avgCaloriesKcal, int loggedNutritionDays, int totalPeriodDays,
                       double avgSleepHours, int totalExerciseMins, int currentStreak, int longestStreak) {
        this.id = id;
        this.reportType = reportType;
        this.title = title;
        this.dateRange = dateRange;
        this.createdAt = createdAt;
        this.startWeight = startWeight;
        this.currentWeight = currentWeight;
        this.weightChange = weightChange;
        this.avgBMI = avgBMI;
        this.avgWaterMl = avgWaterMl;
        this.avgCaloriesKcal = avgCaloriesKcal;
        this.loggedNutritionDays = loggedNutritionDays;
        this.totalPeriodDays = totalPeriodDays;
        this.avgSleepHours = avgSleepHours;
        this.totalExerciseMins = totalExerciseMins;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id);
            obj.put("reportType", reportType);
            obj.put("title", title);
            obj.put("dateRange", dateRange);
            obj.put("createdAt", createdAt);
            obj.put("startWeight", startWeight);
            obj.put("currentWeight", currentWeight);
            obj.put("weightChange", weightChange);
            obj.put("avgBMI", avgBMI);
            obj.put("avgWaterMl", avgWaterMl);
            obj.put("avgCaloriesKcal", avgCaloriesKcal);
            obj.put("loggedNutritionDays", loggedNutritionDays);
            obj.put("totalPeriodDays", totalPeriodDays);
            obj.put("avgSleepHours", avgSleepHours);
            obj.put("totalExerciseMins", totalExerciseMins);
            obj.put("currentStreak", currentStreak);
            obj.put("longestStreak", longestStreak);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static SavedReport fromJson(JSONObject obj) {
        if (obj == null) return null;
        SavedReport report = new SavedReport();
        report.id = obj.optString("id", "");
        report.reportType = obj.optString("reportType", "WEEKLY");
        report.title = obj.optString("title", "");
        report.dateRange = obj.optString("dateRange", "");
        report.createdAt = obj.optString("createdAt", "");
        report.startWeight = obj.optDouble("startWeight", 0.0);
        report.currentWeight = obj.optDouble("currentWeight", 0.0);
        report.weightChange = obj.optDouble("weightChange", 0.0);
        report.avgBMI = obj.optDouble("avgBMI", 0.0);
        report.avgWaterMl = obj.optInt("avgWaterMl", 0);
        report.avgCaloriesKcal = obj.optInt("avgCaloriesKcal", 0);
        report.loggedNutritionDays = obj.optInt("loggedNutritionDays", 0);
        report.totalPeriodDays = obj.optInt("totalPeriodDays", 7);
        report.avgSleepHours = obj.optDouble("avgSleepHours", 0.0);
        report.totalExerciseMins = obj.optInt("totalExerciseMins", 0);
        report.currentStreak = obj.optInt("currentStreak", 0);
        report.longestStreak = obj.optInt("longestStreak", 0);
        return report;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDateRange() { return dateRange; }
    public void setDateRange(String dateRange) { this.dateRange = dateRange; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public double getStartWeight() { return startWeight; }
    public void setStartWeight(double startWeight) { this.startWeight = startWeight; }

    public double getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(double currentWeight) { this.currentWeight = currentWeight; }

    public double getWeightChange() { return weightChange; }
    public void setWeightChange(double weightChange) { this.weightChange = weightChange; }

    public double getAvgBMI() { return avgBMI; }
    public void setAvgBMI(double avgBMI) { this.avgBMI = avgBMI; }

    public int getAvgWaterMl() { return avgWaterMl; }
    public void setAvgWaterMl(int avgWaterMl) { this.avgWaterMl = avgWaterMl; }

    public int getAvgCaloriesKcal() { return avgCaloriesKcal; }
    public void setAvgCaloriesKcal(int avgCaloriesKcal) { this.avgCaloriesKcal = avgCaloriesKcal; }

    public int getLoggedNutritionDays() { return loggedNutritionDays; }
    public void setLoggedNutritionDays(int loggedNutritionDays) { this.loggedNutritionDays = loggedNutritionDays; }

    public int getTotalPeriodDays() { return totalPeriodDays; }
    public void setTotalPeriodDays(int totalPeriodDays) { this.totalPeriodDays = totalPeriodDays; }

    public double getAvgSleepHours() { return avgSleepHours; }
    public void setAvgSleepHours(double avgSleepHours) { this.avgSleepHours = avgSleepHours; }

    public int getTotalExerciseMins() { return totalExerciseMins; }
    public void setTotalExerciseMins(int totalExerciseMins) { this.totalExerciseMins = totalExerciseMins; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }
}
