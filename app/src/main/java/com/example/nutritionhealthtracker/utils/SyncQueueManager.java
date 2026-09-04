package com.example.nutritionhealthtracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.nutritionhealthtracker.models.PendingSyncItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SyncQueueManager {

    private static final String PREF_NAME = "NutritionHealthTrackerPrefs";
    private static final String BASE_KEY_SYNC_QUEUE = "offline_sync_queue_json";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static String getUserScopedKey(Context context, String baseKey) {
        String username = DataManager.getCurrentLoggedInUser(context);
        return "usr_" + username.toLowerCase().trim() + "_" + baseKey;
    }

    public static List<PendingSyncItem> getPendingQueue(Context context) {
        List<PendingSyncItem> list = new ArrayList<>();
        String json = getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_SYNC_QUEUE), "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                PendingSyncItem item = PendingSyncItem.fromJson(obj);
                if (item != null) {
                    list.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void enqueue(Context context, String modelType, String action, String payloadJson) {
        String recordId = UUID.randomUUID().toString();
        String userId = DataManager.getCurrentLoggedInUser(context);
        String deviceId = DeviceManager.getDeviceId(context);
        long updatedAt = System.currentTimeMillis();

        PendingSyncItem item = new PendingSyncItem(recordId, userId, deviceId, modelType, action, updatedAt, "PENDING_SYNC", payloadJson);
        List<PendingSyncItem> queue = getPendingQueue(context);
        queue.add(item);
        saveQueue(context, queue);
    }

    public static void removeFromQueue(Context context, String recordId) {
        if (recordId == null) return;
        List<PendingSyncItem> queue = getPendingQueue(context);
        List<PendingSyncItem> updated = new ArrayList<>();
        for (PendingSyncItem item : queue) {
            if (!recordId.equals(item.getRecordId())) {
                updated.add(item);
            }
        }
        saveQueue(context, updated);
    }

    public static void clearQueue(Context context) {
        getPrefs(context).edit().remove(getUserScopedKey(context, BASE_KEY_SYNC_QUEUE)).apply();
    }

    private static void saveQueue(Context context, List<PendingSyncItem> list) {
        JSONArray arr = new JSONArray();
        for (PendingSyncItem item : list) {
            arr.put(item.toJson());
        }
        getPrefs(context).edit()
                .putString(getUserScopedKey(context, BASE_KEY_SYNC_QUEUE), arr.toString())
                .apply();
    }

    public static int getPendingCount(Context context) {
        return getPendingQueue(context).size();
    }
}
