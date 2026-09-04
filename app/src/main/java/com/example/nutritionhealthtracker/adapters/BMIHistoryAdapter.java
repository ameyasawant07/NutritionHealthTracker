package com.example.nutritionhealthtracker.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.R;
import com.example.nutritionhealthtracker.models.BMIRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BMIHistoryAdapter extends RecyclerView.Adapter<BMIHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<BMIRecord> records;

    public BMIHistoryAdapter(Context context, List<BMIRecord> records) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_bmi_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BMIRecord record = records.get(position);

        holder.tvDate.setText(formatDate(record.getDate()));
        holder.tvWeight.setText(String.format(Locale.US, "Weight: %.1f kg", record.getWeight()));
        holder.tvHeight.setText(String.format(Locale.US, "Height: %.1f cm", record.getHeight()));
        holder.tvBMI.setText(String.format(Locale.US, "BMI: %.1f", record.getBmi()));
        holder.tvCategory.setText(record.getCategory());

        // Dynamic category background color
        int categoryColor;
        switch (record.getCategory().toLowerCase()) {
            case "underweight":
                categoryColor = ContextCompat.getColor(context, R.color.bmi_underweight);
                break;
            case "normal weight":
                categoryColor = ContextCompat.getColor(context, R.color.bmi_normal);
                break;
            case "overweight":
                categoryColor = ContextCompat.getColor(context, R.color.bmi_overweight);
                break;
            case "obese":
            default:
                categoryColor = ContextCompat.getColor(context, R.color.bmi_obese);
                break;
        }

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(24f);
        shape.setColor(categoryColor);
        holder.tvCategory.setBackground(shape);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvWeight, tvHeight, tvBMI, tvCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvWeight = itemView.findViewById(R.id.tvHistoryWeight);
            tvHeight = itemView.findViewById(R.id.tvHistoryHeight);
            tvBMI = itemView.findViewById(R.id.tvHistoryBMI);
            tvCategory = itemView.findViewById(R.id.tvHistoryCategory);
        }
    }
}
