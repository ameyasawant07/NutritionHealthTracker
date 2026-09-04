package com.example.nutritionhealthtracker;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.adapters.FoodAdapter;
import com.example.nutritionhealthtracker.models.FoodItem;
import com.example.nutritionhealthtracker.utils.DietGenerator;
import com.example.nutritionhealthtracker.utils.FoodDatabase;

import java.util.List;

public class ProteinGuideActivity extends AppCompatActivity {

    private TextView tvNeed;
    private RecyclerView rvSources;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protein_guide);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Protein Sources Guide");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvNeed = findViewById(R.id.tvPersonalProteinNeed);
        rvSources = findViewById(R.id.rvProteinSources);
        rvSources.setLayoutManager(new LinearLayoutManager(this));

        DietGenerator.EnergyRequirements req = DietGenerator.calculateRequirements(this);
        tvNeed.setText("Your Estimated Goal Need: ~" + req.targetProteinGrams + "g / day");

        List<FoodItem> proteinFoods = FoodDatabase.getProteinSources();
        rvSources.setAdapter(new FoodAdapter(this, proteinFoods));
    }
}
