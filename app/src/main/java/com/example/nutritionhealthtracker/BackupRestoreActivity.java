package com.example.nutritionhealthtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.nutritionhealthtracker.models.BMIRecord;
import com.example.nutritionhealthtracker.models.ExerciseRecord;
import com.example.nutritionhealthtracker.models.NutritionRecord;
import com.example.nutritionhealthtracker.models.SleepRecord;
import com.example.nutritionhealthtracker.models.WaterRecord;
import com.example.nutritionhealthtracker.models.WeightRecord;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BackupRestoreActivity extends AppCompatActivity {

    private MaterialButton btnBackup, btnRestore;
    private Toolbar toolbar;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    confirmAndRestore(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Backup & Restore");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        btnBackup = findViewById(R.id.btnCreateBackup);
        btnRestore = findViewById(R.id.btnRestoreBackup);
        MaterialButton btnOpenSync = findViewById(R.id.btnOpenSyncSettings);

        btnBackup.setOnClickListener(v -> createBackup());
        btnRestore.setOnClickListener(v -> filePickerLauncher.launch("*/*"));
        if (btnOpenSync != null) {
            btnOpenSync.setOnClickListener(v -> startActivity(new Intent(this, SyncSettingsActivity.class)));
        }
    }

    private void createBackup() {
        String username = DataManager.getCurrentUsername(this);
        String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        try {
            JSONObject backupObj = new JSONObject();
            backupObj.put("app", "NutritionHealthTracker");
            backupObj.put("version", 1);
            backupObj.put("username", username);
            backupObj.put("backupDate", todayStr);

            // Profile
            JSONObject profileObj = new JSONObject();
            profileObj.put("name", DataManager.getProfileName(this));
            profileObj.put("age", DataManager.getProfileAge(this));
            profileObj.put("gender", DataManager.getProfileGender(this));
            profileObj.put("height", DataManager.getProfileHeight(this));
            profileObj.put("weight", DataManager.getProfileWeight(this));
            backupObj.put("profile", profileObj);

            // Water Target
            backupObj.put("waterTarget", DataManager.getUserWaterTarget(this));

            // Weight History
            JSONArray weightArr = new JSONArray();
            for (WeightRecord r : DataManager.getWeightHistory(this)) {
                JSONObject o = new JSONObject();
                o.put("date", r.getDate()); o.put("dateKey", r.getDateKey());
                o.put("time", r.getTime()); o.put("weightKg", r.getWeightKg());
                weightArr.put(o);
            }
            backupObj.put("weightHistory", weightArr);

            // BMI History
            JSONArray bmiArr = new JSONArray();
            for (BMIRecord r : DataManager.getBMIHistory(this)) {
                JSONObject o = new JSONObject();
                o.put("date", r.getDate()); o.put("height", r.getHeight());
                o.put("weight", r.getWeight()); o.put("bmi", r.getBmi());
                o.put("category", r.getCategory());
                bmiArr.put(o);
            }
            backupObj.put("bmiHistory", bmiArr);

            // Water History
            JSONArray waterArr = new JSONArray();
            for (WaterRecord r : DataManager.getWaterHistory(this)) {
                JSONObject o = new JSONObject();
                o.put("date", r.getDate()); o.put("dateKey", r.getDateKey());
                o.put("consumedMl", r.getConsumedMl()); o.put("targetMl", r.getTargetMl());
                waterArr.put(o);
            }
            backupObj.put("waterHistory", waterArr);

            // Nutrition History
            JSONArray nutArr = new JSONArray();
            for (NutritionRecord r : DataManager.getNutritionHistory(this)) {
                JSONObject o = new JSONObject();
                o.put("date", r.getDate()); o.put("breakfast", r.getBreakfast());
                o.put("lunch", r.getLunch()); o.put("dinner", r.getDinner());
                o.put("snacks", r.getSnacks()); o.put("calories", r.getCalories());
                nutArr.put(o);
            }
            backupObj.put("nutritionHistory", nutArr);

            // Exercise History
            JSONArray exArr = new JSONArray();
            for (ExerciseRecord r : DataManager.getExerciseHistory(this)) {
                JSONObject o = new JSONObject();
                o.put("date", r.getDate()); o.put("dateKey", r.getDateKey());
                o.put("time", r.getTime()); o.put("exerciseType", r.getExerciseType());
                o.put("durationMinutes", r.getDurationMinutes());
                exArr.put(o);
            }
            backupObj.put("exerciseHistory", exArr);

            // Sleep History
            JSONArray sleepArr = new JSONArray();
            for (SleepRecord r : DataManager.getSleepHistory(this)) {
                JSONObject o = new JSONObject();
                o.put("date", r.getDate()); o.put("dateKey", r.getDateKey());
                o.put("sleepTime", r.getSleepTime()); o.put("wakeTime", r.getWakeTime());
                o.put("durationHours", r.getDurationHours());
                sleepArr.put(o);
            }
            backupObj.put("sleepHistory", sleepArr);

            // Write JSON to cache & share
            String fileName = "NutritionHealthTracker_Backup_" + username + "_" + todayStr + ".json";
            File file = new File(getCacheDir(), fileName);
            FileOutputStream out = new FileOutputStream(file);
            out.write(backupObj.toString(2).getBytes());
            out.close();

            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/json");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Health Data Backup - " + username);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Save / Share Health Backup JSON"));
            Toast.makeText(this, "Backup JSON file created successfully!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create backup.", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmAndRestore(Uri uri) {
        new AlertDialog.Builder(this)
                .setTitle("Restore Health Data")
                .setMessage("Restoring this backup may replace your current health tracking data. Continue?")
                .setPositiveButton("RESTORE", (dialog, which) -> restoreDataFromUri(uri))
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void restoreDataFromUri(Uri uri) {
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            JSONObject backupObj = new JSONObject(sb.toString());
            String backupUser = backupObj.optString("username", "");
            String currentUser = DataManager.getCurrentUsername(this);

            if (!backupUser.isEmpty() && !backupUser.equalsIgnoreCase(currentUser)) {
                Toast.makeText(this, "Warning: Backup belongs to user '" + backupUser + "', importing to active user '" + currentUser + "'", Toast.LENGTH_LONG).show();
            }

            // Restore Profile
            if (backupObj.has("profile")) {
                JSONObject p = backupObj.getJSONObject("profile");
                DataManager.saveProfile(this, p.optString("name"), p.optInt("age"), p.optString("gender"), p.optDouble("height"), p.optDouble("weight"));
            }

            // Restore Water Target
            if (backupObj.has("waterTarget")) {
                DataManager.saveUserWaterTarget(this, backupObj.optInt("waterTarget", 2000));
            }

            // Restore Weight History
            if (backupObj.has("weightHistory")) {
                JSONArray arr = backupObj.getJSONArray("weightHistory");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    WeightRecord r = new WeightRecord(o.optString("date"), o.optString("dateKey"), o.optString("time"), o.optDouble("weightKg"));
                    DataManager.saveWeightRecord(this, r);
                }
            }

            // Restore BMI History
            if (backupObj.has("bmiHistory")) {
                JSONArray arr = backupObj.getJSONArray("bmiHistory");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    BMIRecord r = new BMIRecord(o.optString("date"), o.optDouble("height"), o.optDouble("weight"), o.optDouble("bmi"), o.optString("category"));
                    DataManager.saveBMIRecord(this, r);
                }
            }

            // Restore Water History
            if (backupObj.has("waterHistory")) {
                JSONArray arr = backupObj.getJSONArray("waterHistory");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    DataManager.saveWaterIntake(this, o.optString("dateKey"), o.optString("date"), o.optInt("consumedMl"), o.optInt("targetMl", 2000));
                }
            }

            // Restore Nutrition History
            if (backupObj.has("nutritionHistory")) {
                JSONArray arr = backupObj.getJSONArray("nutritionHistory");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    NutritionRecord r = new NutritionRecord(o.optString("date"), o.optString("breakfast"), o.optString("lunch"), o.optString("dinner"), o.optString("snacks"), o.optInt("calories"));
                    DataManager.saveNutritionRecord(this, r, o.optString("date"));
                }
            }

            // Restore Exercise History
            if (backupObj.has("exerciseHistory")) {
                JSONArray arr = backupObj.getJSONArray("exerciseHistory");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    ExerciseRecord r = new ExerciseRecord(o.optString("date"), o.optString("dateKey"), o.optString("time"), o.optString("exerciseType"), o.optInt("durationMinutes"));
                    DataManager.saveExerciseRecord(this, r);
                }
            }

            // Restore Sleep History
            if (backupObj.has("sleepHistory")) {
                JSONArray arr = backupObj.getJSONArray("sleepHistory");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    SleepRecord r = new SleepRecord(o.optString("date"), o.optString("dateKey"), o.optString("sleepTime"), o.optString("wakeTime"), o.optDouble("durationHours"));
                    DataManager.saveSleepRecord(this, r);
                }
            }

            Toast.makeText(this, "Health data restored successfully!", Toast.LENGTH_SHORT).show();
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid backup JSON format.", Toast.LENGTH_SHORT).show();
        }
    }
}
