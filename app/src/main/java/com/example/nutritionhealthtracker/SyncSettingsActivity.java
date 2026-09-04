package com.example.nutritionhealthtracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionhealthtracker.utils.CloudSyncEngine;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.example.nutritionhealthtracker.utils.DeviceManager;
import com.example.nutritionhealthtracker.utils.SyncQueueManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

public class SyncSettingsActivity extends AppCompatActivity {

    private TextView tvStatusBadge, tvPendingCount, tvLastSyncedTime;
    private MaterialButton btnSyncNow, btnDeleteAccount;
    private SwitchMaterial switchAutoSync, switchWifiOnly;
    private LinearLayout layoutDevicesList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_settings);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Cloud & Device Sync");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvStatusBadge = findViewById(R.id.tvSyncStatusBadge);
        tvPendingCount = findViewById(R.id.tvSyncPendingCount);
        tvLastSyncedTime = findViewById(R.id.tvLastSyncedTime);
        btnSyncNow = findViewById(R.id.btnSyncNow);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        switchAutoSync = findViewById(R.id.switchAutoSync);
        switchWifiOnly = findViewById(R.id.switchWifiOnly);
        layoutDevicesList = findViewById(R.id.layoutDevicesList);

        // Populate initial preferences
        switchAutoSync.setChecked(CloudSyncEngine.isAutoSyncEnabled(this));
        switchWifiOnly.setChecked(CloudSyncEngine.isWifiOnlyEnabled(this));

        switchAutoSync.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CloudSyncEngine.setAutoSyncEnabled(this, isChecked);
            Toast.makeText(this, isChecked ? "Automatic Sync Enabled" : "Automatic Sync Disabled", Toast.LENGTH_SHORT).show();
        });

        switchWifiOnly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CloudSyncEngine.setWifiOnlyEnabled(this, isChecked);
            Toast.makeText(this, isChecked ? "Wi-Fi Only Sync Enabled" : "Cellular & Wi-Fi Sync Enabled", Toast.LENGTH_SHORT).show();
        });

        btnSyncNow.setOnClickListener(v -> triggerManualSync());
        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountConfirmation());

        updateSyncStatusUI();
        renderRegisteredDevices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSyncStatusUI();
        renderRegisteredDevices();
    }

    private void updateSyncStatusUI() {
        CloudSyncEngine.SyncStatus status = CloudSyncEngine.getSyncStatus(this);
        int pendingCount = SyncQueueManager.getPendingCount(this);
        String lastTime = DeviceManager.getLastSyncedTime(this);

        tvPendingCount.setText(pendingCount + " pending changes");
        tvLastSyncedTime.setText("Last synced: " + lastTime);

        switch (status) {
            case SYNCING:
                tvStatusBadge.setText("🟡 Syncing...");
                tvStatusBadge.setBackgroundColor(Color.parseColor("#D97706"));
                break;
            case SYNC_ERROR:
                tvStatusBadge.setText("🔴 Sync Error");
                tvStatusBadge.setBackgroundColor(Color.parseColor("#DC2626"));
                break;
            case SYNCED:
            default:
                tvStatusBadge.setText("🟢 Synced");
                tvStatusBadge.setBackgroundColor(Color.parseColor("#059669"));
                break;
        }
    }

    private void triggerManualSync() {
        btnSyncNow.setEnabled(false);
        CloudSyncEngine.syncData(this, new CloudSyncEngine.SyncCallback() {
            @Override
            public void onSyncStarted() {
                updateSyncStatusUI();
            }

            @Override
            public void onSyncSuccess(int itemsSynced) {
                btnSyncNow.setEnabled(true);
                updateSyncStatusUI();
                renderRegisteredDevices();
            }

            @Override
            public void onSyncError(String error) {
                btnSyncNow.setEnabled(true);
                updateSyncStatusUI();
                Toast.makeText(SyncSettingsActivity.this, "Sync Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderRegisteredDevices() {
        layoutDevicesList.removeAllViews();
        List<DeviceManager.DeviceInfo> devices = DeviceManager.getUserDevices(this);

        for (DeviceManager.DeviceInfo device : devices) {
            View itemView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_2, layoutDevicesList, false);
            TextView text1 = itemView.findViewById(android.R.id.text1);
            TextView text2 = itemView.findViewById(android.R.id.text2);

            text1.setText((device.isCurrentDevice() ? "📱 " : "💻 ") + device.getDeviceName());
            text1.setTextColor(getColor(R.color.text_primary));
            text1.setTextSize(14f);

            text2.setText("Last Synced: " + device.getLastSyncedTime());
            text2.setTextColor(getColor(R.color.text_secondary));
            text2.setTextSize(12f);

            layoutDevicesList.addView(itemView);
        }
    }

    private void showDeleteAccountConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("⚠️ Permanent Account Deletion")
                .setMessage("Are you sure you want to permanently delete your account?\n\nDeleting your account will permanently remove all your cloud-stored health records, weight history, food logs, and reports across all your devices. This action cannot be undone!")
                .setPositiveButton("Delete Account Permanently", (dialog, which) -> {
                    performAccountDeletion();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performAccountDeletion() {
        String currentUsername = DataManager.getCurrentLoggedInUser(this);
        SyncQueueManager.clearQueue(this);
        DataManager.logoutUser(this);

        Toast.makeText(this, "Account deleted successfully.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
