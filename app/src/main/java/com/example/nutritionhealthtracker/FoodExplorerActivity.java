package com.example.nutritionhealthtracker;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.adapters.FoodAdapter;
import com.example.nutritionhealthtracker.models.FoodItem;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.example.nutritionhealthtracker.utils.FoodDatabase;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class FoodExplorerActivity extends AppCompatActivity {

    private TextInputEditText etSearch;
    private MaterialButtonToggleGroup toggleCategory;
    private TextView tvCountHeader;
    private RecyclerView rvFoods;
    private Toolbar toolbar;

    private String activeCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_explorer);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Food Explorer & Database");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        etSearch = findViewById(R.id.etSearchFood);
        toggleCategory = findViewById(R.id.toggleGroupCategory);
        tvCountHeader = findViewById(R.id.tvFoodCountHeader);
        rvFoods = findViewById(R.id.rvFoodExplorer);

        rvFoods.setLayoutManager(new LinearLayoutManager(this));

        toggleCategory.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnCatAll) activeCategory = "All";
                else if (checkedId == R.id.btnCatGrains) activeCategory = "Grains";
                else if (checkedId == R.id.btnCatPulses) activeCategory = "Pulses";
                else if (checkedId == R.id.btnCatDairy) activeCategory = "Dairy";
                else if (checkedId == R.id.btnCatEggs) activeCategory = "Eggs";
                else if (checkedId == R.id.btnCatMeat) activeCategory = "Meat";
                else if (checkedId == R.id.btnCatFruits) activeCategory = "Fruits";
                else if (checkedId == R.id.btnCatVeggies) activeCategory = "Vegetables";
                else if (checkedId == R.id.btnCatNuts) activeCategory = "Nuts";
                performSearch();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { performSearch(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        performSearch();
    }

    private void performSearch() {
        String query = etSearch.getText() != null ? etSearch.getText().toString() : "";
        String userDietType = DataManager.getDietaryType(this);

        List<FoodItem> results = FoodDatabase.searchFoods(query, activeCategory, userDietType);
        tvCountHeader.setText("Showing " + results.size() + " Food Items");
        rvFoods.setAdapter(new FoodAdapter(this, results));
    }
}
