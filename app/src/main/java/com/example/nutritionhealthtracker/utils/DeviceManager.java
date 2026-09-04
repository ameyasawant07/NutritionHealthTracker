package com.example.nutritionhealthtracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class DeviceManager {

    private static final String PREF_NAME = "NutritionHealthTrackerPrefs";
    private static final String KEY_DEVICE_ID = "app_unique_device_id";
    private static final String BASE_KEY_LAST_SYNCED_TIME = "user_last_synced_timestamp";
    private static final String BASE_KEY_REGISTERED_DEVICES = "user_registered_devices_json";

    public static class DeviceInfo {
        private String deviceId;
        private String deviceName;
        private String lastSyncedTime;
        private boolean isCurrentDevice;

        public DeviceInfo(String deviceId, String deviceName, String lastSyncedTime, boolean isCurrentDevice) {
            this.deviceId = deviceId;
            this.deviceName = deviceName;
            this.lastSyncedTime = lastSyncedTime;
            this.isCurrentDevice = isCurrentDevice;
        }

        public String getDeviceId() { return deviceId; }
        public String getDeviceName() { return deviceName; }
        public String getLastSyncedTime() { return lastSyncedTime; }
        public boolean isCurrentDevice() { return isCurrentDevice; }
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static String getUserScopedKey(Context context, String baseKey) {
        String username = DataManager.getCurrentLoggedInUser(context);
        return "usr_" + username.toLowerCase().trim() + "_" + baseKey;
    }

    public static String getDeviceId(Context context) {
        SharedPreferences prefs = getPrefs(context);
        String id = prefs.getString(KEY_DEVICE_ID, null);
        if (id == null) {
            id = "device_" + UUID.randomUUID().toString().substring(0, 8);
            prefs.edit().putString(KEY_DEVICE_ID, id).apply();
        }
        return id;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) return "";
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String getLastSyncedTime(Context context) {
        return getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_LAST_SYNCED_TIME), "Never synced");
    }

    public static void updateLastSyncedTime(Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.US);
        String timeStr = sdf.format(new Date());
        getPrefs(context).edit()
                .putString(getUserScopedKey(context, BASE_KEY_LAST_SYNCED_TIME), timeStr)
                .apply();
        registerCurrentDevice(context, timeStr);
    }

    public static List<DeviceInfo> getUserDevices(Context context) {
        List<DeviceInfo> list = new ArrayList<>();
        String currentId = getDeviceId(context);
        String currentName = getDeviceName();
        String lastSync = getLastSyncedTime(context);

        list.add(new DeviceInfo(currentId, currentName + " (This Device)", lastSync, true));

        String json = getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_REGISTERED_DEVICES), "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String id = obj.optString("deviceId");
                if (!id.equals(currentId)) {
                    list.add(new DeviceInfo(
                            id,
                            obj.optString("deviceName", "Other Device"),
                            obj.optString("lastSyncedTime", "Recently"),
                            false
                    ));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static void registerCurrentDevice(Context context, String lastSyncTime) {
        List<DeviceInfo> existing = getUserDevices(context);
        JSONArray arr = new JSONArray();

        String currentId = getDeviceId(context);
        try {
            JSONObject currentObj = new JSONObject();
            currentObj.put("deviceId", currentId);
            currentObj.put("deviceName", getDeviceName());
            currentObj.put("lastSyncedTime", lastSyncTime);
            arr.put(currentObj);

            for (DeviceInfo d : existing) {
                if (!d.getDeviceId().equals(currentId)) {
                    JSONObject obj = new JSONObject();
                    obj.put("deviceId", d.getDeviceId());
                    obj.put("deviceName", d.getDeviceName());
                    obj.put("lastSyncedTime", d.getLastSyncedTime());
                    arr.put(obj);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getPrefs(context).edit()
                .putString(getUserScopedKey(context, BASE_KEY_REGISTERED_DEVICES), arr.toString())
                .apply();
    }
}
