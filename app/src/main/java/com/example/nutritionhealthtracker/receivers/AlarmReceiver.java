package com.example.nutritionhealthtracker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.nutritionhealthtracker.ExerciseTrackerActivity;
import com.example.nutritionhealthtracker.HealthHistoryActivity;
import com.example.nutritionhealthtracker.NutritionActivity;
import com.example.nutritionhealthtracker.SleepTrackerActivity;
import com.example.nutritionhealthtracker.WaterTrackerActivity;
import com.example.nutritionhealthtracker.WeightTrackerActivity;
import com.example.nutritionhealthtracker.models.NutritionRecord;
import com.example.nutritionhealthtracker.models.SleepRecord;
import com.example.nutritionhealthtracker.models.WeightRecord;
import com.example.nutritionhealthtracker.utils.AlarmScheduler;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.example.nutritionhealthtracker.utils.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        String alarmUsername = intent.getStringExtra(AlarmScheduler.EXTRA_USERNAME);
        String currentActiveUser = DataManager.getCurrentUsername(context);

        if (currentActiveUser == null || currentActiveUser.isEmpty() || !currentActiveUser.equalsIgnoreCase(alarmUsername)) {
            return;
        }

        if (!DataManager.isMasterNotificationsEnabled(context)) {
            return;
        }

        String action = intent.getAction();
        String todayDateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        switch (action) {
            case AlarmScheduler.ACTION_WATER_REMINDER:
                handleWaterReminder(context, todayDateKey);
                break;
            case AlarmScheduler.ACTION_BREAKFAST_REMINDER:
                handleBreakfastReminder(context, todayDateKey);
                break;
            case AlarmScheduler.ACTION_LUNCH_REMINDER:
                handleLunchReminder(context, todayDateKey);
                break;
            case AlarmScheduler.ACTION_DINNER_REMINDER:
                handleDinnerReminder(context, todayDateKey);
                break;
            case AlarmScheduler.ACTION_NUTRITION_REMINDER:
                handleNutritionReminder(context, todayDateKey);
                break;
            case AlarmScheduler.ACTION_WEIGHT_REMINDER:
                handleWeightReminder(context, todayDateKey);
                break;
            case AlarmScheduler.ACTION_EXERCISE_REMINDER:
                handleExerciseReminder(context, todayDateKey);
                break;
            case AlarmScheduler.ACTION_SLEEP_REMINDER:
                handleSleepReminder(context, todayDateKey);
                break;
            case AlarmScheduler.ACTION_SUMMARY_REMINDER:
                handleSummaryReminder(context, todayDateKey);
                break;
        }
    }

    private void handleWaterReminder(Context context, String todayDateKey) {
        if (!DataManager.isWaterRemindersEnabled(context)) return;
        int currentIntake = DataManager.getWaterIntake(context, todayDateKey);
        int targetMl = DataManager.getUserWaterTarget(context);
        if (currentIntake >= targetMl) return;

        NotificationHelper.showNotification(
                context, NotificationHelper.NOTIF_ID_WATER, NotificationHelper.CHANNEL_WATER,
                "💧 Time for some water",
                "You've had " + currentIntake + " ml of your " + targetMl + " ml goal today. Keep going!",
                new Intent(context, WaterTrackerActivity.class)
        );
    }

    private void handleBreakfastReminder(Context context, String todayDateKey) {
        if (!DataManager.isBreakfastReminderEnabled(context)) return;
        NutritionRecord record = DataManager.getNutritionRecord(context, todayDateKey);
        if (record != null && record.getBreakfast() != null && !record.getBreakfast().trim().isEmpty()) return;

        NotificationHelper.showNotification(
                context, NotificationHelper.NOTIF_ID_BREAKFAST, NotificationHelper.CHANNEL_MEALS,
                "🍳 Breakfast Reminder", "Have you had your breakfast today?",
                new Intent(context, NutritionActivity.class)
        );
    }

    private void handleLunchReminder(Context context, String todayDateKey) {
        if (!DataManager.isLunchReminderEnabled(context)) return;
        NutritionRecord record = DataManager.getNutritionRecord(context, todayDateKey);
        if (record != null && record.getLunch() != null && !record.getLunch().trim().isEmpty()) return;

        NotificationHelper.showNotification(
                context, NotificationHelper.NOTIF_ID_LUNCH, NotificationHelper.CHANNEL_MEALS,
                "🍱 Lunch Reminder", "Have you had your lunch today?",
                new Intent(context, NutritionActivity.class)
        );
    }

    private void handleDinnerReminder(Context context, String todayDateKey) {
        if (!DataManager.isDinnerReminderEnabled(context)) return;
        NutritionRecord record = DataManager.getNutritionRecord(context, todayDateKey);
        if (record != null && record.getDinner() != null && !record.getDinner().trim().isEmpty()) return;

        NotificationHelper.showNotification(
                context, NotificationHelper.NOTIF_ID_DINNER, NotificationHelper.CHANNEL_MEALS,
                "🍽️ Dinner Reminder", "Have you had your dinner today?",
                new Intent(context, NutritionActivity.class)
        );
    }

    private void handleNutritionReminder(Context context, String todayDateKey) {
        if (!DataManager.isNutritionReminderEnabled(context)) return;
        NutritionRecord record = DataManager.getNutritionRecord(context, todayDateKey);
        if (record != null && !record.getBreakfast().isEmpty() && !record.getLunch().isEmpty() && !record.getDinner().isEmpty()) return;

        NotificationHelper.showNotification(
                context, NotificationHelper.NOTIF_ID_NUTRITION_CHECK, NotificationHelper.CHANNEL_MEALS,
                "🥗 Nutrition Check", "Don't forget to record today's meals in Nutrition Tracker.",
                new Intent(context, NutritionActivity.class)
        );
    }

    private void handleWeightReminder(Context context, String todayDateKey) {
        if (!DataManager.isWeightReminderEnabled(context)) return;
        NotificationHelper.showNotification(
                context, 1010, NotificationHelper.CHANNEL_GENERAL,
                "⚖️ Weight Check", "Don't forget to record your weight today.",
                new Intent(context, WeightTrackerActivity.class)
        );
    }

    private void handleExerciseReminder(Context context, String todayDateKey) {
        if (!DataManager.isExerciseReminderEnabled(context)) return;
        int exMins = DataManager.getExerciseMinutesForDate(context, todayDateKey);
        if (exMins > 0) return;

        NotificationHelper.showNotification(
                context, 1011, NotificationHelper.CHANNEL_GENERAL,
                "🏃 Have you completed your activity today?", "Don't forget to log your exercise duration.",
                new Intent(context, ExerciseTrackerActivity.class)
        );
    }

    private void handleSleepReminder(Context context, String todayDateKey) {
        if (!DataManager.isSleepReminderEnabled(context)) return;
        NotificationHelper.showNotification(
                context, 1012, NotificationHelper.CHANNEL_GENERAL,
                "😴 Sleep Reminder", "Don't forget to record your sleep schedule.",
                new Intent(context, SleepTrackerActivity.class)
        );
    }

    private void handleSummaryReminder(Context context, String todayDateKey) {
        if (!DataManager.isDailySummaryEnabled(context)) return;

        int currentWater = DataManager.getWaterIntake(context, todayDateKey);
        int targetWater = DataManager.getUserWaterTarget(context);

        NutritionRecord record = DataManager.getNutritionRecord(context, todayDateKey);
        int calories = (record != null) ? record.getCalories() : 0;

        int exMins = DataManager.getExerciseMinutesForDate(context, todayDateKey);

        SleepRecord latestSleep = DataManager.getLatestSleepRecord(context);
        double sleepHrs = (latestSleep != null) ? latestSleep.getDurationHours() : 0.0;

        WeightRecord latestWeight = DataManager.getLatestWeightRecord(context);
        double weightKg = (latestWeight != null) ? latestWeight.getWeightKg() : 0.0;

        int streak = DataManager.getCurrentStreak(context);

        StringBuilder sb = new StringBuilder();
        sb.append("Water: ").append(currentWater).append(" / ").append(targetWater).append(" ml\n");
        sb.append("Calories: ").append(calories).append(" kcal\n");
        sb.append("Exercise: ").append(exMins).append(" min\n");
        sb.append("Sleep: ").append(String.format(Locale.US, "%.1f", sleepHrs)).append(" hrs\n");
        if (weightKg > 0) sb.append("Weight: ").append(String.format(Locale.US, "%.1f", weightKg)).append(" kg\n");
        sb.append("Streak: ").append(streak).append(" days 🔥");

        NotificationHelper.showNotification(
                context,
                NotificationHelper.NOTIF_ID_SUMMARY,
                NotificationHelper.CHANNEL_SUMMARY,
                "📊 Today's Health Summary",
                sb.toString(),
                new Intent(context, HealthHistoryActivity.class)
        );
    }
}
