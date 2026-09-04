package com.example.nutritionhealthtracker;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.nutritionhealthtracker.models.BMIRecord;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BMICalculatorActivity extends AppCompatActivity {

    private TextInputEditText etHeight, etWeight;
    private MaterialButton btnCalculate;
    private MaterialCardView cardResult;
    private TextView tvBMIValue, tvBMICategory, tvBMIExplanation;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_calculator);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("BMI Calculator");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        etHeight = findViewById(R.id.etBMIHeight);
        etWeight = findViewById(R.id.etBMIWeight);
        btnCalculate = findViewById(R.id.btnCalculateBMI);
        cardResult = findViewById(R.id.cardResult);
        tvBMIValue = findViewById(R.id.tvBMIValue);
        tvBMICategory = findViewById(R.id.tvBMICategory);
        tvBMIExplanation = findViewById(R.id.tvBMIExplanation);

        // Pre-fill height/weight from profile if saved
        double savedHeight = DataManager.getProfileHeight(this);
        double savedWeight = DataManager.getProfileWeight(this);
        if (savedHeight > 0) {
            etHeight.setText(String.format(Locale.US, "%.1f", savedHeight));
        }
        if (savedWeight > 0) {
            etWeight.setText(String.format(Locale.US, "%.1f", savedWeight));
        }

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateAndSaveBMI();
            }
        });
    }

    private void calculateAndSaveBMI() {
        String heightStr = etHeight.getText() != null ? etHeight.getText().toString().trim() : "";
        String weightStr = etWeight.getText() != null ? etWeight.getText().toString().trim() : "";

        if (heightStr.isEmpty()) {
            etHeight.setError("Please enter your height.");
            etHeight.requestFocus();
            return;
        }

        double heightCm;
        try {
            heightCm = Double.parseDouble(heightStr);
            if (heightCm <= 0) {
                etHeight.setError("Height must be greater than zero.");
                etHeight.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etHeight.setError("Please enter a valid numeric height.");
            etHeight.requestFocus();
            return;
        }

        if (weightStr.isEmpty()) {
            etWeight.setError("Please enter your weight.");
            etWeight.requestFocus();
            return;
        }

        double weightKg;
        try {
            weightKg = Double.parseDouble(weightStr);
            if (weightKg <= 0) {
                etWeight.setError("Weight must be greater than zero.");
                etWeight.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etWeight.setError("Please enter a valid numeric weight.");
            etWeight.requestFocus();
            return;
        }

        // Calculation: height in meters
        double heightM = heightCm / 100.0;
        double bmi = weightKg / (heightM * heightM);

        String category;
        String explanation;
        int categoryColor;

        if (bmi < 18.5) {
            category = "Underweight";
            explanation = "Underweight: Your BMI is below the normal range.";
            categoryColor = ContextCompat.getColor(this, R.color.bmi_underweight);
        } else if (bmi >= 18.5 && bmi <= 24.9) {
            category = "Normal Weight";
            explanation = "Normal Weight: Your BMI is within the normal range.";
            categoryColor = ContextCompat.getColor(this, R.color.bmi_normal);
        } else if (bmi >= 25.0 && bmi <= 29.9) {
            category = "Overweight";
            explanation = "Overweight: Your BMI is above the normal range.";
            categoryColor = ContextCompat.getColor(this, R.color.bmi_overweight);
        } else {
            category = "Obese";
            explanation = "Obese: Your BMI is significantly above the normal range.";
            categoryColor = ContextCompat.getColor(this, R.color.bmi_obese);
        }

        // Display results
        tvBMIValue.setText(String.format(Locale.US, "Your BMI: %.1f", bmi));
        tvBMICategory.setText(category);

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(24f);
        shape.setColor(categoryColor);
        tvBMICategory.setBackground(shape);

        tvBMIExplanation.setText(explanation);
        cardResult.setVisibility(View.VISIBLE);

        // Save record automatically
        String currentDate = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(new Date());
        BMIRecord record = new BMIRecord(currentDate, heightCm, weightKg, bmi, category);
        DataManager.saveBMIRecord(this, record);

        Toast.makeText(this, "BMI calculated and saved to history!", Toast.LENGTH_SHORT).show();
    }
}
