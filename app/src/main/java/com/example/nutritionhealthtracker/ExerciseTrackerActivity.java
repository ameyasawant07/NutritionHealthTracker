package com.example.nutritionhealthtracker;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.adapters.ExerciseHistoryAdapter;
import com.example.nutritionhealthtracker.models.ExerciseRecord;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExerciseTrackerActivity extends AppCompatActivity {

    private Spinner spExerciseType;
    private TextInputEditText etDuration;
    private MaterialButton btnSave;
    private TextView tvTodayExercise;
    private RecyclerView rvHistory;
    private Toolbar toolbar;

    private static final String[] EXERCISE_TYPES = {
            "Walking", "Running", "Cycling", "Gym / Weight Training", "Yoga", "Swimming", "Aerobics", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_tracker);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Exercise Tracker");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        spExerciseType = findViewById(R.id.spExerciseType);
        etDuration = findViewById(R.id.etDuration);
        btnSave = findViewById(R.id.btnSaveExercise);
        tvTodayExercise = findViewById(R.id.tvTodayExercise);
        rvHistory = findViewById(R.id.rvExerciseHistory);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        setupSpinner();
        btnSave.setOnClickListener(v -> saveExercise());

        loadCurrentState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCurrentState();
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                EXERCISE_TYPES
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spExerciseType.setAdapter(adapter);
    }

    private void loadCurrentState() {
        String todayKey = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        int todayMins = DataManager.getExerciseMinutesForDate(this, todayKey);
        tvTodayExercise.setText("Today\'s Total: " + todayMins + " minutes");

        List<ExerciseRecord> history = DataManager.getExerciseHistory(this);
        ExerciseHistoryAdapter adapter = new ExerciseHistoryAdapter(this, history);
        rvHistory.setAdapter(adapter);
    }

    private void saveExercise() {
        String durationStr = etDuration.getText() != null ? etDuration.getText().toString().trim() : "";
        String exerciseType = spExerciseType.getSelectedItem() != null ? spExerciseType.getSelectedItem().toString() : "Walking";

        if (durationStr.isEmpty()) {
            etDuration.setError("Please enter exercise duration in minutes.");
            etDuration.requestFocus();
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0 || duration > 1440) {
                etDuration.setError("Please enter a valid duration (1–1440 mins).");
                etDuration.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etDuration.setError("Invalid duration.");
            etDuration.requestFocus();
            return;
        }

        String dateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        String displayDate = new SimpleDateFormat("dd MMM yyyy", Locale.US).format(new Date());
        String time = new SimpleDateFormat("hh:mm a", Locale.US).format(new Date());

        ExerciseRecord record = new ExerciseRecord(displayDate, dateKey, time, exerciseType, duration);
        DataManager.saveExerciseRecord(this, record);
        DataManager.updateStreak(this);

        etDuration.setText("");
        Toast.makeText(this, exerciseType + " (" + duration + " min) saved!", Toast.LENGTH_SHORT).show();
        loadCurrentState();
    }
}
