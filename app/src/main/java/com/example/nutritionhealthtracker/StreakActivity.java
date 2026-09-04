package com.example.nutritionhealthtracker;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionhealthtracker.utils.DataManager;
import com.google.android.material.card.MaterialCardView;

public class StreakActivity extends AppCompatActivity {

    private TextView tvCurrentStreak, tvLongestStreak;
    private TextView tvBadge3, tvBadge7, tvBadge14, tvBadge30;
    private MaterialCardView cardBadge3, cardBadge7, cardBadge14, cardBadge30;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streak);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Streaks & Achievements");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvCurrentStreak = findViewById(R.id.tvCurrentStreakCount);
        tvLongestStreak = findViewById(R.id.tvLongestStreakCount);

        tvBadge3 = findViewById(R.id.tvBadge3Status);
        tvBadge7 = findViewById(R.id.tvBadge7Status);
        tvBadge14 = findViewById(R.id.tvBadge14Status);
        tvBadge30 = findViewById(R.id.tvBadge30Status);

        cardBadge3 = findViewById(R.id.cardBadge3);
        cardBadge7 = findViewById(R.id.cardBadge7);
        cardBadge14 = findViewById(R.id.cardBadge14);
        cardBadge30 = findViewById(R.id.cardBadge30);

        loadStreakData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStreakData();
    }

    private void loadStreakData() {
        DataManager.updateStreak(this);

        int current = DataManager.getCurrentStreak(this);
        int longest = DataManager.getLongestStreak(this);

        tvCurrentStreak.setText(current + " Days");
        tvLongestStreak.setText("🏆 Personal Best: " + longest + " Days");

        updateBadge(cardBadge3, tvBadge3, longest >= 3, "3 Days Reached!");
        updateBadge(cardBadge7, tvBadge7, longest >= 7, "7 Days Reached!");
        updateBadge(cardBadge14, tvBadge14, longest >= 14, "14 Days Reached!");
        updateBadge(cardBadge30, tvBadge30, longest >= 30, "30 Days Reached!");
    }

    private void updateBadge(MaterialCardView card, TextView text, boolean unlocked, String unlockedMsg) {
        if (unlocked) {
            card.setStrokeColor(Color.parseColor("#4CAF50"));
            card.setStrokeWidth(3);
            text.setText("✓ " + unlockedMsg);
            text.setTextColor(Color.parseColor("#388E3C"));
        } else {
            card.setStrokeColor(Color.parseColor("#E0E0E0"));
            card.setStrokeWidth(1);
            text.setText("Locked");
            text.setTextColor(Color.parseColor("#757575"));
        }
    }
}
