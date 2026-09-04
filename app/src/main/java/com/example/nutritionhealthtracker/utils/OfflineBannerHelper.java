package com.example.nutritionhealthtracker.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OfflineBannerHelper implements NetworkMonitor.NetworkStatusListener {

    private final Activity activity;
    private TextView bannerTv;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable hideRunnable;

    public OfflineBannerHelper(Activity activity) {
        this.activity = activity;
    }

    public void setupBanner() {
        if (activity == null || activity.isFinishing()) return;

        ViewGroup root = activity.findViewById(android.R.id.content);
        if (root == null) return;

        // Check if root child is a ViewGroup we can attach to, or wrap content
        View mainView = root.getChildAt(0);
        if (mainView instanceof ViewGroup) {
            ViewGroup mainGroup = (ViewGroup) mainView;

            bannerTv = new TextView(activity);
            bannerTv.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            bannerTv.setPadding(32, 16, 32, 16);
            bannerTv.setTextSize(12f);
            bannerTv.setGravity(Gravity.CENTER);
            bannerTv.setVisibility(View.GONE);

            // Insert at index 0 if LinearLayout or add to view hierarchy
            if (mainGroup instanceof LinearLayout) {
                mainGroup.addView(bannerTv, 0);
            } else {
                root.addView(bannerTv);
            }
        }

        NetworkMonitor.getInstance(activity).addListener(this);
    }

    public void unregister() {
        if (activity != null) {
            NetworkMonitor.getInstance(activity).removeListener(this);
        }
    }

    @Override
    public void onNetworkStatusChanged(boolean isOnline) {
        if (bannerTv == null || activity == null || activity.isFinishing()) return;

        if (hideRunnable != null) {
            handler.removeCallbacks(hideRunnable);
        }

        if (!isOnline) {
            bannerTv.setBackgroundColor(Color.parseColor("#991B1B")); // Dark red
            bannerTv.setTextColor(Color.WHITE);
            bannerTv.setText("🔴 Offline Mode — Data will sync when you're back online.");
            bannerTv.setVisibility(View.VISIBLE);
        } else {
            bannerTv.setVisibility(View.GONE);
        }
    }
}
