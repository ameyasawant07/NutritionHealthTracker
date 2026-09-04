package com.example.nutritionhealthtracker.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.nutritionhealthtracker.receivers.AlarmReceiver;

import java.util.Calendar;

public class AlarmScheduler {

    public static final String ACTION_WATER_REMINDER = "com.example.nutritionhealthtracker.WATER_REMINDER";
    public static final String ACTION_BREAKFAST_REMINDER = "com.example.nutritionhealthtracker.BREAKFAST_REMINDER";
    public static final String ACTION_LUNCH_REMINDER = "com.example.nutritionhealthtracker.LUNCH_REMINDER";
    public static final String ACTION_DINNER_REMINDER = "com.example.nutritionhealthtracker.DINNER_REMINDER";
    public static final String ACTION_NUTRITION_REMINDER = "com.example.nutritionhealthtracker.NUTRITION_REMINDER";
    public static final String ACTION_SUMMARY_REMINDER = "com.example.nutritionhealthtracker.SUMMARY_REMINDER";

    public static final String ACTION_WEIGHT_REMINDER = "com.example.nutritionhealthtracker.WEIGHT_REMINDER";
    public static final String ACTION_EXERCISE_REMINDER = "com.example.nutritionhealthtracker.EXERCISE_REMINDER";
    public static final String ACTION_SLEEP_REMINDER = "com.example.nutritionhealthtracker.SLEEP_REMINDER";

    public static final String EXTRA_USERNAME = "extra_username";

    private static final int REQ_WATER_9AM = 2001;
    private static final int REQ_WATER_11AM = 2002;
    private static final int REQ_WATER_1PM = 2003;
    private static final int REQ_WATER_3PM = 2004;
    private static final int REQ_WATER_5PM = 2005;
    private static final int REQ_WATER_7PM = 2006;

    private static final int REQ_BREAKFAST = 2010;
    private static final int REQ_LUNCH = 2011;
    private static final int REQ_DINNER = 2012;
    private static final int REQ_NUTRITION = 2013;
    private static final int REQ_SUMMARY = 2014;

    private static final int REQ_WEIGHT = 2015;
    private static final int REQ_EXERCISE = 2016;
    private static final int REQ_SLEEP = 2017;

