package com.example.nutritionhealthtracker.models;

public class WaterRecord {
    private String date;       // Display Date (e.g., "29 Aug 2026")
    private String dateKey;    // Key Date (e.g., "2026-08-29")
    private int consumedMl;    // Total consumed in ml
    private int targetMl;      // Daily target in ml (2000 ml)

    public WaterRecord(String date, String dateKey, int consumedMl, int targetMl) {
        this.date = date;
        this.dateKey = dateKey;
        this.consumedMl = consumedMl;
        this.targetMl = targetMl;
    }

    public String getDate() {
        return date;
    }

    public String getDateKey() {
        return dateKey;
    }

    public int getConsumedMl() {
        return consumedMl;
    }

    public void setConsumedMl(int consumedMl) {
        this.consumedMl = consumedMl;
    }

    public int getTargetMl() {
        return targetMl;
    }

    public int getProgressPercentage() {
        if (targetMl <= 0) return 0;
        return Math.min(100, (int) (((double) consumedMl / targetMl) * 100));
    }
}
