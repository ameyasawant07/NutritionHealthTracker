package com.example.nutritionhealthtracker;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.adapters.DietMealAdapter;
import com.example.nutritionhealthtracker.models.DietPlanMeal;
import com.example.nutritionhealthtracker.models.FoodItem;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.example.nutritionhealthtracker.utils.DietGenerator;
import com.example.nutritionhealthtracker.utils.FoodDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyDietPlanActivity extends AppCompatActivity {

    private TextView tvGoalTitle, tvDietTypeBadge, tvTotalCal, tvTotalProtein, tvTotalCarbs, tvTotalFat;
    private RecyclerView rvMeals;
    private Toolbar toolbar;

    private List<DietPlanMeal> activeMeals = new ArrayList<>();
    private DietMealAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_diet_plan);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("My Personalized Diet Plan");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvGoalTitle = findViewById(R.id.tvPlanGoalTitle);
        tvDietTypeBadge = findViewById(R.id.tvPlanDietType);
        tvTotalCal = findViewById(R.id.tvPlanTotalCal);
        tvTotalProtein = findViewById(R.id.tvPlanTotalProtein);
        tvTotalCarbs = findViewById(R.id.tvPlanTotalCarbs);
        tvTotalFat = findViewById(R.id.tvPlanTotalFat);

        rvMeals = findViewById(R.id.rvDietMeals);
        rvMeals.setLayoutManager(new LinearLayoutManager(this));

        loadDietPlan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDietPlan();
    }

    private void loadDietPlan() {
        String goal = DataManager.getUserGoal(this);
        String type = DataManager.getDietaryType(this);

        tvGoalTitle.setText("🥗 " + goal + " Plan");
        tvDietTypeBadge.setText(type);

        activeMeals = DietGenerator.generateDailyPlan(this);

        adapter = new DietMealAdapter(this, activeMeals, (meal, position) -> showReplaceMealDialog(meal, position));
        rvMeals.setAdapter(adapter);

        recalculatePlanTotals();
    }

    private void recalculatePlanTotals() {
        int totalCal = 0;
        double totalP = 0, totalC = 0, totalF = 0;

        for (DietPlanMeal m : activeMeals) {
            FoodItem f = m.getFoodItem();
            totalCal += f.getCalories();
            totalP += f.getProtein();
            totalC += f.getCarbs();
            totalF += f.getFat();
        }

        tvTotalCal.setText(totalCal + " kcal");
        tvTotalProtein.setText(String.format(Locale.US, "P: %.1fg", totalP));
        tvTotalCarbs.setText(String.format(Locale.US, "C: %.1fg", totalC));
        tvTotalFat.setText(String.format(Locale.US, "F: %.1fg", totalF));
    }

    private void showReplaceMealDialog(DietPlanMeal meal, int position) {
        String userDiet = DataManager.getDietaryType(this);
        List<FoodItem> substitutes = FoodDatabase.getSubstitutesFor(meal.getFoodItem(), userDiet);

        if (substitutes.isEmpty()) {
            Toast.makeText(this, "No suitable substitutes found for " + meal.getFoodItem().getName(), Toast.LENGTH_SHORT).show();
            return;
        }

        String[] itemNames = new String[substitutes.size()];
        for (int i = 0; i < substitutes.size(); i++) {
            FoodItem f = substitutes.get(i);
            itemNames[i] = f.getName() + " (" + f.getCalories() + " kcal, " + f.getProtein() + "g P)";
        }

        new AlertDialog.Builder(this)
                .setTitle("Replace " + meal.getMealType() + " Meal")
                .setItems(itemNames, (dialog, which) -> {
                    FoodItem selectedSubstitute = substitutes.get(which);
                    meal.setFoodItem(selectedSubstitute);
                    meal.setPortion(selectedSubstitute.getServingSize());
                    meal.setExplanation(selectedSubstitute.getWhyUseful());

                    adapter.notifyItemChanged(position);
                    recalculatePlanTotals();
                    Toast.makeText(this, "Meal replaced with " + selectedSubstitute.getName(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
