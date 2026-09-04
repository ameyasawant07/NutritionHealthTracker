package com.example.nutritionhealthtracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.R;
import com.example.nutritionhealthtracker.models.NutritionRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NutritionHistoryAdapter extends RecyclerView.Adapter<NutritionHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<NutritionRecord> records;

    public NutritionHistoryAdapter(Context context, List<NutritionRecord> records) {
        this.context = context;
        this.records = records;
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return "";
        String str = dateStr.trim();
        if (str.matches("\\d{4}-\\d{2}-\\d{2}")) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                Date date = inputFormat.parse(str);
                if (date != null) {
                    return outputFormat.format(date);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nutrition_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NutritionRecord record = records.get(position);

        holder.tvDate.setText(formatDate(record.getDate()));
        
        if (record.getCalories() > 0) {
            holder.tvCalories.setVisibility(View.VISIBLE);
            holder.tvCalories.setText(String.format(Locale.US, "🔥 %d kcal", record.getCalories()));
        } else {
            holder.tvCalories.setVisibility(View.GONE);
        }

        String breakfastText = record.getBreakfast() != null && !record.getBreakfast().trim().isEmpty() 
                ? record.getBreakfast().trim() : "None";
        String lunchText = record.getLunch() != null && !record.getLunch().trim().isEmpty() 
                ? record.getLunch().trim() : "None";
        String dinnerText = record.getDinner() != null && !record.getDinner().trim().isEmpty() 
                ? record.getDinner().trim() : "None";
        String snacksText = record.getSnacks() != null && !record.getSnacks().trim().isEmpty() 
                ? record.getSnacks().trim() : "None";

        holder.tvBreakfast.setText("🥣 Breakfast: " + breakfastText);
        holder.tvLunch.setText("🥗 Lunch: " + lunchText);
        holder.tvDinner.setText("🍲 Dinner: " + dinnerText);
        holder.tvSnacks.setText("🍎 Snacks: " + snacksText);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvCalories, tvBreakfast, tvLunch, tvDinner, tvSnacks;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvNutDate);
            tvCalories = itemView.findViewById(R.id.tvNutCalories);
            tvBreakfast = itemView.findViewById(R.id.tvBreakfast);
            tvLunch = itemView.findViewById(R.id.tvLunch);
            tvDinner = itemView.findViewById(R.id.tvDinner);
            tvSnacks = itemView.findViewById(R.id.tvSnacks);
        }
    }
}
