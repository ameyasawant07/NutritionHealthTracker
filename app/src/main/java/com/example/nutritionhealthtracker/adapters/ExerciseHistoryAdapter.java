package com.example.nutritionhealthtracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.R;
import com.example.nutritionhealthtracker.models.ExerciseRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExerciseHistoryAdapter extends RecyclerView.Adapter<ExerciseHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<ExerciseRecord> records;

    public ExerciseHistoryAdapter(Context context, List<ExerciseRecord> records) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseRecord record = records.get(position);
        holder.tvType.setText("🏃 " + record.getExerciseType());
        holder.tvDate.setText(formatDate(record.getDate()) + " • " + record.getTime());
        holder.tvDuration.setText(record.getDurationMinutes() + " min");
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDate, tvDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvExType);
            tvDate = itemView.findViewById(R.id.tvExDate);
            tvDuration = itemView.findViewById(R.id.tvExDuration);
        }
    }
}
