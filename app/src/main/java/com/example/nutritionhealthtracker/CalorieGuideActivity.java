package com.example.nutritionhealthtracker;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionhealthtracker.utils.DietGenerator;

public class CalorieGuideActivity extends AppCompatActivity {

    private TextView tvTarget;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_guide);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Calorie Science Guide");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvTarget = findViewById(R.id.tvCalorieDetailTarget);

        DietGenerator.EnergyRequirements req = DietGenerator.calculateRequirements(this);
        tvTarget.setText("Estimated Goal TDEE: " + req.targetCalories + " kcal / day");
    }
}
