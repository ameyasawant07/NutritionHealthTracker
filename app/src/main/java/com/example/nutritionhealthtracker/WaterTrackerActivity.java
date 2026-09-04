package com.example.nutritionhealthtracker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionhealthtracker.utils.DataManager;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WaterTrackerActivity extends AppCompatActivity {

    private TextView tvDate, tvAmount, tvTarget, tvPercent;
    private ProgressBar pbWater;
    private MaterialButton btnAdd250, btnAdd500, btnAdd1000, btnReset, btnSetTarget;
    private Toolbar toolbar;

    private int dailyTargetMl = 2000;
    private int currentWaterMl = 0;
    private String todayDateKey;
    private String displayDate;

    private static final String[] PRESET_TARGETS = {
            "1000 ml", "1500 ml", "2000 ml", "2500 ml", "3000 ml", "3500 ml", "4000 ml", "Custom Target..."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_tracker);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Water Tracker");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        tvDate = findViewById(R.id.tvWaterDate);
        tvAmount = findViewById(R.id.tvWaterAmount);
        tvTarget = findViewById(R.id.tvWaterTarget);
        tvPercent = findViewById(R.id.tvWaterPercent);
        pbWater = findViewById(R.id.pbWater);

        btnSetTarget = findViewById(R.id.btnSetTarget);
        btnAdd250 = findViewById(R.id.btnAdd250);
        btnAdd500 = findViewById(R.id.btnAdd500);
        btnAdd1000 = findViewById(R.id.btnAdd1000);
        btnReset = findViewById(R.id.btnResetWater);

        todayDateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        displayDate = new SimpleDateFormat("dd MMMM yyyy", Locale.US).format(new Date());
        tvDate.setText("Today\'s Intake (" + displayDate + ")");

        // Load active user's saved target and today's water intake
        dailyTargetMl = DataManager.getUserWaterTarget(this);
        currentWaterMl = DataManager.getWaterIntake(this, todayDateKey);
        updateUI();

        btnSetTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetTargetDialog();
            }
        });

        btnAdd250.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(250);
            }
        });

        btnAdd500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(500);
            }
        });

        btnAdd1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(1000);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetWater();
            }
        });
    }

    private void showSetTargetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Choose Daily Water Target")
                .setItems(PRESET_TARGETS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == PRESET_TARGETS.length - 1) {
                            showCustomTargetDialog();
                        } else {
                            int target = 1000 + (which * 500);
                            saveNewTarget(target);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCustomTargetDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("e.g. 2800");

        new AlertDialog.Builder(this)
                .setTitle("Set Custom Daily Target (ml)")
                .setMessage("Enter your target water intake in ml (100 ml to 10,000 ml):")
                .setView(input)
                .setPositiveButton("SAVE TARGET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = input.getText().toString().trim();
                        if (text.isEmpty()) {
                            Toast.makeText(WaterTrackerActivity.this, "Please enter a valid water target.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            int target = Integer.parseInt(text);
                            if (target < 100 || target > 10000) {
                                Toast.makeText(WaterTrackerActivity.this, "Target must be between 100 ml and 10,000 ml.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            saveNewTarget(target);
                        } catch (NumberFormatException e) {
                            Toast.makeText(WaterTrackerActivity.this, "Invalid number format.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveNewTarget(int targetMl) {
        dailyTargetMl = targetMl;
        DataManager.saveUserWaterTarget(this, dailyTargetMl);
        DataManager.saveWaterIntake(this, todayDateKey, displayDate, currentWaterMl, dailyTargetMl);
        updateUI();
        Toast.makeText(this, "Daily water target updated to " + dailyTargetMl + " ml!", Toast.LENGTH_SHORT).show();
    }

    private void addWater(int amountMl) {
        currentWaterMl += amountMl;
        DataManager.saveWaterIntake(this, todayDateKey, displayDate, currentWaterMl, dailyTargetMl);
        DataManager.updateStreak(this); // Check streak after every water log
        updateUI();

        if (currentWaterMl >= dailyTargetMl && (currentWaterMl - amountMl) < dailyTargetMl) {
            Toast.makeText(this, "🎉 Congratulations! You reached your daily hydration target!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "+" + amountMl + " ml added!", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetWater() {
        currentWaterMl = 0;
        DataManager.resetWaterIntake(this, todayDateKey, displayDate, dailyTargetMl);
        updateUI();
        Toast.makeText(this, "Today's water intake reset to 0 ml.", Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        tvAmount.setText(currentWaterMl + " ml");
        tvTarget.setText("Daily Target: " + dailyTargetMl + " ml");

        pbWater.setMax(dailyTargetMl);
        pbWater.setProgress(Math.min(currentWaterMl, dailyTargetMl));

        int percent = (int) (((double) currentWaterMl / dailyTargetMl) * 100);
        tvPercent.setText(percent + "% of daily goal (" + currentWaterMl + " / " + dailyTargetMl + " ml)");
    }
}
