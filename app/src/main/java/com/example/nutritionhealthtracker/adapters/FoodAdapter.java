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
import com.example.nutritionhealthtracker.models.FoodItem;

import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private final Context context;
    private final List<FoodItem> foods;

    public FoodAdapter(Context context, List<FoodItem> foods) {
        this.context = context;
        this.foods = foods;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = foods.get(position);

        holder.tvName.setText(item.getName());
        holder.tvServing.setText(item.getCategory() + " • " + item.getServingSize());

        holder.tvCalories.setText(item.getCalories() + " kcal");
        holder.tvProtein.setText(String.format(Locale.US, "P: %.1fg", item.getProtein()));
        holder.tvCarbs.setText(String.format(Locale.US, "C: %.1fg", item.getCarbs()));
        holder.tvFat.setText(String.format(Locale.US, "F: %.1fg", item.getFat()));

        holder.tvWhyUseful.setText("💡 " + item.getWhyUseful());

        String type = item.getDietaryType();
        holder.tvDietaryBadge.setText(type);
        if ("Veg".equalsIgnoreCase(type)) {
            holder.tvDietaryBadge.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else if ("Vegan".equalsIgnoreCase(type)) {
            holder.tvDietaryBadge.setBackgroundColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvDietaryBadge.setBackgroundColor(Color.parseColor("#E53935"));
        }
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvServing, tvDietaryBadge, tvCalories, tvProtein, tvCarbs, tvFat, tvWhyUseful;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvServing = itemView.findViewById(R.id.tvFoodServing);
            tvDietaryBadge = itemView.findViewById(R.id.tvDietaryBadge);
            tvCalories = itemView.findViewById(R.id.tvFoodCalories);
            tvProtein = itemView.findViewById(R.id.tvFoodProtein);
            tvCarbs = itemView.findViewById(R.id.tvFoodCarbs);
            tvFat = itemView.findViewById(R.id.tvFoodFat);
            tvWhyUseful = itemView.findViewById(R.id.tvFoodWhyUseful);
        }
    }
}
