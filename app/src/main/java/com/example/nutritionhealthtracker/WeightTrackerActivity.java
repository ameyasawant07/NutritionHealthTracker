package com.example.nutritionhealthtracker;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.adapters.WeightHistoryAdapter;
import com.example.nutritionhealthtracker.models.BMIRecord;
import com.example.nutritionhealthtracker.models.WeightRecord;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeightTrackerActivity extends AppCompatActivity {

    private TextInputEditText etWeight;
    private MaterialButton btnSaveWeight;
    private TextView tvCurrentBMI, tvLatestWeight;
    private RecyclerView rvWeightHistory;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_tracker);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Weight Tracker");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        etWeight = findViewById(R.id.etWeight);
        btnSaveWeight = findViewById(R.id.btnSaveWeight);
        tvCurrentBMI = findViewById(R.id.tvCurrentBMI);
        tvLatestWeight = findViewById(R.id.tvLatestWeight);
        rvWeightHistory = findViewById(R.id.rvWeightHistory);

        rvWeightHistory.setLayoutManager(new LinearLayoutManager(this));

        btnSaveWeight.setOnClickListener(v -> saveWeight());

        loadCurrentState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCurrentState();
    }

    private void loadCurrentState() {
        WeightRecord latest = DataManager.getLatestWeightRecord(this);
        if (latest != null) {
            tvLatestWeight.setText("Latest: " + String.format(Locale.US, "%.1f", latest.getWeightKg()) + " kg  (" + latest.getDate() + ")");
        } else {
            tvLatestWeight.setText("No weight records yet.");
        }

        // Show BMI estimate if height available
        double heightCm = DataManager.getProfileHeight(this);
        if (heightCm > 0 && latest != null) {
            double heightM = heightCm / 100.0;
            double bmi = latest.getWeightKg() / (heightM * heightM);
            tvCurrentBMI.setVisibility(View.VISIBLE);
            tvCurrentBMI.setText(String.format(Locale.US, "Estimated BMI: %.1f", bmi));
        } else {
            tvCurrentBMI.setVisibility(View.GONE);
        }

        List<WeightRecord> history = DataManager.getWeightHistory(this);
        WeightHistoryAdapter adapter = new WeightHistoryAdapter(this, history);
        rvWeightHistory.setAdapter(adapter);
    }

    private void saveWeight() {
        String weightStr = etWeight.getText() != null ? etWeight.getText().toString().trim() : "";

        if (weightStr.isEmpty()) {
            etWeight.setError("Please enter your weight.");
            etWeight.requestFocus();
            return;
        }

        double weight;
        try {
            weight = Double.parseDouble(weightStr);
            if (weight <= 0 || weight > 500) {
                etWeight.setError("Please enter a valid weight (0–500 kg).");
                etWeight.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etWeight.setError("Invalid weight value.");
            etWeight.requestFocus();
            return;
        }

        String dateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        String displayDate = new SimpleDateFormat("dd MMM yyyy", Locale.US).format(new Date());
        String time = new SimpleDateFormat("hh:mm a", Locale.US).format(new Date());

        WeightRecord record = new WeightRecord(displayDate, dateKey, time, weight);
        DataManager.saveWeightRecord(this, record);

        // Auto-save BMI record if profile height is set
        double heightCm = DataManager.getProfileHeight(this);
        if (heightCm > 0) {
            double heightM = heightCm / 100.0;
            double bmi = weight / (heightM * heightM);
            String bmiCategory;
            if (bmi < 18.5) {
                bmiCategory = "Underweight";
            } else if (bmi <= 24.9) {
                bmiCategory = "Normal Weight";
            } else if (bmi <= 29.9) {
                bmiCategory = "Overweight";
            } else {
                bmiCategory = "Obese";
            }
            String bmiDate = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(new Date());
            BMIRecord bmiRecord = new BMIRecord(bmiDate, heightCm, weight, bmi, bmiCategory);
            DataManager.saveBMIRecord(this, bmiRecord);
        }

        etWeight.setText("");
        Toast.makeText(this, String.format(Locale.US, "Weight %.1f kg saved!", weight), Toast.LENGTH_SHORT).show();
        loadCurrentState();
    }
}
