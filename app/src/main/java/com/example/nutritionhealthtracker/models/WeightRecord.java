package com.example.nutritionhealthtracker.models;

public class WeightRecord {
    private String date;        // display date e.g. "29 Aug 2026"
    private String dateKey;     // sort key e.g. "2026-08-29"
    private String time;        // e.g. "08:30 AM"
    private double weightKg;

    public WeightRecord(String date, String dateKey, String time, double weightKg) {
        this.date = date;
        this.dateKey = dateKey;
        this.time = time;
        this.weightKg = weightKg;
    }

    public String getDate() { return date; }
    public String getDateKey() { return dateKey; }
    public String getTime() { return time; }
    public double getWeightKg() { return weightKg; }

    public void setDate(String date) { this.date = date; }
    public void setDateKey(String dateKey) { this.dateKey = dateKey; }
    public void setTime(String time) { this.time = time; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
}
