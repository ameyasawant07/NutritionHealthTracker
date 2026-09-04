package com.example.nutritionhealthtracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.R;
import com.example.nutritionhealthtracker.models.SavedReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SavedReportsAdapter extends RecyclerView.Adapter<SavedReportsAdapter.ReportViewHolder> {

    public interface OnReportClickListener {
        void onReportClick(SavedReport report);
        void onReportDelete(SavedReport report);
    }

    private List<SavedReport> reportList = new ArrayList<>();
    private final OnReportClickListener listener;

    public SavedReportsAdapter(OnReportClickListener listener) {
        this.listener = listener;
    }

    public void setReports(List<SavedReport> reports) {
        this.reportList = reports != null ? reports : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        SavedReport report = reportList.get(position);
        holder.bind(report, listener);
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTypeTag;
        private final TextView tvSavedDate;
        private final TextView tvTitle;
        private final TextView tvDateRange;
        private final TextView tvWeightNet;
        private final TextView tvWaterAvg;
        private final TextView tvCaloriesAvg;
        private final TextView tvSleepAvg;
        private final ImageButton btnDelete;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTypeTag = itemView.findViewById(R.id.tvReportTypeTag);
            tvSavedDate = itemView.findViewById(R.id.tvReportSavedDate);
            tvTitle = itemView.findViewById(R.id.tvSavedReportTitle);
            tvDateRange = itemView.findViewById(R.id.tvSavedReportDateRange);
            tvWeightNet = itemView.findViewById(R.id.tvSavedWeightNet);
            tvWaterAvg = itemView.findViewById(R.id.tvSavedWaterAvg);
            tvCaloriesAvg = itemView.findViewById(R.id.tvSavedCaloriesAvg);
            tvSleepAvg = itemView.findViewById(R.id.tvSavedSleepAvg);
            btnDelete = itemView.findViewById(R.id.btnDeleteReport);
        }

        public void bind(SavedReport report, OnReportClickListener listener) {
            String type = report.getReportType() != null ? report.getReportType() : "WEEKLY";
            tvTypeTag.setText(type.toUpperCase(Locale.US));

            tvSavedDate.setText("Saved: " + (report.getCreatedAt() != null ? report.getCreatedAt() : ""));
            tvTitle.setText(report.getTitle() != null ? report.getTitle() : "Health Report");
            tvDateRange.setText("Period: " + (report.getDateRange() != null ? report.getDateRange() : ""));

            double weightDiff = report.getWeightChange();
            String prefix = weightDiff > 0 ? "+" : "";
            tvWeightNet.setText(String.format(Locale.US, "%s%.1f kg", prefix, weightDiff));

            tvWaterAvg.setText(report.getAvgWaterMl() + " ml");
            tvCaloriesAvg.setText(report.getAvgCaloriesKcal() + " kcal");
            tvSleepAvg.setText(String.format(Locale.US, "%.1f hrs", report.getAvgSleepHours()));

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onReportClick(report);
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onReportDelete(report);
            });
        }
    }
}
