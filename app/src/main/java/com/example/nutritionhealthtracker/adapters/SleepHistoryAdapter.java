package com.example.nutritionhealthtracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.R;
import com.example.nutritionhealthtracker.models.SleepRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SleepHistoryAdapter extends RecyclerView.Adapter<SleepHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<SleepRecord> records;

    public SleepHistoryAdapter(Context context, List<SleepRecord> records) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_sleep_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SleepRecord record = records.get(position);
        holder.tvDate.setText(formatDate(record.getDate()));
        holder.tvTimes.setText(record.getSleepTime() + " – " + record.getWakeTime());
        holder.tvDuration.setText(String.format(Locale.US, "%.1f hrs", record.getDurationHours()));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTimes, tvDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvSleepDate);
            tvTimes = itemView.findViewById(R.id.tvSleepTimes);
            tvDuration = itemView.findViewById(R.id.tvSleepDuration);
        }
    }
}
