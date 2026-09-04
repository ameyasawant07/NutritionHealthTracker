package com.example.nutritionhealthtracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.nutritionhealthtracker.models.PendingSyncItem;

import java.util.List;

public class CloudSyncEngine {

    public interface SyncCallback {
        void onSyncStarted();
        void onSyncSuccess(int itemsSynced);
        void onSyncError(String error);
    }

    public enum SyncStatus {
        SYNCED,
        SYNCING,
        SYNC_ERROR
    }

    private static final String PREF_NAME = "NutritionHealthTrackerPrefs";
    private static final String BASE_KEY_AUTO_SYNC = "pref_auto_sync_enabled";
    private static final String BASE_KEY_WIFI_ONLY = "pref_wifi_only_enabled";
    private static final String BASE_KEY_LAST_SYNC_STATUS = "pref_last_sync_status";

    private static boolean isSyncing = false;

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static String getUserScopedKey(Context context, String baseKey) {
        String username = DataManager.getCurrentLoggedInUser(context);
        return "usr_" + username.toLowerCase().trim() + "_" + baseKey;
    }

    // Sync Preferences
    public static boolean isAutoSyncEnabled(Context context) {
        return getPrefs(context).getBoolean(getUserScopedKey(context, BASE_KEY_AUTO_SYNC), true);
    }

    public static void setAutoSyncEnabled(Context context, boolean enabled) {
        getPrefs(context).edit().putBoolean(getUserScopedKey(context, BASE_KEY_AUTO_SYNC), enabled).apply();
    }

    public static boolean isWifiOnlyEnabled(Context context) {
        return getPrefs(context).getBoolean(getUserScopedKey(context, BASE_KEY_WIFI_ONLY), false);
    }

    public static void setWifiOnlyEnabled(Context context, boolean enabled) {
        getPrefs(context).edit().putBoolean(getUserScopedKey(context, BASE_KEY_WIFI_ONLY), enabled).apply();
    }

    public static SyncStatus getSyncStatus(Context context) {
        String statusStr = getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_LAST_SYNC_STATUS), "SYNCED");
        try {
            return SyncStatus.valueOf(statusStr);
        } catch (Exception e) {
            return SyncStatus.SYNCED;
        }
    }

    public static void setSyncStatus(Context context, SyncStatus status) {
        getPrefs(context).edit()
                .putString(getUserScopedKey(context, BASE_KEY_LAST_SYNC_STATUS), status.name())
                .apply();
    }

    public static void triggerAutoSyncIfAvailable(Context context) {
        if (!isAutoSyncEnabled(context)) return;

        if (!NetworkMonitor.isOnline(context)) return;

        if (isWifiOnlyEnabled(context) && !NetworkMonitor.isWifiConnected(context)) return;

        syncData(context, null);
    }

    public static void syncData(Context context, SyncCallback callback) {
        if (isSyncing) return;

        if (!NetworkMonitor.isOnline(context)) {
            setSyncStatus(context, SyncStatus.SYNC_ERROR);
            if (callback != null) {
                callback.onSyncError("No internet connection available.");
            }
            return;
        }

        if (isWifiOnlyEnabled(context) && !NetworkMonitor.isWifiConnected(context)) {
            setSyncStatus(context, SyncStatus.SYNC_ERROR);
            if (callback != null) {
                callback.onSyncError("Wi-Fi Only sync is enabled. Connect to Wi-Fi to sync.");
            }
            return;
        }

        isSyncing = true;
        setSyncStatus(context, SyncStatus.SYNCING);

        if (callback != null) callback.onSyncStarted();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<PendingSyncItem> queue = SyncQueueManager.getPendingQueue(context);
            int syncedCount = queue.size();

            // Clear processed items from sync queue
            SyncQueueManager.clearQueue(context);

            // Update last synced time and register active device
            DeviceManager.updateLastSyncedTime(context);
            setSyncStatus(context, SyncStatus.SYNCED);
            isSyncing = false;

            if (callback != null) {
                callback.onSyncSuccess(syncedCount);
            }
        }, 1500);
    }

    public static void performCloudBackup(Context context) {
        syncData(context, null);
    }

    public static void performCloudRestore(Context context) {
        syncData(context, null);
    }
}
