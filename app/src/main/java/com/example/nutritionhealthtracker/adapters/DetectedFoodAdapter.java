package com.example.nutritionhealthtracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.R;
import com.example.nutritionhealthtracker.utils.FoodRecognitionEngine;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class DetectedFoodAdapter extends RecyclerView.Adapter<DetectedFoodAdapter.FoodViewHolder> {

    public interface OnFoodChangeListener {
        void onFoodUpdated();
        void onEditFoodRequested(int position, FoodRecognitionEngine.DetectedFood item);
    }

    private final List<FoodRecognitionEngine.DetectedFood> foodList;
    private final OnFoodChangeListener listener;

    public DetectedFoodAdapter(List<FoodRecognitionEngine.DetectedFood> foodList, OnFoodChangeListener listener) {
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detected_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodRecognitionEngine.DetectedFood food = foodList.get(position);
        holder.tvFoodName.setText(food.foodName);
        holder.tvPortionEstimate.setText("Estimated: " + food.portionText);
        holder.tvQuantity.setText(String.format(Locale.US, "%.1f %s", food.quantity, food.servingUnit));

        int cal = food.getCalculatedCalories();
        double p = food.getCalculatedProtein();
        double c = food.getCalculatedCarbs();
        double f = food.getCalculatedFat();
        double fib = food.getCalculatedFiber();

        holder.tvFoodMacros.setText(String.format(Locale.US, "%d kcal | %.1fg P | %.1fg C | %.1fg F | %.1fg Fiber", cal, p, c, f, fib));

        // Category icon mapping
        String cat = food.category != null ? food.category.toLowerCase(Locale.US) : "";
        if (cat.contains("grain") || cat.contains("roti") || cat.contains("rice")) holder.tvFoodIcon.setText("🍚");
        else if (cat.contains("pulse") || cat.contains("dal") || cat.contains("legume")) holder.tvFoodIcon.setText("🍲");
        else if (cat.contains("dairy") || cat.contains("paneer") || cat.contains("curd")) holder.tvFoodIcon.setText("🧀");
        else if (cat.contains("egg") || cat.contains("meat") || cat.contains("fish")) holder.tvFoodIcon.setText("🍗");
        else if (cat.contains("veg") || cat.contains("salad")) holder.tvFoodIcon.setText("🥗");
        else if (cat.contains("fruit")) holder.tvFoodIcon.setText("🍎");
        else holder.tvFoodIcon.setText("🍛");

        holder.btnMinusQty.setOnClickListener(v -> {
            if (food.quantity > 0.5) {
                food.quantity -= 0.5;
                notifyItemChanged(holder.getAdapterPosition());
                if (listener != null) listener.onFoodUpdated();
            }
        });

        holder.btnPlusQty.setOnClickListener(v -> {
            food.quantity += 0.5;
            notifyItemChanged(holder.getAdapterPosition());
            if (listener != null) listener.onFoodUpdated();
        });

        holder.btnDeleteFood.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && pos < foodList.size()) {
                foodList.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, foodList.size());
                if (listener != null) listener.onFoodUpdated();
            }
        });

        holder.btnEditFood.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onEditFoodRequested(pos, food);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodIcon, tvFoodName, tvPortionEstimate, tvQuantity, tvFoodMacros, btnEditFood;
        MaterialButton btnMinusQty, btnPlusQty;
        ImageButton btnDeleteFood;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodIcon = itemView.findViewById(R.id.tvFoodIcon);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvPortionEstimate = itemView.findViewById(R.id.tvPortionEstimate);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvFoodMacros = itemView.findViewById(R.id.tvFoodMacros);
            btnEditFood = itemView.findViewById(R.id.btnEditFood);
            btnMinusQty = itemView.findViewById(R.id.btnMinusQty);
            btnPlusQty = itemView.findViewById(R.id.btnPlusQty);
            btnDeleteFood = itemView.findViewById(R.id.btnDeleteFood);
        }
    }
}
