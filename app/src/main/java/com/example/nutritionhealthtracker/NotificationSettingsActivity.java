package com.example.nutritionhealthtracker;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionhealthtracker.utils.AlarmScheduler;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.example.nutritionhealthtracker.utils.NotificationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class NotificationSettingsActivity extends AppCompatActivity {

    private SwitchMaterial switchMaster, switchWater, switchBreakfast, switchLunch, switchDinner;
    private SwitchMaterial switchNutrition, switchWeight, switchExercise, switchSleep, switchSummary;
    private LinearLayout llDetailedSettings;
    private MaterialButton btnTestNotif;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Notification Settings");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        switchMaster = findViewById(R.id.switchMasterNotif);
        switchWater = findViewById(R.id.switchWaterNotif);
        switchBreakfast = findViewById(R.id.switchBreakfastNotif);
        switchLunch = findViewById(R.id.switchLunchNotif);
        switchDinner = findViewById(R.id.switchDinnerNotif);

        switchNutrition = findViewById(R.id.switchNutritionNotif);
        switchWeight = findViewById(R.id.switchWeightNotif);
        switchExercise = findViewById(R.id.switchExerciseNotif);
        switchSleep = findViewById(R.id.switchSleepNotif);
        switchSummary = findViewById(R.id.switchSummaryNotif);

        llDetailedSettings = findViewById(R.id.llDetailedSettings);
        btnTestNotif = findViewById(R.id.btnSendTestNotif);

        loadCurrentPreferences();
        setupListeners();
    }

    private void loadCurrentPreferences() {
        boolean master = DataManager.isMasterNotificationsEnabled(this);
        switchMaster.setChecked(master);
        llDetailedSettings.setVisibility(master ? View.VISIBLE : View.GONE);

        switchWater.setChecked(DataManager.isWaterRemindersEnabled(this));
        switchBreakfast.setChecked(DataManager.isBreakfastReminderEnabled(this));
        switchLunch.setChecked(DataManager.isLunchReminderEnabled(this));
        switchDinner.setChecked(DataManager.isDinnerReminderEnabled(this));

        switchNutrition.setChecked(DataManager.isNutritionReminderEnabled(this));
        switchWeight.setChecked(DataManager.isWeightReminderEnabled(this));
        switchExercise.setChecked(DataManager.isExerciseReminderEnabled(this));
        switchSleep.setChecked(DataManager.isSleepReminderEnabled(this));
        switchSummary.setChecked(DataManager.isDailySummaryEnabled(this));
    }

    private void setupListeners() {
        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            int id = buttonView.getId();
            if (id == R.id.switchMasterNotif) {
                DataManager.setMasterNotificationsEnabled(this, isChecked);
                llDetailedSettings.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            } else if (id == R.id.switchWaterNotif) DataManager.setWaterRemindersEnabled(this, isChecked);
            else if (id == R.id.switchBreakfastNotif) DataManager.setBreakfastReminderEnabled(this, isChecked);
            else if (id == R.id.switchLunchNotif) DataManager.setLunchReminderEnabled(this, isChecked);
            else if (id == R.id.switchDinnerNotif) DataManager.setDinnerReminderEnabled(this, isChecked);
            else if (id == R.id.switchNutritionNotif) DataManager.setNutritionReminderEnabled(this, isChecked);
            else if (id == R.id.switchWeightNotif) DataManager.setWeightReminderEnabled(this, isChecked);
            else if (id == R.id.switchExerciseNotif) DataManager.setExerciseReminderEnabled(this, isChecked);
            else if (id == R.id.switchSleepNotif) DataManager.setSleepReminderEnabled(this, isChecked);
            else if (id == R.id.switchSummaryNotif) DataManager.setDailySummaryEnabled(this, isChecked);

            AlarmScheduler.scheduleUserAlarms(this);
        };

        switchMaster.setOnCheckedChangeListener(listener);
        switchWater.setOnCheckedChangeListener(listener);
        switchBreakfast.setOnCheckedChangeListener(listener);
        switchLunch.setOnCheckedChangeListener(listener);
        switchDinner.setOnCheckedChangeListener(listener);
        switchNutrition.setOnCheckedChangeListener(listener);
        switchWeight.setOnCheckedChangeListener(listener);
        switchExercise.setOnCheckedChangeListener(listener);
        switchSleep.setOnCheckedChangeListener(listener);
        switchSummary.setOnCheckedChangeListener(listener);

        btnTestNotif.setOnClickListener(v -> {
            NotificationHelper.showTestNotification(this);
            Toast.makeText(this, "Test notification sent! Check your notification panel.", Toast.LENGTH_LONG).show();
        });
    }
}
