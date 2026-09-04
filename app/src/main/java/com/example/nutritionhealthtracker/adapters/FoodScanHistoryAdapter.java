package com.example.nutritionhealthtracker.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.R;
import com.example.nutritionhealthtracker.models.FoodScanRecord;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class FoodScanHistoryAdapter extends RecyclerView.Adapter<FoodScanHistoryAdapter.HistoryViewHolder> {

    public interface OnScanActionListener {
        void onScanClicked(FoodScanRecord record);
        void onDeleteScanClicked(FoodScanRecord record, int position);
    }

    private final List<FoodScanRecord> historyList;
    private final OnScanActionListener listener;

    public FoodScanHistoryAdapter(List<FoodScanRecord> historyList, OnScanActionListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_scan_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        FoodScanRecord record = historyList.get(position);
        holder.tvScanDateTime.setText(record.getDate() + " • " + record.getTime());
        holder.tvMealSlotBadge.setText(record.getMealSlot() != null ? record.getMealSlot() : "Meal");
        holder.tvScanFoods.setText(record.getDetectedFoodsSummary());
        holder.tvScanMacros.setText(String.format(Locale.US, "%d kcal | %.1fg Protein", record.getCalories(), record.getProtein()));

        // Thumbnail handling
        String photoUriStr = record.getPhotoUri();
        if (photoUriStr != null && !photoUriStr.isEmpty()) {
            try {
                if (photoUriStr.startsWith("file://")) {
                    File file = new File(photoUriStr.replace("file://", ""));
                    if (file.exists()) {
                        holder.ivScanThumbnail.setImageURI(Uri.fromFile(file));
                    } else {
                        holder.ivScanThumbnail.setImageResource(R.drawable.bg_circle_accent);
                    }
                } else {
                    holder.ivScanThumbnail.setImageURI(Uri.parse(photoUriStr));
                }
            } catch (Exception e) {
                holder.ivScanThumbnail.setImageResource(R.drawable.bg_circle_accent);
            }
        } else {
            holder.ivScanThumbnail.setImageResource(R.drawable.bg_circle_accent);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onScanClicked(record);
        });

        holder.btnDeleteScan.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onDeleteScanClicked(record, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivScanThumbnail;
        TextView tvScanDateTime, tvMealSlotBadge, tvScanFoods, tvScanMacros;
        ImageButton btnDeleteScan;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivScanThumbnail = itemView.findViewById(R.id.ivScanThumbnail);
            tvScanDateTime = itemView.findViewById(R.id.tvScanDateTime);
            tvMealSlotBadge = itemView.findViewById(R.id.tvMealSlotBadge);
            tvScanFoods = itemView.findViewById(R.id.tvScanFoods);
            tvScanMacros = itemView.findViewById(R.id.tvScanMacros);
            btnDeleteScan = itemView.findViewById(R.id.btnDeleteScan);
        }
    }
}
