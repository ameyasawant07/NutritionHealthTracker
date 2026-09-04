package com.example.nutritionhealthtracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.R;
import com.example.nutritionhealthtracker.models.WaterRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WaterHistoryAdapter extends RecyclerView.Adapter<WaterHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<WaterRecord> records;

    public WaterHistoryAdapter(Context context, List<WaterRecord> records) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_water_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WaterRecord record = records.get(position);

        holder.tvDate.setText(formatDate(record.getDate()));
        holder.tvAmount.setText(String.format(Locale.US, "%d ml / %d ml", record.getConsumedMl(), record.getTargetMl()));

        holder.pbWater.setMax(record.getTargetMl());
        holder.pbWater.setProgress(Math.min(record.getConsumedMl(), record.getTargetMl()));

        int percent = record.getProgressPercentage();
        if (percent >= 100) {
            holder.tvPercent.setText(String.format(Locale.US, "🎉 %d%% of daily target achieved (Goal Met!)", percent));
        } else {
            holder.tvPercent.setText(String.format(Locale.US, "%d%% of daily target achieved", percent));
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvAmount, tvPercent;
        ProgressBar pbWater;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvWaterHistoryDate);
            tvAmount = itemView.findViewById(R.id.tvWaterHistoryAmount);
            tvPercent = itemView.findViewById(R.id.tvWaterHistoryPercent);
            pbWater = itemView.findViewById(R.id.pbWaterHistory);
        }
    }
}
