package com.example.nutritionhealthtracker.models;

public class ExerciseRecord {
    private String date;        // display date e.g. "29 Aug 2026"
    private String dateKey;     // sort key e.g. "2026-08-29"
    private String time;        // e.g. "06:30 AM"
    private String exerciseType; // Walking, Running, etc.
    private int durationMinutes;

    public ExerciseRecord(String date, String dateKey, String time, String exerciseType, int durationMinutes) {
        this.date = date;
        this.dateKey = dateKey;
        this.time = time;
        this.exerciseType = exerciseType;
        this.durationMinutes = durationMinutes;
    }

    public String getDate() { return date; }
    public String getDateKey() { return dateKey; }
    public String getTime() { return time; }
    public String getExerciseType() { return exerciseType; }
    public int getDurationMinutes() { return durationMinutes; }

    public void setDate(String date) { this.date = date; }
    public void setDateKey(String dateKey) { this.dateKey = dateKey; }
    public void setTime(String time) { this.time = time; }
    public void setExerciseType(String exerciseType) { this.exerciseType = exerciseType; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
}
