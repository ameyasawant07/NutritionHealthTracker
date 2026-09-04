package com.example.nutritionhealthtracker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.nutritionhealthtracker.utils.AlarmScheduler;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Reschedule active user's enabled alarms after reboot
            AlarmScheduler.scheduleUserAlarms(context);
        }
    }
}
