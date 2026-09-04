package com.example.nutritionhealthtracker;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.adapters.SleepHistoryAdapter;
import com.example.nutritionhealthtracker.models.SleepRecord;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SleepTrackerActivity extends AppCompatActivity {

    private MaterialButton btnSleepTime, btnWakeTime, btnSave;
    private TextView tvLatestSleep, tvCalculatedDuration;
    private RecyclerView rvHistory;
    private Toolbar toolbar;

    private int sleepHour = 23, sleepMinute = 0;
    private int wakeHour = 7, wakeMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracker);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Sleep Tracker");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        btnSleepTime = findViewById(R.id.btnSelectSleepTime);
        btnWakeTime = findViewById(R.id.btnSelectWakeTime);
        btnSave = findViewById(R.id.btnSaveSleep);
        tvLatestSleep = findViewById(R.id.tvLatestSleep);
        tvCalculatedDuration = findViewById(R.id.tvCalculatedDuration);
        rvHistory = findViewById(R.id.rvSleepHistory);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        btnSleepTime.setOnClickListener(v -> pickTime(true));
        btnWakeTime.setOnClickListener(v -> pickTime(false));
        btnSave.setOnClickListener(v -> saveSleep());

        updateTimeButtons();
        loadCurrentState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCurrentState();
    }

    private void pickTime(boolean isSleepTime) {
        int initialHour = isSleepTime ? sleepHour : wakeHour;
        int initialMin = isSleepTime ? sleepMinute : wakeMinute;

        TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            if (isSleepTime) {
                sleepHour = hourOfDay;
                sleepMinute = minute;
            } else {
                wakeHour = hourOfDay;
                wakeMinute = minute;
            }
            updateTimeButtons();
        }, initialHour, initialMin, false);
        dialog.show();
    }

    private void updateTimeButtons() {
        btnSleepTime.setText(formatTime(sleepHour, sleepMinute));
        btnWakeTime.setText(formatTime(wakeHour, wakeMinute));

        double duration = calculateDurationHours(sleepHour, sleepMinute, wakeHour, wakeMinute);
        tvCalculatedDuration.setText(String.format(Locale.US, "Calculated Duration: %.1f hours", duration));
    }

    private String formatTime(int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        return new SimpleDateFormat("hh:mm a", Locale.US).format(cal.getTime());
    }

    private double calculateDurationHours(int sHour, int sMin, int wHour, int wMin) {
        int startTotalMins = sHour * 60 + sMin;
        int endTotalMins = wHour * 60 + wMin;

        int diffMins = endTotalMins - startTotalMins;
        if (diffMins < 0) {
            diffMins += 24 * 60; // Crossed midnight
        }

        return diffMins / 60.0;
    }

    private void loadCurrentState() {
        SleepRecord latest = DataManager.getLatestSleepRecord(this);
        if (latest != null) {
            tvLatestSleep.setText(String.format(Locale.US, "Latest: %.1f hrs (%s)", latest.getDurationHours(), latest.getDate()));
        } else {
            tvLatestSleep.setText("Latest: No sleep records yet.");
        }

        List<SleepRecord> history = DataManager.getSleepHistory(this);
        SleepHistoryAdapter adapter = new SleepHistoryAdapter(this, history);
        rvHistory.setAdapter(adapter);
    }

    private void saveSleep() {
        double durationHours = calculateDurationHours(sleepHour, sleepMinute, wakeHour, wakeMinute);
        if (durationHours <= 0 || durationHours > 24) {
            Toast.makeText(this, "Please select valid sleep and wake times.", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        String displayDate = new SimpleDateFormat("dd MMM yyyy", Locale.US).format(new Date());
        String sleepTimeStr = formatTime(sleepHour, sleepMinute);
        String wakeTimeStr = formatTime(wakeHour, wakeMinute);

        SleepRecord record = new SleepRecord(displayDate, dateKey, sleepTimeStr, wakeTimeStr, durationHours);
        DataManager.saveSleepRecord(this, record);
        DataManager.updateStreak(this);

        Toast.makeText(this, String.format(Locale.US, "Sleep record (%.1f hrs) saved!", durationHours), Toast.LENGTH_SHORT).show();
        loadCurrentState();
    }
}
