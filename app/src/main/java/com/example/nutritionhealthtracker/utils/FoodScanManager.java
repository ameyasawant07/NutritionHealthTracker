package com.example.nutritionhealthtracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.nutritionhealthtracker.models.FoodScanRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FoodScanManager {

    private static final String PREF_NAME = "NutritionHealthTrackerPrefs";
    private static final String BASE_KEY_SCAN_HISTORY = "food_scan_history_json";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static String getUserScopedKey(Context context, String baseKey) {
        String username = DataManager.getCurrentUsername(context);
        if (username == null || username.isEmpty()) return baseKey;
        return "usr_" + username.toLowerCase() + "_" + baseKey;
    }

    public static void saveScanRecord(Context context, FoodScanRecord record) {
        List<FoodScanRecord> history = getScanHistory(context);
        history.add(0, record);
        saveHistoryList(context, history);
    }

    public static List<FoodScanRecord> getScanHistory(Context context) {
        List<FoodScanRecord> list = new ArrayList<>();
        String json = getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_SCAN_HISTORY), "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new FoodScanRecord(
                        o.optString("id"),
                        o.optString("date"),
                        o.optString("dateKey"),
                        o.optString("time"),
                        o.optString("photoUri"),
                        o.optString("detectedFoodsSummary"),
                        o.optInt("calories"),
                        o.optDouble("protein"),
                        o.optDouble("carbs"),
                        o.optDouble("fat"),
                        o.optDouble("fiber"),
                        o.optString("mealSlot", "Lunch")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean deleteScanRecord(Context context, String scanId) {
        List<FoodScanRecord> history = getScanHistory(context);
        FoodScanRecord target = null;
        for (FoodScanRecord item : history) {
            if (item.getId().equals(scanId)) {
                target = item;
                break;
            }
        }
        if (target != null) {
            deletePhotoFile(target.getPhotoUri());
            history.remove(target);
            saveHistoryList(context, history);
            return true;
        }
        return false;
    }

    public static boolean deleteScanPhoto(Context context, String scanId) {
        List<FoodScanRecord> history = getScanHistory(context);
        for (FoodScanRecord item : history) {
            if (item.getId().equals(scanId)) {
                deletePhotoFile(item.getPhotoUri());
                item.setPhotoUri("");
                saveHistoryList(context, history);
                return true;
            }
        }
        return false;
    }

    public static void clearAllScanHistory(Context context) {
        List<FoodScanRecord> history = getScanHistory(context);
        for (FoodScanRecord item : history) {
            deletePhotoFile(item.getPhotoUri());
        }
        getPrefs(context).edit().remove(getUserScopedKey(context, BASE_KEY_SCAN_HISTORY)).apply();
    }

    private static void deletePhotoFile(String photoUriStr) {
        if (photoUriStr != null && !photoUriStr.isEmpty() && photoUriStr.startsWith("file://")) {
            try {
                File file = new File(photoUriStr.replace("file://", ""));
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveHistoryList(Context context, List<FoodScanRecord> list) {
        JSONArray arr = new JSONArray();
        for (FoodScanRecord item : list) {
            try {
                JSONObject o = new JSONObject();
                o.put("id", item.getId());
                o.put("date", item.getDate());
                o.put("dateKey", item.getDateKey());
                o.put("time", item.getTime());
                o.put("photoUri", item.getPhotoUri());
                o.put("detectedFoodsSummary", item.getDetectedFoodsSummary());
                o.put("calories", item.getCalories());
                o.put("protein", item.getProtein());
                o.put("carbs", item.getCarbs());
                o.put("fat", item.getFat());
                o.put("fiber", item.getFiber());
                o.put("mealSlot", item.getMealSlot());
                arr.put(o);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        getPrefs(context).edit().putString(getUserScopedKey(context, BASE_KEY_SCAN_HISTORY), arr.toString()).apply();
    }
}
