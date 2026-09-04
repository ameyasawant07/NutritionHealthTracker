package com.example.nutritionhealthtracker.models;

public class BMIRecord {
    private String date;
    private double height;
    private double weight;
    private double bmi;
    private String category;

    public BMIRecord(String date, double height, double weight, double bmi, String category) {
        this.date = date;
        this.height = height;
        this.weight = weight;
        this.bmi = bmi;
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public double getBmi() {
        return bmi;
    }

    public String getCategory() {
        return category;
    }
}
