package com.example.nutritionhealthtracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.adapters.DetectedFoodAdapter;
import com.example.nutritionhealthtracker.models.FoodItem;
import com.example.nutritionhealthtracker.models.FoodScanRecord;
import com.example.nutritionhealthtracker.models.NutritionRecord;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.example.nutritionhealthtracker.utils.FoodDatabase;
import com.example.nutritionhealthtracker.utils.FoodRecognitionEngine;
import com.example.nutritionhealthtracker.utils.FoodScanManager;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FoodScanResultActivity extends AppCompatActivity implements DetectedFoodAdapter.OnFoodChangeListener {

    public static FoodRecognitionEngine.AnalysisResult currentAnalysisResult;

    private ImageView ivResultPhoto;
    private TextView tvScanConfidenceText, tvTotalCalories, tvTotalProtein, tvTotalCarbs, tvTotalFat, tvTotalFiber, tvSmartInsightText;
    private Spinner spinnerMealSlot;
    private RecyclerView rvDetectedFoods;
    private MaterialButton btnAddMissingFood, btnConfirmSaveMeal, btnEditManual, btnScanAgain;
    private Toolbar toolbar;

    private DetectedFoodAdapter foodAdapter;
    private List<FoodRecognitionEngine.DetectedFood> detectedFoodsList;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_scan_result);

        View rootResult = findViewById(R.id.rootResult);
        if (rootResult != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootResult, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }

        toolbar = findViewById(R.id.toolbarResult);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        photoPath = getIntent().getStringExtra("photoPath");

        ivResultPhoto = findViewById(R.id.ivResultPhoto);
        tvScanConfidenceText = findViewById(R.id.tvScanConfidenceText);
        tvTotalCalories = findViewById(R.id.tvTotalCalories);
        tvTotalProtein = findViewById(R.id.tvTotalProtein);
        tvTotalCarbs = findViewById(R.id.tvTotalCarbs);
        tvTotalFat = findViewById(R.id.tvTotalFat);
        tvTotalFiber = findViewById(R.id.tvTotalFiber);
        tvSmartInsightText = findViewById(R.id.tvSmartInsightText);
        spinnerMealSlot = findViewById(R.id.spinnerMealSlot);
        rvDetectedFoods = findViewById(R.id.rvDetectedFoods);
        btnAddMissingFood = findViewById(R.id.btnAddMissingFood);
        btnConfirmSaveMeal = findViewById(R.id.btnConfirmSaveMeal);
        btnEditManual = findViewById(R.id.btnEditManual);
        btnScanAgain = findViewById(R.id.btnScanAgain);

        setupMealSlotSpinner();
        loadPhotoPreview();

        if (currentAnalysisResult != null && currentAnalysisResult.detectedFoods != null) {
            detectedFoodsList = new ArrayList<>(currentAnalysisResult.detectedFoods);
            tvScanConfidenceText.setText("✅ Food Detected (" + currentAnalysisResult.confidencePercent + "% confidence)");
            tvSmartInsightText.setText(currentAnalysisResult.smartInsight != null ? currentAnalysisResult.smartInsight : "");
        } else {
            detectedFoodsList = new ArrayList<>();
            tvScanConfidenceText.setText("Meal Review");
        }

        rvDetectedFoods.setLayoutManager(new LinearLayoutManager(this));
        foodAdapter = new DetectedFoodAdapter(detectedFoodsList, this);
        rvDetectedFoods.setAdapter(foodAdapter);

        updateNutritionTotals();

        btnAddMissingFood.setOnClickListener(v -> showAddFoodDialog());
        btnEditManual.setOnClickListener(v -> showAddFoodDialog());
        btnScanAgain.setOnClickListener(v -> finish());

        btnConfirmSaveMeal.setOnClickListener(v -> confirmAndSaveMeal());
    }

    private void setupMealSlotSpinner() {
        String[] slots = {"Lunch", "Breakfast", "Dinner", "Snacks"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, slots);
        spinnerMealSlot.setAdapter(adapter);

        // Pre-select based on current time
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 11) spinnerMealSlot.setSelection(1); // Breakfast
        else if (hour >= 11 && hour < 16) spinnerMealSlot.setSelection(0); // Lunch
        else if (hour >= 16 && hour < 19) spinnerMealSlot.setSelection(3); // Snacks
        else spinnerMealSlot.setSelection(2); // Dinner
    }

    private void loadPhotoPreview() {
        if (photoPath != null && !photoPath.isEmpty()) {
            File file = new File(photoPath);
            if (file.exists()) {
                ivResultPhoto.setImageURI(Uri.fromFile(file));
            }
        }
    }

    private void updateNutritionTotals() {
        int totalCal = 0;
        double totalP = 0, totalC = 0, totalF = 0, totalFib = 0;

        for (FoodRecognitionEngine.DetectedFood item : detectedFoodsList) {
            totalCal += item.getCalculatedCalories();
            totalP += item.getCalculatedProtein();
            totalC += item.getCalculatedCarbs();
            totalF += item.getCalculatedFat();
            totalFib += item.getCalculatedFiber();
        }

        tvTotalCalories.setText(totalCal + " kcal");
        tvTotalProtein.setText(String.format(Locale.US, "%.1fg", totalP));
        tvTotalCarbs.setText(String.format(Locale.US, "%.1fg", totalC));
        tvTotalFat.setText(String.format(Locale.US, "%.1fg", totalF));
        tvTotalFiber.setText(String.format(Locale.US, "%.1fg", totalFib));
    }

    @Override
    public void onFoodUpdated() {
        updateNutritionTotals();
    }

    @Override
    public void onEditFoodRequested(int position, FoodRecognitionEngine.DetectedFood item) {
        showEditFoodDialog(position, item);
    }

    private void showAddFoodDialog() {
        EditText input = new EditText(this);
        input.setHint("e.g. Paneer curry, Roti, Chicken, Apple...");
        new AlertDialog.Builder(this)
                .setTitle("➕ Add Food Item")
                .setMessage("Enter the name of the food item to add:")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        FoodItem dbMatch = FoodDatabase.findFoodByName(name);
                        FoodRecognitionEngine.DetectedFood newFood;
                        if (dbMatch != null) {
                            newFood = new FoodRecognitionEngine.DetectedFood(
                                    dbMatch.getName(), dbMatch.getCategory(), dbMatch.getServingSize(),
                                    1.0, "serving", dbMatch.getCalories(), dbMatch.getProtein(),
                                    dbMatch.getCarbs(), dbMatch.getFat(), dbMatch.getFiber(), dbMatch.getVitamins()
                            );
                        } else {
                            newFood = new FoodRecognitionEngine.DetectedFood(
                                    name, "Custom Food", "1 serving (100g)",
                                    1.0, "serving", 150, 5.0, 20.0, 4.0, 2.0, "Vitamins"
                            );
                        }
                        detectedFoodsList.add(newFood);
                        foodAdapter.notifyItemInserted(detectedFoodsList.size() - 1);
                        updateNutritionTotals();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditFoodDialog(int position, FoodRecognitionEngine.DetectedFood item) {
        EditText input = new EditText(this);
        input.setText(item.foodName);
        new AlertDialog.Builder(this)
                .setTitle("✏️ Edit Food Name")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        item.foodName = newName;
                        FoodItem dbMatch = FoodDatabase.findFoodByName(newName);
                        if (dbMatch != null) {
                            item.baseCalories = dbMatch.getCalories();
                            item.baseProtein = dbMatch.getProtein();
                            item.baseCarbs = dbMatch.getCarbs();
                            item.baseFat = dbMatch.getFat();
                            item.baseFiber = dbMatch.getFiber();
                        }
                        foodAdapter.notifyItemChanged(position);
                        updateNutritionTotals();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmAndSaveMeal() {
        if (detectedFoodsList.isEmpty()) {
            Toast.makeText(this, "Please add at least one food item.", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedSlot = spinnerMealSlot.getSelectedItem().toString();
        String todayKey = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        String displayDate = new SimpleDateFormat("dd MMM yyyy", Locale.US).format(new Date());
        String timeStr = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());

        int totalCal = 0;
        double totalP = 0, totalC = 0, totalF = 0, totalFib = 0;
        StringBuilder foodsSummary = new StringBuilder();

        for (int i = 0; i < detectedFoodsList.size(); i++) {
            FoodRecognitionEngine.DetectedFood f = detectedFoodsList.get(i);
            totalCal += f.getCalculatedCalories();
            totalP += f.getCalculatedProtein();
            totalC += f.getCalculatedCarbs();
            totalF += f.getCalculatedFat();
            totalFib += f.getCalculatedFiber();

            foodsSummary.append(f.foodName);
            if (i < detectedFoodsList.size() - 1) foodsSummary.append(", ");
        }

        // 1. Update DataManager Nutrition Record for Today
        NutritionRecord currentRecord = DataManager.getNutritionRecord(this, todayKey);
        String b = (currentRecord != null) ? currentRecord.getBreakfast() : "";
        String l = (currentRecord != null) ? currentRecord.getLunch() : "";
        String d = (currentRecord != null) ? currentRecord.getDinner() : "";
        String s = (currentRecord != null) ? currentRecord.getSnacks() : "";
        int existingCal = (currentRecord != null) ? currentRecord.getCalories() : 0;

        String mealDetails = foodsSummary + " (" + totalCal + " kcal)";
        if ("Breakfast".equalsIgnoreCase(selectedSlot)) {
            b = b.isEmpty() ? mealDetails : b + " | " + mealDetails;
        } else if ("Lunch".equalsIgnoreCase(selectedSlot)) {
            l = l.isEmpty() ? mealDetails : l + " | " + mealDetails;
        } else if ("Dinner".equalsIgnoreCase(selectedSlot)) {
            d = d.isEmpty() ? mealDetails : d + " | " + mealDetails;
        } else {
            s = s.isEmpty() ? mealDetails : s + " | " + mealDetails;
        }

        NutritionRecord updatedRecord = new NutritionRecord(displayDate, b, l, d, s, existingCal + totalCal);
        DataManager.saveNutritionRecord(this, updatedRecord, displayDate);
        DataManager.updateStreak(this);

        // 2. Save Scan Record in FoodScanManager History
        String scanId = "scan_" + System.currentTimeMillis();
        String photoUriStr = photoPath != null ? "file://" + photoPath : "";
        FoodScanRecord scanRecord = new FoodScanRecord(
                scanId, displayDate, todayKey, timeStr, photoUriStr,
                foodsSummary.toString(), totalCal, totalP, totalC, totalF, totalFib, selectedSlot
        );
        FoodScanManager.saveScanRecord(this, scanRecord);

        Toast.makeText(this, "✓ Meal logged successfully to " + selectedSlot + "!", Toast.LENGTH_LONG).show();

        // Launch AI Health Assistant so user can ask questions about logged meal
        Intent assistantIntent = new Intent(this, AIHealthAssistantActivity.class);
        startActivity(assistantIntent);
        finish();
    }
}
