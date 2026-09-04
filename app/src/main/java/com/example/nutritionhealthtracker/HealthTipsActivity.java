package com.example.nutritionhealthtracker;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.adapters.HealthTipsAdapter;
import com.example.nutritionhealthtracker.models.HealthTip;

import java.util.ArrayList;
import java.util.List;

public class HealthTipsActivity extends AppCompatActivity {

    private RecyclerView rvHealthTips;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tips);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Health Tips & Guidance");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        rvHealthTips = findViewById(R.id.rvHealthTips);
        rvHealthTips.setLayoutManager(new LinearLayoutManager(this));

        List<HealthTip> tipsList = generateHealthTips();
        HealthTipsAdapter adapter = new HealthTipsAdapter(this, tipsList);
        rvHealthTips.setAdapter(adapter);
    }

    private List<HealthTip> generateHealthTips() {
        List<HealthTip> list = new ArrayList<>();

        list.add(new HealthTip(
                "Drink Enough Water",
                "Drink at least 8 to 10 glasses (2 liters) of clean water daily to stay hydrated, boost digestion, and maintain kidney health.",
                "Hydration",
                "💧"
        ));

        list.add(new HealthTip(
                "Include Fruits & Vegetables",
                "Fill half your plate with colorful fruits and leafy greens to provide essential vitamins, minerals, and dietary fiber.",
                "Nutrition",
                "🥗"
        ));

        list.add(new HealthTip(
                "Eat a Balanced Diet",
                "Combine complex carbohydrates, lean proteins, healthy fats, and micronutrients in every meal to keep your energy stable.",
                "Dietary Balance",
                "🍱"
        ));

        list.add(new HealthTip(
                "Avoid Excessive Processed Food",
                "Reduce intake of ultra-processed snacks, sugary soft drinks, and refined trans fats to prevent long-term metabolic risks.",
                "Healthy Choices",
                "🚫"
        ));

        list.add(new HealthTip(
                "Maintain Regular Physical Activity",
                "Engage in at least 30 minutes of moderate exercise like brisk walking, cycling, or swimming 5 days a week.",
                "Fitness",
                "🏃‍♂️"
        ));

        list.add(new HealthTip(
                "Get Adequate Restful Sleep",
                "Aim for 7–9 hours of continuous sleep every night to facilitate muscle repair, hormonal balance, and mental clarity.",
                "Sleep & Recovery",
                "😴"
        ));

        list.add(new HealthTip(
                "Avoid Skipping Important Meals",
                "Eating breakfast and timely meals prevents sudden blood sugar spikes and reduces unhealthy late-night overeating.",
                "Meal Planning",
                "🍳"
        ));

        list.add(new HealthTip(
                "Monitor Your Health Regularly",
                "Keep track of key body vital signs like BMI, daily water intake, and nutrition to stay proactive about wellness.",
                "Self Monitoring",
                "📊"
        ));

        return list;
    }
}
