package com.example.nutritionhealthtracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionhealthtracker.utils.DataManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DataManager.applyTheme(this); // Apply saved theme before layout inflation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Transition after 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                String activeUser = DataManager.getCurrentUsername(SplashActivity.this);
                Intent intent;
                if (activeUser != null && !activeUser.trim().isEmpty()) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
