package com.example.nutritionhealthtracker.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.R;
import com.example.nutritionhealthtracker.models.DietPlanMeal;
import com.example.nutritionhealthtracker.models.FoodItem;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class DietMealAdapter extends RecyclerView.Adapter<DietMealAdapter.ViewHolder> {

    public interface OnReplaceMealClickListener {
        void onReplaceMealClick(DietPlanMeal meal, int position);
    }

    private final Context context;
    private final List<DietPlanMeal> meals;
    private final OnReplaceMealClickListener listener;

    public DietMealAdapter(Context context, List<DietPlanMeal> meals, OnReplaceMealClickListener listener) {
        this.context = context;
        this.meals = meals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_diet_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DietPlanMeal meal = meals.get(position);
        FoodItem food = meal.getFoodItem();

        holder.tvType.setText("⏰ " + meal.getMealType());
        holder.tvName.setText(food.getName());
        holder.tvPortion.setText("Recommended Portion: " + meal.getPortion());

        holder.tvCalories.setText(food.getCalories() + " kcal");
        holder.tvProtein.setText(String.format(Locale.US, "P: %.1fg", food.getProtein()));
        holder.tvCarbs.setText(String.format(Locale.US, "C: %.1fg", food.getCarbs()));
        holder.tvFat.setText(String.format(Locale.US, "F: %.1fg", food.getFat()));

        holder.tvWhyUseful.setText("💡 " + meal.getExplanation());

        String type = food.getDietaryType();
        holder.tvDietBadge.setText(type);
        if ("Veg".equalsIgnoreCase(type)) {
            holder.tvDietBadge.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else if ("Vegan".equalsIgnoreCase(type)) {
            holder.tvDietBadge.setBackgroundColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvDietBadge.setBackgroundColor(Color.parseColor("#E53935"));
        }

        holder.btnReplace.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReplaceMealClick(meal, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDietBadge, tvName, tvPortion, tvCalories, tvProtein, tvCarbs, tvFat, tvWhyUseful;
        MaterialButton btnReplace;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvMealType);
            tvDietBadge = itemView.findViewById(R.id.tvMealDietBadge);
            tvName = itemView.findViewById(R.id.tvMealFoodName);
            tvPortion = itemView.findViewById(R.id.tvMealPortion);
            tvCalories = itemView.findViewById(R.id.tvMealCalories);
            tvProtein = itemView.findViewById(R.id.tvMealProtein);
            tvCarbs = itemView.findViewById(R.id.tvMealCarbs);
            tvFat = itemView.findViewById(R.id.tvMealFat);
            tvWhyUseful = itemView.findViewById(R.id.tvMealWhyUseful);
            btnReplace = itemView.findViewById(R.id.btnReplaceMeal);
        }
    }
}
