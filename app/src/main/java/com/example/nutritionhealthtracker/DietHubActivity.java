package com.example.nutritionhealthtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionhealthtracker.utils.DataManager;
import com.google.android.material.card.MaterialCardView;

public class DietHubActivity extends AppCompatActivity {

    private TextView tvSubhead;
    private MaterialCardView cardPlan, cardExplorer, cardProtein, cardCalorie, cardHealthy, cardSmartRecs, cardGoal, cardTips;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_hub);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Diet & Nutrition System");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvSubhead = findViewById(R.id.tvHubGoalSubtitle);
        cardPlan = findViewById(R.id.cardMyDietPlan);
        cardExplorer = findViewById(R.id.cardFoodExplorer);
        cardProtein = findViewById(R.id.cardProteinGuide);
        cardCalorie = findViewById(R.id.cardCalorieGuide);
        cardHealthy = findViewById(R.id.cardHealthyGuide);
        cardSmartRecs = findViewById(R.id.cardSmartRecs);
        cardGoal = findViewById(R.id.cardDietGoal);
        cardTips = findViewById(R.id.cardDietHubTips);

        setupListeners();
        updateSubhead();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSubhead();
    }

    private void updateSubhead() {
        String goal = DataManager.getUserGoal(this);
        String type = DataManager.getDietaryType(this);
        tvSubhead.setText("Goal: " + goal + " • " + type);
    }

    private void setupListeners() {
        cardPlan.setOnClickListener(v -> startActivity(new Intent(this, MyDietPlanActivity.class)));
        cardExplorer.setOnClickListener(v -> startActivity(new Intent(this, FoodExplorerActivity.class)));
        cardProtein.setOnClickListener(v -> startActivity(new Intent(this, ProteinGuideActivity.class)));
        cardCalorie.setOnClickListener(v -> startActivity(new Intent(this, CalorieGuideActivity.class)));
        cardHealthy.setOnClickListener(v -> startActivity(new Intent(this, HealthyFoodGuideActivity.class)));
        cardSmartRecs.setOnClickListener(v -> startActivity(new Intent(this, SmartRecommendationsActivity.class)));
        cardGoal.setOnClickListener(v -> startActivity(new Intent(this, DietGoalActivity.class)));
        if (cardTips != null) {
            cardTips.setOnClickListener(v -> startActivity(new Intent(this, HealthTipsActivity.class)));
        }
    }
}
