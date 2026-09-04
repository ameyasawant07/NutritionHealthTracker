package com.example.nutritionhealthtracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.nutritionhealthtracker.models.BMIRecord;
import com.example.nutritionhealthtracker.models.ExerciseRecord;
import com.example.nutritionhealthtracker.models.NutritionRecord;
import com.example.nutritionhealthtracker.models.SavedReport;
import com.example.nutritionhealthtracker.models.SleepRecord;
import com.example.nutritionhealthtracker.models.WaterRecord;
import com.example.nutritionhealthtracker.models.WeightRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataManager {

    private static final String PREF_NAME = "NutritionHealthTrackerPrefs";
    private static final String BASE_KEY_DARK_MODE = "app_theme_dark_mode";

    // Auth & Session
    private static final String KEY_CURRENT_USER = "current_logged_in_user";
    private static final String KEY_USER_ACCOUNT_PREFIX = "user_account_";

    // Base Keys — Profile
    private static final String BASE_KEY_NAME = "profile_name";
    private static final String BASE_KEY_AGE = "profile_age";
    private static final String BASE_KEY_GENDER = "profile_gender";
    private static final String BASE_KEY_HEIGHT = "profile_height";
    private static final String BASE_KEY_WEIGHT = "profile_weight";

    // Base Keys — Diet & Goal Preferences
    private static final String BASE_KEY_USER_GOAL = "user_diet_goal";
    private static final String BASE_KEY_DIETARY_TYPE = "user_dietary_type";
    private static final String BASE_KEY_ALLERGIES = "user_allergies";
    private static final String BASE_KEY_FOODS_TO_AVOID = "user_foods_to_avoid";
    private static final String BASE_KEY_CUSTOM_DIET_PLAN = "user_custom_diet_plan_json";

    // Base Keys — History Lists
    private static final String BASE_KEY_BMI_HISTORY = "bmi_history_json";
    private static final String BASE_KEY_NUTRITION_HISTORY = "nutrition_history_json";
    private static final String BASE_KEY_WATER_HISTORY = "water_history_json";
    private static final String BASE_KEY_WEIGHT_HISTORY = "weight_history_json";
    private static final String BASE_KEY_EXERCISE_HISTORY = "exercise_history_json";
    private static final String BASE_KEY_SLEEP_HISTORY = "sleep_history_json";
    private static final String BASE_KEY_SAVED_REPORTS_HISTORY = "saved_reports_history_json";

    // Base Keys — Daily Singles
    private static final String BASE_KEY_WATER_TARGET = "water_target_ml";
    private static final String BASE_KEY_WATER_PREFIX = "water_intake_";
    private static final String BASE_KEY_NUTRITION_PREFIX = "nutrition_record_";

    // Streak Keys
    private static final String BASE_KEY_CURRENT_STREAK = "streak_current";
    private static final String BASE_KEY_LONGEST_STREAK = "streak_longest";
    private static final String BASE_KEY_LAST_STREAK_DATE = "streak_last_date";

    // Notification Preference Keys
    private static final String BASE_KEY_NOTIF_ENABLED = "notif_master_enabled";
    private static final String BASE_KEY_WATER_NOTIF = "notif_water_enabled";
    private static final String BASE_KEY_BREAKFAST_NOTIF = "notif_breakfast_enabled";
    private static final String BASE_KEY_LUNCH_NOTIF = "notif_lunch_enabled";
    private static final String BASE_KEY_DINNER_NOTIF = "notif_dinner_enabled";
    private static final String BASE_KEY_NUTRITION_NOTIF = "notif_nutrition_enabled";
    private static final String BASE_KEY_SUMMARY_NOTIF = "notif_summary_enabled";
    private static final String BASE_KEY_WEIGHT_NOTIF = "notif_weight_enabled";
    private static final String BASE_KEY_EXERCISE_NOTIF = "notif_exercise_enabled";
    private static final String BASE_KEY_SLEEP_NOTIF = "notif_sleep_enabled";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // ==========================================
    // AUTH & SESSION
    // ==========================================

    public static boolean registerUser(Context context, String name, String username, String password) {
        String cleanUsername = username.trim().toLowerCase();
        SharedPreferences prefs = getPrefs(context);
        String accountKey = KEY_USER_ACCOUNT_PREFIX + cleanUsername;
        if (prefs.contains(accountKey)) return false;

        try {
            JSONObject userObj = new JSONObject();
            userObj.put("name", name.trim());
            userObj.put("username", cleanUsername);
            userObj.put("password", password.trim());
            prefs.edit()
                    .putString(accountKey, userObj.toString())
                    .putString(KEY_CURRENT_USER, cleanUsername)
                    .apply();
            migrateLegacyDataIfNeeded(context, cleanUsername);
            if (getProfileName(context).isEmpty()) {
                saveProfile(context, name.trim(), 0, "Select Gender", 0.0, 0.0);
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean loginUser(Context context, String username, String password) {
        String cleanUsername = username.trim().toLowerCase();
        String jsonString = getPrefs(context).getString(KEY_USER_ACCOUNT_PREFIX + cleanUsername, null);
        if (jsonString == null) return false;
        try {
            JSONObject userObj = new JSONObject(jsonString);
            if (userObj.optString("password", "").equals(password.trim())) {
                getPrefs(context).edit().putString(KEY_CURRENT_USER, cleanUsername).apply();
                migrateLegacyDataIfNeeded(context, cleanUsername);
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void logoutUser(Context context) {
        getPrefs(context).edit().remove(KEY_CURRENT_USER).apply();
    }

    public static String getCurrentUsername(Context context) {
        return getPrefs(context).getString(KEY_CURRENT_USER, "");
    }

    public static String getCurrentLoggedInUser(Context context) {
        return getCurrentUsername(context);
    }

    private static String getUserScopedKey(Context context, String baseKey) {
        String username = getCurrentUsername(context);
        if (username == null || username.isEmpty()) return baseKey;
        return "usr_" + username.toLowerCase() + "_" + baseKey;
    }

    private static void migrateLegacyDataIfNeeded(Context context, String targetUsername) {
        SharedPreferences prefs = getPrefs(context);
        String targetPrefix = "usr_" + targetUsername.toLowerCase() + "_";
        if (prefs.contains(targetPrefix + BASE_KEY_BMI_HISTORY) || prefs.contains(targetPrefix + BASE_KEY_NAME)) return;

        SharedPreferences.Editor editor = prefs.edit();
        if (prefs.contains(BASE_KEY_NAME)) editor.putString(targetPrefix + BASE_KEY_NAME, prefs.getString(BASE_KEY_NAME, ""));
        if (prefs.contains(BASE_KEY_AGE)) editor.putInt(targetPrefix + BASE_KEY_AGE, prefs.getInt(BASE_KEY_AGE, 0));
        if (prefs.contains(BASE_KEY_GENDER)) editor.putString(targetPrefix + BASE_KEY_GENDER, prefs.getString(BASE_KEY_GENDER, "Select Gender"));
        if (prefs.contains(BASE_KEY_HEIGHT)) editor.putFloat(targetPrefix + BASE_KEY_HEIGHT, prefs.getFloat(BASE_KEY_HEIGHT, 0f));
        if (prefs.contains(BASE_KEY_WEIGHT)) editor.putFloat(targetPrefix + BASE_KEY_WEIGHT, prefs.getFloat(BASE_KEY_WEIGHT, 0f));
        if (prefs.contains(BASE_KEY_BMI_HISTORY)) editor.putString(targetPrefix + BASE_KEY_BMI_HISTORY, prefs.getString(BASE_KEY_BMI_HISTORY, "[]"));
        if (prefs.contains(BASE_KEY_NUTRITION_HISTORY)) editor.putString(targetPrefix + BASE_KEY_NUTRITION_HISTORY, prefs.getString(BASE_KEY_NUTRITION_HISTORY, "[]"));
        if (prefs.contains(BASE_KEY_WATER_HISTORY)) editor.putString(targetPrefix + BASE_KEY_WATER_HISTORY, prefs.getString(BASE_KEY_WATER_HISTORY, "[]"));
        editor.apply();
    }

    // ==========================================
    // PROFILE
    // ==========================================

    public static void saveProfile(Context context, String name, int age, String gender, double height, double weight) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(getUserScopedKey(context, BASE_KEY_NAME), name);
        editor.putInt(getUserScopedKey(context, BASE_KEY_AGE), age);
        editor.putString(getUserScopedKey(context, BASE_KEY_GENDER), gender);
        editor.putFloat(getUserScopedKey(context, BASE_KEY_HEIGHT), (float) height);
        editor.putFloat(getUserScopedKey(context, BASE_KEY_WEIGHT), (float) weight);
        editor.apply();
        SyncQueueManager.enqueue(context, "PROFILE", "UPDATE", "{\"name\":\"" + name + "\",\"weight\":" + weight + "}");
    }

    public static String getProfileName(Context context) { return getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_NAME), ""); }
    public static int getProfileAge(Context context) { return getPrefs(context).getInt(getUserScopedKey(context, BASE_KEY_AGE), 0); }
    public static String getProfileGender(Context context) { return getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_GENDER), "Select Gender"); }
    public static double getProfileHeight(Context context) { return getPrefs(context).getFloat(getUserScopedKey(context, BASE_KEY_HEIGHT), 0f); }
    public static double getProfileWeight(Context context) { return getPrefs(context).getFloat(getUserScopedKey(context, BASE_KEY_WEIGHT), 0f); }

    // ==========================================
    // DIET & GOAL PREFERENCES
    // ==========================================

    public static String getUserGoal(Context context) {
        return getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_USER_GOAL), "Healthy / Balanced Diet");
    }

    public static void setUserGoal(Context context, String goal) {
        getPrefs(context).edit().putString(getUserScopedKey(context, BASE_KEY_USER_GOAL), goal).apply();
    }

    public static String getDietaryType(Context context) {
        return getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_DIETARY_TYPE), "Vegetarian");
    }

    public static void setDietaryType(Context context, String type) {
        getPrefs(context).edit().putString(getUserScopedKey(context, BASE_KEY_DIETARY_TYPE), type).apply();
    }

    public static String getAllergies(Context context) {
        return getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_ALLERGIES), "");
    }

    public static void setAllergies(Context context, String allergies) {
        getPrefs(context).edit().putString(getUserScopedKey(context, BASE_KEY_ALLERGIES), allergies).apply();
    }

    public static String getFoodsToAvoid(Context context) {
        return getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_FOODS_TO_AVOID), "");
    }

    public static void setFoodsToAvoid(Context context, String foods) {
        getPrefs(context).edit().putString(getUserScopedKey(context, BASE_KEY_FOODS_TO_AVOID), foods).apply();
    }

    public static String getCustomDietPlan(Context context) {
        return getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_CUSTOM_DIET_PLAN), "");
    }

    public static void saveCustomDietPlan(Context context, String json) {
        getPrefs(context).edit().putString(getUserScopedKey(context, BASE_KEY_CUSTOM_DIET_PLAN), json).apply();
    }

    // ==========================================
    // BMI HISTORY
    // ==========================================

    public static void saveBMIRecord(Context context, BMIRecord record) {
        List<BMIRecord> history = getBMIHistory(context);
        history.add(0, record);
        JSONArray arr = new JSONArray();
        for (BMIRecord item : history) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("date", item.getDate()); obj.put("height", item.getHeight());
                obj.put("weight", item.getWeight()); obj.put("bmi", item.getBmi());
                obj.put("category", item.getCategory());
                arr.put(obj);
            } catch (JSONException e) { e.printStackTrace(); }
        }
        getPrefs(context).edit().putString(getUserScopedKey(context, BASE_KEY_BMI_HISTORY), arr.toString()).apply();
        SyncQueueManager.enqueue(context, "BMI", "CREATE", "{\"bmi\":" + record.getBmi() + "}");
    }

    public static List<BMIRecord> getBMIHistory(Context context) {
        List<BMIRecord> list = new ArrayList<>();
        String json = getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_BMI_HISTORY), "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new BMIRecord(o.optString("date"), o.optDouble("height"), o.optDouble("weight"), o.optDouble("bmi"), o.optString("category")));
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return list;
    }

    public static void clearBMIHistory(Context context) { getPrefs(context).edit().remove(getUserScopedKey(context, BASE_KEY_BMI_HISTORY)).apply(); }

    // ==========================================
    // WATER
    // ==========================================

    public static void saveUserWaterTarget(Context context, int targetMl) { getPrefs(context).edit().putInt(getUserScopedKey(context, BASE_KEY_WATER_TARGET), targetMl).apply(); }
    public static int getUserWaterTarget(Context context) { return getPrefs(context).getInt(getUserScopedKey(context, BASE_KEY_WATER_TARGET), 2000); }

    public static void saveWaterIntake(Context context, String dateKey, String displayDate, int amountMl, int targetMl) {
        getPrefs(context).edit().putInt(getUserScopedKey(context, BASE_KEY_WATER_PREFIX + dateKey), amountMl).apply();
        List<WaterRecord> history = getWaterHistory(context);
        boolean updated = false;
        for (WaterRecord item : history) {
            if (item.getDateKey().equalsIgnoreCase(dateKey)) { item.setConsumedMl(amountMl); updated = true; break; }
        }
        if (!updated) history.add(0, new WaterRecord(displayDate, dateKey, amountMl, targetMl));
        saveWaterHistoryList(context, history);
    }

    public static int getWaterIntake(Context context, String dateKey) { return getPrefs(context).getInt(getUserScopedKey(context, BASE_KEY_WATER_PREFIX + dateKey), 0); }

    public static void resetWaterIntake(Context context, String dateKey, String displayDate, int targetMl) { saveWaterIntake(context, dateKey, displayDate, 0, targetMl); }

    public static List<WaterRecord> getWaterHistory(Context context) {
        List<WaterRecord> list = new ArrayList<>();
        String json = getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_WATER_HISTORY), "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new WaterRecord(o.optString("date"), o.optString("dateKey"), o.optInt("consumedMl"), o.optInt("targetMl", 2000)));
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return list;
    }

    private static void saveWaterHistoryList(Context context, List<WaterRecord> list) {
        JSONArray arr = new JSONArray();
        for (WaterRecord item : list) {
            try {
                JSONObject o = new JSONObject();
                o.put("date", item.getDate()); o.put("dateKey", item.getDateKey());
                o.put("consumedMl", item.getConsumedMl()); o.put("targetMl", item.getTargetMl());
                arr.put(o);
            } catch (JSONException e) { e.printStackTrace(); }
        }
        getPrefs(context).edit().putString(getUserScopedKey(context, BASE_KEY_WATER_HISTORY), arr.toString()).apply();
    }

    public static void clearWaterHistory(Context context) { getPrefs(context).edit().remove(getUserScopedKey(context, BASE_KEY_WATER_HISTORY)).apply(); }

    // ==========================================
    // NUTRITION
    // ==========================================

    public static void saveNutritionRecord(Context context, NutritionRecord record, String displayDate) {
        String key = getUserScopedKey(context, BASE_KEY_NUTRITION_PREFIX + record.getDate());
        try {
            JSONObject obj = new JSONObject();
            obj.put("date", record.getDate()); obj.put("displayDate", displayDate);
            obj.put("breakfast", record.getBreakfast()); obj.put("lunch", record.getLunch());
            obj.put("dinner", record.getDinner()); obj.put("snacks", record.getSnacks());
            obj.put("calories", record.getCalories());
            getPrefs(context).edit().putString(key, obj.toString()).apply();
        } catch (JSONException e) { e.printStackTrace(); }

        List<NutritionRecord> history = getNutritionHistory(context);
        boolean updated = false;
        for (int i = 0; i < history.size(); i++) {
            if (history.get(i).getDate().equalsIgnoreCase(record.getDate())) { history.set(i, record); updated = true; break; }
        }
        if (!updated) history.add(0, record);
        saveNutritionHistoryList(context, history);
    }

    public static NutritionRecord getNutritionRecord(Context context, String dateKey) {
        String json = getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_NUTRITION_PREFIX + dateKey), null);
        if (json == null) return null;
        try {
            JSONObject o = new JSONObject(json);
            return new NutritionRecord(o.optString("date", dateKey), o.optString("breakfast"), o.optString("lunch"), o.optString("dinner"), o.optString("snacks"), o.optInt("calories"));
        } catch (JSONException e) { e.printStackTrace(); return null; }
    }

    public static List<NutritionRecord> getNutritionHistory(Context context) {
        List<NutritionRecord> list = new ArrayList<>();
        String json = getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_NUTRITION_HISTORY), "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new NutritionRecord(o.optString("date"), o.optString("breakfast"), o.optString("lunch"), o.optString("dinner"), o.optString("snacks"), o.optInt("calories")));
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return list;
    }

    private static void saveNutritionHistoryList(Context context, List<NutritionRecord> list) {
        JSONArray arr = new JSONArray();
        for (NutritionRecord item : list) {
            try {
                JSONObject o = new JSONObject();
                o.put("date", item.getDate()); o.put("breakfast", item.getBreakfast());
                o.put("lunch", item.getLunch()); o.put("dinner", item.getDinner());
                o.put("snacks", item.getSnacks()); o.put("calories", item.getCalories());
                arr.put(o);
            } catch (JSONException e) { e.printStackTrace(); }
        }
        getPrefs(context).edit().putString(getUserScopedKey(context, BASE_KEY_NUTRITION_HISTORY), arr.toString()).apply();
    }

    public static void clearNutritionHistory(Context context) { getPrefs(context).edit().remove(getUserScopedKey(context, BASE_KEY_NUTRITION_HISTORY)).apply(); }

    // ==========================================
    // WEIGHT HISTORY
    // ==========================================

    public static void saveWeightRecord(Context context, WeightRecord record) {
        List<WeightRecord> history = getWeightHistory(context);
        history.add(0, record);
        JSONArray arr = new JSONArray();
        for (WeightRecord item : history) {
            try {
                JSONObject o = new JSONObject();
                o.put("date", item.getDate()); o.put("dateKey", item.getDateKey());
                o.put("time", item.getTime()); o.put("weightKg", item.getWeightKg());
                arr.put(o);
            } catch (JSONException e) { e.printStackTrace(); }
        }
        getPrefs(context).edit().putString(getUserScopedKey(context, BASE_KEY_WEIGHT_HISTORY), arr.toString()).apply();
    }

    public static List<WeightRecord> getWeightHistory(Context context) {
        List<WeightRecord> list = new ArrayList<>();
        String json = getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_WEIGHT_HISTORY), "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new WeightRecord(o.optString("date"), o.optString("dateKey"), o.optString("time"), o.optDouble("weightKg")));
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return list;
    }

    public static WeightRecord getLatestWeightRecord(Context context) {
        List<WeightRecord> history = getWeightHistory(context);
        return history.isEmpty() ? null : history.get(0);
    }

    public static void clearWeightHistory(Context context) { getPrefs(context).edit().remove(getUserScopedKey(context, BASE_KEY_WEIGHT_HISTORY)).apply(); }

    // ==========================================
    // EXERCISE HISTORY
    // ==========================================

    public static void saveExerciseRecord(Context context, ExerciseRecord record) {
        List<ExerciseRecord> history = getExerciseHistory(context);
        history.add(0, record);
        JSONArray arr = new JSONArray();
        for (ExerciseRecord item : history) {
            try {
                JSONObject o = new JSONObject();
                o.put("date", item.getDate()); o.put("dateKey", item.getDateKey());
                o.put("time", item.getTime()); o.put("exerciseType", item.getExerciseType());
                o.put("durationMinutes", item.getDurationMinutes());
                arr.put(o);
            } catch (JSONException e) { e.printStackTrace(); }
        }
        getPrefs(context).edit().putString(getUserScopedKey(context, BASE_KEY_EXERCISE_HISTORY), arr.toString()).apply();
    }

    public static List<ExerciseRecord> getExerciseHistory(Context context) {
        List<ExerciseRecord> list = new ArrayList<>();
        String json = getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_EXERCISE_HISTORY), "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new ExerciseRecord(o.optString("date"), o.optString("dateKey"), o.optString("time"), o.optString("exerciseType"), o.optInt("durationMinutes")));
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return list;
    }

    public static int getExerciseMinutesForDate(Context context, String dateKey) {
        List<ExerciseRecord> history = getExerciseHistory(context);
        int total = 0;
        for (ExerciseRecord r : history) {
            if (r.getDateKey().equalsIgnoreCase(dateKey)) total += r.getDurationMinutes();
        }
        return total;
    }

    public static void clearExerciseHistory(Context context) { getPrefs(context).edit().remove(getUserScopedKey(context, BASE_KEY_EXERCISE_HISTORY)).apply(); }

    // ==========================================
    // SLEEP HISTORY
    // ==========================================

    public static void saveSleepRecord(Context context, SleepRecord record) {
        List<SleepRecord> history = getSleepHistory(context);
        history.add(0, record);
        JSONArray arr = new JSONArray();
        for (SleepRecord item : history) {
            try {
                JSONObject o = new JSONObject();
                o.put("date", item.getDate()); o.put("dateKey", item.getDateKey());
                o.put("sleepTime", item.getSleepTime()); o.put("wakeTime", item.getWakeTime());
                o.put("durationHours", item.getDurationHours());
                arr.put(o);
            } catch (JSONException e) { e.printStackTrace(); }
        }
        getPrefs(context).edit().putString(getUserScopedKey(context, BASE_KEY_SLEEP_HISTORY), arr.toString()).apply();
    }

    public static List<SleepRecord> getSleepHistory(Context context) {
        List<SleepRecord> list = new ArrayList<>();
        String json = getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_SLEEP_HISTORY), "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new SleepRecord(o.optString("date"), o.optString("dateKey"), o.optString("sleepTime"), o.optString("wakeTime"), o.optDouble("durationHours")));
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return list;
    }

    public static SleepRecord getLatestSleepRecord(Context context) {
        List<SleepRecord> history = getSleepHistory(context);
        return history.isEmpty() ? null : history.get(0);
    }

    public static void clearSleepHistory(Context context) { getPrefs(context).edit().remove(getUserScopedKey(context, BASE_KEY_SLEEP_HISTORY)).apply(); }

    // ==========================================
    // STREAK CALCULATION
    // ==========================================

    public static void updateStreak(Context context) {
        String todayKey = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        int waterToday = getWaterIntake(context, todayKey);
        NutritionRecord nutritionToday = getNutritionRecord(context, todayKey);
        boolean hasMeal = nutritionToday != null && (
                (nutritionToday.getBreakfast() != null && !nutritionToday.getBreakfast().trim().isEmpty()) ||
                (nutritionToday.getLunch() != null && !nutritionToday.getLunch().trim().isEmpty()) ||
                (nutritionToday.getDinner() != null && !nutritionToday.getDinner().trim().isEmpty())
        );

        boolean todayComplete = waterToday > 0 && hasMeal;
        if (!todayComplete) return;

        SharedPreferences prefs = getPrefs(context);
        String lastStreakDate = prefs.getString(getUserScopedKey(context, BASE_KEY_LAST_STREAK_DATE), "");
        int currentStreak = prefs.getInt(getUserScopedKey(context, BASE_KEY_CURRENT_STREAK), 0);
        int longestStreak = prefs.getInt(getUserScopedKey(context, BASE_KEY_LONGEST_STREAK), 0);

        if (todayKey.equals(lastStreakDate)) return;

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        String yesterdayKey = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(yesterday.getTime());

        if (yesterdayKey.equals(lastStreakDate)) {
            currentStreak += 1;
        } else {
            currentStreak = 1;
        }

        if (currentStreak > longestStreak) longestStreak = currentStreak;

        prefs.edit()
                .putInt(getUserScopedKey(context, BASE_KEY_CURRENT_STREAK), currentStreak)
                .putInt(getUserScopedKey(context, BASE_KEY_LONGEST_STREAK), longestStreak)
                .putString(getUserScopedKey(context, BASE_KEY_LAST_STREAK_DATE), todayKey)
                .apply();
    }

    public static int getCurrentStreak(Context context) { return getPrefs(context).getInt(getUserScopedKey(context, BASE_KEY_CURRENT_STREAK), 0); }
    public static int getLongestStreak(Context context) { return getPrefs(context).getInt(getUserScopedKey(context, BASE_KEY_LONGEST_STREAK), 0); }

    // ==========================================
    // NOTIFICATION PREFERENCES
    // ==========================================

    public static boolean isMasterNotificationsEnabled(Context context) { return getPrefs(context).getBoolean(getUserScopedKey(context, BASE_KEY_NOTIF_ENABLED), true); }
    public static void setMasterNotificationsEnabled(Context context, boolean e) { getPrefs(context).edit().putBoolean(getUserScopedKey(context, BASE_KEY_NOTIF_ENABLED), e).apply(); }

    public static boolean isWaterRemindersEnabled(Context context) { return getPrefs(context).getBoolean(getUserScopedKey(context, BASE_KEY_WATER_NOTIF), true); }
    public static void setWaterRemindersEnabled(Context context, boolean e) { getPrefs(context).edit().putBoolean(getUserScopedKey(context, BASE_KEY_WATER_NOTIF), e).apply(); }

    public static boolean isBreakfastReminderEnabled(Context context) { return getPrefs(context).getBoolean(getUserScopedKey(context, BASE_KEY_BREAKFAST_NOTIF), true); }
    public static void setBreakfastReminderEnabled(Context context, boolean e) { getPrefs(context).edit().putBoolean(getUserScopedKey(context, BASE_KEY_BREAKFAST_NOTIF), e).apply(); }

    public static boolean isLunchReminderEnabled(Context context) { return getPrefs(context).getBoolean(getUserScopedKey(context, BASE_KEY_LUNCH_NOTIF), true); }
    public static void setLunchReminderEnabled(Context context, boolean e) { getPrefs(context).edit().putBoolean(getUserScopedKey(context, BASE_KEY_LUNCH_NOTIF), e).apply(); }

    public static boolean isDinnerReminderEnabled(Context context) { return getPrefs(context).getBoolean(getUserScopedKey(context, BASE_KEY_DINNER_NOTIF), true); }
    public static void setDinnerReminderEnabled(Context context, boolean e) { getPrefs(context).edit().putBoolean(getUserScopedKey(context, BASE_KEY_DINNER_NOTIF), e).apply(); }

    public static boolean isNutritionReminderEnabled(Context context) { return getPrefs(context).getBoolean(getUserScopedKey(context, BASE_KEY_NUTRITION_NOTIF), true); }
    public static void setNutritionReminderEnabled(Context context, boolean e) { getPrefs(context).edit().putBoolean(getUserScopedKey(context, BASE_KEY_NUTRITION_NOTIF), e).apply(); }

    public static boolean isDailySummaryEnabled(Context context) { return getPrefs(context).getBoolean(getUserScopedKey(context, BASE_KEY_SUMMARY_NOTIF), true); }
    public static void setDailySummaryEnabled(Context context, boolean e) { getPrefs(context).edit().putBoolean(getUserScopedKey(context, BASE_KEY_SUMMARY_NOTIF), e).apply(); }

    public static boolean isWeightReminderEnabled(Context context) { return getPrefs(context).getBoolean(getUserScopedKey(context, BASE_KEY_WEIGHT_NOTIF), false); }
    public static void setWeightReminderEnabled(Context context, boolean e) { getPrefs(context).edit().putBoolean(getUserScopedKey(context, BASE_KEY_WEIGHT_NOTIF), e).apply(); }

    public static boolean isExerciseReminderEnabled(Context context) { return getPrefs(context).getBoolean(getUserScopedKey(context, BASE_KEY_EXERCISE_NOTIF), false); }
    public static void setExerciseReminderEnabled(Context context, boolean e) { getPrefs(context).edit().putBoolean(getUserScopedKey(context, BASE_KEY_EXERCISE_NOTIF), e).apply(); }

    public static boolean isSleepReminderEnabled(Context context) { return getPrefs(context).getBoolean(getUserScopedKey(context, BASE_KEY_SLEEP_NOTIF), false); }
    public static void setSleepReminderEnabled(Context context, boolean e) { getPrefs(context).edit().putBoolean(getUserScopedKey(context, BASE_KEY_SLEEP_NOTIF), e).apply(); }

    // ==========================================
    // GLOBAL DARK MODE THEME MANAGEMENT
    // ==========================================

    public static boolean isDarkModeEnabled(Context context) {
        return getPrefs(context).getBoolean(getUserScopedKey(context, BASE_KEY_DARK_MODE), false);
    }

    public static void setDarkModeEnabled(Context context, boolean enabled) {
        getPrefs(context).edit().putBoolean(getUserScopedKey(context, BASE_KEY_DARK_MODE), enabled).apply();
    }

    public static void applyTheme(Context context) {
        if (isDarkModeEnabled(context)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // ==========================================
    // SAVED REPORTS MANAGEMENT
    // ==========================================

    public static List<SavedReport> getSavedReports(Context context) {
        List<SavedReport> list = new ArrayList<>();
        String json = getPrefs(context).getString(getUserScopedKey(context, BASE_KEY_SAVED_REPORTS_HISTORY), "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                SavedReport report = SavedReport.fromJson(obj);
                if (report != null) {
                    list.add(report);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void saveReport(Context context, SavedReport report) {
        if (report == null) return;
        List<SavedReport> list = getSavedReports(context);
        // Add to beginning of list (newest first)
        list.add(0, report);
        saveSavedReportsList(context, list);
    }

    public static void deleteReport(Context context, String reportId) {
        if (reportId == null) return;
        List<SavedReport> list = getSavedReports(context);
        List<SavedReport> updated = new ArrayList<>();
        for (SavedReport r : list) {
            if (!reportId.equals(r.getId())) {
                updated.add(r);
            }
        }
        saveSavedReportsList(context, updated);
    }

    private static void saveSavedReportsList(Context context, List<SavedReport> list) {
        JSONArray arr = new JSONArray();
        for (SavedReport r : list) {
            arr.put(r.toJson());
        }
        getPrefs(context).edit()
                .putString(getUserScopedKey(context, BASE_KEY_SAVED_REPORTS_HISTORY), arr.toString())
                .apply();
    }
}
