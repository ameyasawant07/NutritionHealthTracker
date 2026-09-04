package com.example.nutritionhealthtracker.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.nutritionhealthtracker.R;

public class NotificationHelper {

    public static final String CHANNEL_WATER = "channel_water";
    public static final String CHANNEL_MEALS = "channel_meals";
    public static final String CHANNEL_SUMMARY = "channel_summary";
    public static final String CHANNEL_GENERAL = "channel_general";

    public static final int NOTIF_ID_TEST = 1001;
    public static final int NOTIF_ID_WATER = 1002;
    public static final int NOTIF_ID_BREAKFAST = 1003;
    public static final int NOTIF_ID_LUNCH = 1004;
    public static final int NOTIF_ID_DINNER = 1005;
    public static final int NOTIF_ID_NUTRITION_CHECK = 1006;
    public static final int NOTIF_ID_SUMMARY = 1007;

    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager == null) return;

            // Channel 1: Water Reminders
            NotificationChannel waterChannel = new NotificationChannel(
                    CHANNEL_WATER,
                    "💧 Water Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            waterChannel.setDescription("Reminders to keep you hydrated throughout the day.");

            // Channel 2: Meal Reminders
            NotificationChannel mealsChannel = new NotificationChannel(
                    CHANNEL_MEALS,
                    "🍳 Meal & Daily Nutrition Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            mealsChannel.setDescription("Reminders for breakfast, lunch, dinner, and nutrition logging.");

            // Channel 3: Daily Health Summary
            NotificationChannel summaryChannel = new NotificationChannel(
                    CHANNEL_SUMMARY,
                    "📊 Daily Health Summaries",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            summaryChannel.setDescription("Daily health tracking breakdown and progress summaries.");

            // Channel 4: General System Reminders
            NotificationChannel generalChannel = new NotificationChannel(
                    CHANNEL_GENERAL,
                    "ℹ️ General Health Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            generalChannel.setDescription("General health guidelines and test notifications.");

            manager.createNotificationChannel(waterChannel);
            manager.createNotificationChannel(mealsChannel);
            manager.createNotificationChannel(summaryChannel);
            manager.createNotificationChannel(generalChannel);
        }
    }

    public static void showNotification(Context context, int notificationId, String channelId, String title, String message, Intent targetIntent) {
        createNotificationChannels(context);

        PendingIntent pendingIntent = null;
        if (targetIntent != null) {
            targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(
                    context,
                    notificationId,
                    targetIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public static void showTestNotification(Context context) {
        showNotification(
                context,
                NOTIF_ID_TEST,
                CHANNEL_GENERAL,
                "🔔 Test Notification",
                "Notifications are working correctly for Nutrition & Health Tracker!",
                null
        );
    }
}
