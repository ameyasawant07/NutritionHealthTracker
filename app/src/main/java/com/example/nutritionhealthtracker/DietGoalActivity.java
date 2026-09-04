package com.example.nutritionhealthtracker;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionhealthtracker.utils.DataManager;
import com.example.nutritionhealthtracker.utils.DietGenerator;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class DietGoalActivity extends AppCompatActivity {

    private Spinner spGoal, spDietType;
    private CheckBox cbNuts, cbLactose, cbGluten, cbSoy;
    private TextView tvCalories, tvMacros;
    private MaterialButton btnSave;
    private Toolbar toolbar;

    private static final String[] GOAL_OPTIONS = {
            "Healthy / Balanced Diet",
            "Fitness & Muscle Building",
            "Weight Loss",
            "Healthy Weight Gain",
            "General Fitness",
            "Healthy Lifestyle"
    };

    private static final String[] DIET_TYPE_OPTIONS = {
            "Vegetarian",
            "Non-Vegetarian",
            "Vegan"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_goal);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Diet Goal & Preferences");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        spGoal = findViewById(R.id.spDietGoal);
        spDietType = findViewById(R.id.spDietType);

        cbNuts = findViewById(R.id.cbNuts);
        cbLactose = findViewById(R.id.cbLactose);
        cbGluten = findViewById(R.id.cbGluten);
        cbSoy = findViewById(R.id.cbSoy);

        tvCalories = findViewById(R.id.tvTargetCalories);
        tvMacros = findViewById(R.id.tvTargetMacros);
        btnSave = findViewById(R.id.btnSaveGoalPreferences);

        setupSpinners();
        loadSavedPreferences();
        updateCalculatedTarget();

        btnSave.setOnClickListener(v -> savePreferences());
    }

    private void setupSpinners() {
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, GOAL_OPTIONS);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGoal.setAdapter(goalAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DIET_TYPE_OPTIONS);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDietType.setAdapter(typeAdapter);
    }

    private void loadSavedPreferences() {
        String goal = DataManager.getUserGoal(this);
        for (int i = 0; i < GOAL_OPTIONS.length; i++) {
            if (GOAL_OPTIONS[i].equalsIgnoreCase(goal)) {
                spGoal.setSelection(i);
                break;
            }
        }

        String type = DataManager.getDietaryType(this);
        for (int i = 0; i < DIET_TYPE_OPTIONS.length; i++) {
            if (DIET_TYPE_OPTIONS[i].equalsIgnoreCase(type)) {
                spDietType.setSelection(i);
                break;
            }
        }

        String allergies = DataManager.getAllergies(this);
        cbNuts.setChecked(allergies.contains("Nuts"));
        cbLactose.setChecked(allergies.contains("Lactose"));
        cbGluten.setChecked(allergies.contains("Gluten"));
        cbSoy.setChecked(allergies.contains("Soy"));
    }

    private void updateCalculatedTarget() {
        DietGenerator.EnergyRequirements req = DietGenerator.calculateRequirements(this);
        tvCalories.setText("Target Calories: " + req.targetCalories + " kcal / day");
        tvMacros.setText(String.format(Locale.US, "Protein: %dg • Carbs: %dg • Fat: %dg • Fiber: %dg",
                req.targetProteinGrams, req.targetCarbsGrams, req.targetFatGrams, req.targetFiberGrams));
    }

    private void savePreferences() {
        String goal = spGoal.getSelectedItem() != null ? spGoal.getSelectedItem().toString() : "Healthy / Balanced Diet";
        String type = spDietType.getSelectedItem() != null ? spDietType.getSelectedItem().toString() : "Vegetarian";

        StringBuilder allergies = new StringBuilder();
        if (cbNuts.isChecked()) allergies.append("Nuts ");
        if (cbLactose.isChecked()) allergies.append("Lactose ");
        if (cbGluten.isChecked()) allergies.append("Gluten ");
        if (cbSoy.isChecked()) allergies.append("Soy ");

        DataManager.setUserGoal(this, goal);
        DataManager.setDietaryType(this, type);
        DataManager.setAllergies(this, allergies.toString().trim());

        updateCalculatedTarget();
        Toast.makeText(this, "Diet goal & preferences saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
