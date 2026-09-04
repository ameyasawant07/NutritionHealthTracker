package com.example.nutritionhealthtracker;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.adapters.FoodScanHistoryAdapter;
import com.example.nutritionhealthtracker.models.FoodScanRecord;
import com.example.nutritionhealthtracker.utils.FoodScanManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FoodScanHistoryActivity extends AppCompatActivity implements FoodScanHistoryAdapter.OnScanActionListener {

    private RecyclerView rvScanHistory;
    private TextView tvEmptyScanHistory;
    private MaterialButton btnClearAllHistory;
    private Toolbar toolbar;

    private FoodScanHistoryAdapter adapter;
    private final List<FoodScanRecord> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_scan_history);

        View rootHistory = findViewById(R.id.rootHistory);
        if (rootHistory != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootHistory, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }

        toolbar = findViewById(R.id.toolbarHistory);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        rvScanHistory = findViewById(R.id.rvScanHistory);
        tvEmptyScanHistory = findViewById(R.id.tvEmptyScanHistory);
        btnClearAllHistory = findViewById(R.id.btnClearAllHistory);

        rvScanHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoodScanHistoryAdapter(historyList, this);
        rvScanHistory.setAdapter(adapter);

        btnClearAllHistory.setOnClickListener(v -> confirmClearAll());

        loadHistory();
    }

    private void loadHistory() {
        historyList.clear();
        List<FoodScanRecord> fetched = FoodScanManager.getScanHistory(this);
        historyList.addAll(fetched);
        adapter.notifyDataSetChanged();

        if (historyList.isEmpty()) {
            tvEmptyScanHistory.setVisibility(View.VISIBLE);
            rvScanHistory.setVisibility(View.GONE);
            btnClearAllHistory.setVisibility(View.GONE);
        } else {
            tvEmptyScanHistory.setVisibility(View.GONE);
            rvScanHistory.setVisibility(View.VISIBLE);
            btnClearAllHistory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onScanClicked(FoodScanRecord record) {
        String detailMsg = "Date: " + record.getDate() + " • " + record.getTime() + "\n" +
                "Meal Slot: " + record.getMealSlot() + "\n\n" +
                "Detected Foods:\n" + record.getDetectedFoodsSummary() + "\n\n" +
                "Nutrition Breakdown:\n" +
                "• Calories: " + record.getCalories() + " kcal\n" +
                "• Protein: " + String.format(Locale.US, "%.1fg", record.getProtein()) + "\n" +
                "• Carbs: " + String.format(Locale.US, "%.1fg", record.getCarbs()) + "\n" +
                "• Fat: " + String.format(Locale.US, "%.1fg", record.getFat()) + "\n" +
                "• Fiber: " + String.format(Locale.US, "%.1fg", record.getFiber());

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("📸 Scan Detail")
                .setMessage(detailMsg)
                .setPositiveButton("Close", null);

        if (record.getPhotoUri() != null && !record.getPhotoUri().isEmpty()) {
            builder.setNeutralButton("Delete Photo Only", (dialog, which) -> {
                FoodScanManager.deleteScanPhoto(this, record.getId());
                Toast.makeText(this, "Photo deleted securely for privacy.", Toast.LENGTH_SHORT).show();
                loadHistory();
            });
        }

        builder.show();
    }

    @Override
    public void onDeleteScanClicked(FoodScanRecord record, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Scan Entry")
                .setMessage("Are you sure you want to delete this food scan record and its associated photo?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    FoodScanManager.deleteScanRecord(this, record.getId());
                    loadHistory();
                    Toast.makeText(this, "Scan record deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmClearAll() {
        new AlertDialog.Builder(this)
                .setTitle("🔒 Clear All Scan History")
                .setMessage("Are you sure you want to permanently delete all scan records and stored food photos?")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    FoodScanManager.clearAllScanHistory(this);
                    loadHistory();
                    Toast.makeText(this, "All food scan history cleared.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
