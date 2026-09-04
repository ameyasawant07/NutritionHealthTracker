package com.example.nutritionhealthtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionhealthtracker.utils.AlarmScheduler;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.example.nutritionhealthtracker.utils.DietGenerator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etName, etAge, etHeight, etWeight;
    private Spinner spGender;
    private MaterialButton btnSave, btnLogout, btnNotifSettings;
    private MaterialCardView cardDietLink;
    private TextView tvCalorieHint;
    private Toolbar toolbar;
    private SwitchMaterial switchDarkMode;

    private static final String[] GENDER_OPTIONS = {"Select Gender", "Male", "Female", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("My Profile");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        etUsername = findViewById(R.id.etProfileUsername);
        etName = findViewById(R.id.etProfileName);
        etAge = findViewById(R.id.etProfileAge);
        etHeight = findViewById(R.id.etProfileHeight);
        etWeight = findViewById(R.id.etProfileWeight);
        spGender = findViewById(R.id.spProfileGender);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnLogout = findViewById(R.id.btnLogout);
        btnNotifSettings = findViewById(R.id.btnNotificationSettings);
        cardDietLink = findViewById(R.id.cardProfileDietLink);
        tvCalorieHint = findViewById(R.id.tvProfileCalorieHint);
        switchDarkMode = findViewById(R.id.switchDarkMode);

        setupGenderSpinner();
        loadSavedProfile();

        // Dark Mode Toggle
        if (switchDarkMode != null) {
            switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                DataManager.setDarkModeEnabled(ProfileActivity.this, isChecked);
                DataManager.applyTheme(ProfileActivity.this);
                recreate();
            });
        }

        if (cardDietLink != null) {
            cardDietLink.setOnClickListener(v -> startActivity(new Intent(this, CalorieGuideActivity.class)));
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
            }
        });

        btnNotifSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, NotificationSettingsActivity.class));
            }
        });

        MaterialButton btnSyncSettings = findViewById(R.id.btnSyncSettings);
        if (btnSyncSettings != null) {
            btnSyncSettings.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SyncSettingsActivity.class)));
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLogout();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCalorieHint();
    }

    private void updateCalorieHint() {
        if (tvCalorieHint != null) {
            DietGenerator.EnergyRequirements req = DietGenerator.calculateRequirements(this);
            tvCalorieHint.setText("Your current target: " + req.targetCalories + " kcal/day (" + DataManager.getUserGoal(this) + ") →");
        }
    }

    private void setupGenderSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                GENDER_OPTIONS
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(adapter);
    }

    private void loadSavedProfile() {
        String username = DataManager.getCurrentUsername(this);
        String name = DataManager.getProfileName(this);
        int age = DataManager.getProfileAge(this);
        String gender = DataManager.getProfileGender(this);
        double height = DataManager.getProfileHeight(this);
        double weight = DataManager.getProfileWeight(this);

        if (etUsername != null) {
            etUsername.setText(username);
        }
        if (name != null && !name.isEmpty()) {
            etName.setText(name);
        }
        if (age > 0) {
            etAge.setText(String.valueOf(age));
        }
        if (height > 0) {
            etHeight.setText(String.format(Locale.US, "%.1f", height));
        }
        if (weight > 0) {
            etWeight.setText(String.format(Locale.US, "%.1f", weight));
        }

        for (int i = 0; i < GENDER_OPTIONS.length; i++) {
            if (GENDER_OPTIONS[i].equalsIgnoreCase(gender)) {
                spGender.setSelection(i);
                break;
            }
        }

        // Load dark mode state
        if (switchDarkMode != null) {
            switchDarkMode.setChecked(DataManager.isDarkModeEnabled(this));
        }
    }

    private void saveProfileData() {
        String nameStr = etName.getText() != null ? etName.getText().toString().trim() : "";
        String ageStr = etAge.getText() != null ? etAge.getText().toString().trim() : "";
        String heightStr = etHeight.getText() != null ? etHeight.getText().toString().trim() : "";
        String weightStr = etWeight.getText() != null ? etWeight.getText().toString().trim() : "";
        String genderStr = spGender.getSelectedItem() != null ? spGender.getSelectedItem().toString() : "Select Gender";

        if (nameStr.isEmpty()) {
            etName.setError("Please enter your name.");
            etName.requestFocus();
            return;
        }

        if (ageStr.isEmpty()) {
            etAge.setError("Please enter your age.");
            etAge.requestFocus();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 120) {
                etAge.setError("Please enter a valid age between 1 and 120.");
                etAge.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etAge.setError("Invalid age value.");
            etAge.requestFocus();
            return;
        }

        if (genderStr.equals("Select Gender")) {
            Toast.makeText(this, "Please select your gender.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (heightStr.isEmpty()) {
            etHeight.setError("Please enter your height.");
            etHeight.requestFocus();
            return;
        }

        double height;
        try {
            height = Double.parseDouble(heightStr);
            if (height <= 0) {
                etHeight.setError("Height must be greater than zero.");
                etHeight.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etHeight.setError("Invalid height value.");
            etHeight.requestFocus();
            return;
        }

        if (weightStr.isEmpty()) {
            etWeight.setError("Please enter your weight.");
            etWeight.requestFocus();
            return;
        }

        double weight;
        try {
            weight = Double.parseDouble(weightStr);
            if (weight <= 0) {
                etWeight.setError("Weight must be greater than zero.");
                etWeight.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etWeight.setError("Invalid weight value.");
            etWeight.requestFocus();
            return;
        }

        DataManager.saveProfile(this, nameStr, age, genderStr, height, weight);
        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out of your account?")
                .setPositiveButton("LOG OUT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel all scheduled alarms for this user before logging out
                        AlarmScheduler.cancelUserAlarms(ProfileActivity.this);

                        DataManager.logoutUser(ProfileActivity.this);
                        Toast.makeText(ProfileActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }
}
