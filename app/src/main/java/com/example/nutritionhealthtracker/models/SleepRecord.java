package com.example.nutritionhealthtracker.models;

public class SleepRecord {
    private String date;            // display date e.g. "29 Aug 2026"
    private String dateKey;         // sort key e.g. "2026-08-29"
    private String sleepTime;       // e.g. "11:00 PM"
    private String wakeTime;        // e.g. "07:00 AM"
    private double durationHours;   // calculated

    public SleepRecord(String date, String dateKey, String sleepTime, String wakeTime, double durationHours) {
        this.date = date;
        this.dateKey = dateKey;
        this.sleepTime = sleepTime;
        this.wakeTime = wakeTime;
        this.durationHours = durationHours;
    }

    public String getDate() { return date; }
    public String getDateKey() { return dateKey; }
    public String getSleepTime() { return sleepTime; }
    public String getWakeTime() { return wakeTime; }
    public double getDurationHours() { return durationHours; }

    public void setDate(String date) { this.date = date; }
    public void setDateKey(String dateKey) { this.dateKey = dateKey; }
    public void setSleepTime(String sleepTime) { this.sleepTime = sleepTime; }
    public void setWakeTime(String wakeTime) { this.wakeTime = wakeTime; }
    public void setDurationHours(double durationHours) { this.durationHours = durationHours; }
}
