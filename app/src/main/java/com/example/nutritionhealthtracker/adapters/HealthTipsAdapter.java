package com.example.nutritionhealthtracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.R;
import com.example.nutritionhealthtracker.models.HealthTip;

import java.util.List;

public class HealthTipsAdapter extends RecyclerView.Adapter<HealthTipsAdapter.ViewHolder> {

    private final Context context;
    private final List<HealthTip> tipsList;

    public HealthTipsAdapter(Context context, List<HealthTip> tipsList) {
        this.context = context;
        this.tipsList = tipsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_health_tip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthTip tip = tipsList.get(position);
        holder.tvEmoji.setText(tip.getIconEmoji());
        holder.tvCategory.setText(tip.getCategory());
        holder.tvTitle.setText(tip.getTitle());
        holder.tvDescription.setText(tip.getDescription());
    }

    @Override
    public int getItemCount() {
        return tipsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvCategory, tvTitle, tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tvTipEmoji);
            tvCategory = itemView.findViewById(R.id.tvTipCategory);
            tvTitle = itemView.findViewById(R.id.tvTipTitle);
            tvDescription = itemView.findViewById(R.id.tvTipDescription);
        }
    }
}