    public static void scheduleUserAlarms(Context context) {
        String activeUser = DataManager.getCurrentUsername(context);
        if (activeUser == null || activeUser.isEmpty()) {
            cancelUserAlarms(context);
            return;
        }

        if (!DataManager.isMasterNotificationsEnabled(context)) {
            cancelUserAlarms(context);
            return;
        }

        // Meal Alarms
        if (DataManager.isBreakfastReminderEnabled(context)) scheduleDailyAlarm(context, REQ_BREAKFAST, ACTION_BREAKFAST_REMINDER, activeUser, 8, 30);
        else cancelSingleAlarm(context, REQ_BREAKFAST, ACTION_BREAKFAST_REMINDER);

        if (DataManager.isLunchReminderEnabled(context)) scheduleDailyAlarm(context, REQ_LUNCH, ACTION_LUNCH_REMINDER, activeUser, 13, 0);
        else cancelSingleAlarm(context, REQ_LUNCH, ACTION_LUNCH_REMINDER);

        if (DataManager.isDinnerReminderEnabled(context)) scheduleDailyAlarm(context, REQ_DINNER, ACTION_DINNER_REMINDER, activeUser, 20, 0);
        else cancelSingleAlarm(context, REQ_DINNER, ACTION_DINNER_REMINDER);

        // Nutrition Check
        if (DataManager.isNutritionReminderEnabled(context)) scheduleDailyAlarm(context, REQ_NUTRITION, ACTION_NUTRITION_REMINDER, activeUser, 21, 30);
        else cancelSingleAlarm(context, REQ_NUTRITION, ACTION_NUTRITION_REMINDER);

        // Daily Summary
        if (DataManager.isDailySummaryEnabled(context)) scheduleDailyAlarm(context, REQ_SUMMARY, ACTION_SUMMARY_REMINDER, activeUser, 21, 0);
        else cancelSingleAlarm(context, REQ_SUMMARY, ACTION_SUMMARY_REMINDER);

        // Weight Check (9:00 AM)
        if (DataManager.isWeightReminderEnabled(context)) scheduleDailyAlarm(context, REQ_WEIGHT, ACTION_WEIGHT_REMINDER, activeUser, 9, 0);
        else cancelSingleAlarm(context, REQ_WEIGHT, ACTION_WEIGHT_REMINDER);

        // Exercise Reminder (5:00 PM)
        if (DataManager.isExerciseReminderEnabled(context)) scheduleDailyAlarm(context, REQ_EXERCISE, ACTION_EXERCISE_REMINDER, activeUser, 17, 0);
        else cancelSingleAlarm(context, REQ_EXERCISE, ACTION_EXERCISE_REMINDER);

        // Sleep Reminder (10:00 PM)
        if (DataManager.isSleepReminderEnabled(context)) scheduleDailyAlarm(context, REQ_SLEEP, ACTION_SLEEP_REMINDER, activeUser, 22, 0);
        else cancelSingleAlarm(context, REQ_SLEEP, ACTION_SLEEP_REMINDER);

        // Water Daytime Alarms
        if (DataManager.isWaterRemindersEnabled(context)) {
            scheduleDailyAlarm(context, REQ_WATER_9AM, ACTION_WATER_REMINDER, activeUser, 9, 0);
            scheduleDailyAlarm(context, REQ_WATER_11AM, ACTION_WATER_REMINDER, activeUser, 11, 0);
            scheduleDailyAlarm(context, REQ_WATER_1PM, ACTION_WATER_REMINDER, activeUser, 13, 0);
            scheduleDailyAlarm(context, REQ_WATER_3PM, ACTION_WATER_REMINDER, activeUser, 15, 0);
            scheduleDailyAlarm(context, REQ_WATER_5PM, ACTION_WATER_REMINDER, activeUser, 17, 0);
            scheduleDailyAlarm(context, REQ_WATER_7PM, ACTION_WATER_REMINDER, activeUser, 19, 0);
        } else {
            cancelSingleAlarm(context, REQ_WATER_9AM, ACTION_WATER_REMINDER);
            cancelSingleAlarm(context, REQ_WATER_11AM, ACTION_WATER_REMINDER);
            cancelSingleAlarm(context, REQ_WATER_1PM, ACTION_WATER_REMINDER);
            cancelSingleAlarm(context, REQ_WATER_3PM, ACTION_WATER_REMINDER);
            cancelSingleAlarm(context, REQ_WATER_5PM, ACTION_WATER_REMINDER);
            cancelSingleAlarm(context, REQ_WATER_7PM, ACTION_WATER_REMINDER);
        }
    }

    private static void scheduleDailyAlarm(Context context, int requestCode, String action, String username, int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_USERNAME, username);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    private static void cancelSingleAlarm(Context context, int requestCode, String action) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(action);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public static void cancelUserAlarms(Context context) {
        cancelSingleAlarm(context, REQ_BREAKFAST, ACTION_BREAKFAST_REMINDER);
        cancelSingleAlarm(context, REQ_LUNCH, ACTION_LUNCH_REMINDER);
        cancelSingleAlarm(context, REQ_DINNER, ACTION_DINNER_REMINDER);
        cancelSingleAlarm(context, REQ_NUTRITION, ACTION_NUTRITION_REMINDER);
        cancelSingleAlarm(context, REQ_SUMMARY, ACTION_SUMMARY_REMINDER);
        cancelSingleAlarm(context, REQ_WEIGHT, ACTION_WEIGHT_REMINDER);
        cancelSingleAlarm(context, REQ_EXERCISE, ACTION_EXERCISE_REMINDER);
        cancelSingleAlarm(context, REQ_SLEEP, ACTION_SLEEP_REMINDER);
        cancelSingleAlarm(context, REQ_WATER_9AM, ACTION_WATER_REMINDER);
        cancelSingleAlarm(context, REQ_WATER_11AM, ACTION_WATER_REMINDER);
        cancelSingleAlarm(context, REQ_WATER_1PM, ACTION_WATER_REMINDER);
        cancelSingleAlarm(context, REQ_WATER_3PM, ACTION_WATER_REMINDER);
        cancelSingleAlarm(context, REQ_WATER_5PM, ACTION_WATER_REMINDER);
        cancelSingleAlarm(context, REQ_WATER_7PM, ACTION_WATER_REMINDER);
    }
}
