package com.example.nutritionhealthtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionhealthtracker.utils.AlarmScheduler;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private boolean isLoginMode = true;

    private MaterialButtonToggleGroup toggleGroup;
    private TextInputLayout tilFullName;
    private TextInputEditText etFullName, etUsername, etPassword;
    private MaterialButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toggleGroup = findViewById(R.id.toggleGroupAuth);
        tilFullName = findViewById(R.id.tilFullName);
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSubmit = findViewById(R.id.btnAuthSubmit);

        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.btnTabLogin) {
                        isLoginMode = true;
                        tilFullName.setVisibility(View.GONE);
                        btnSubmit.setText("Log In");
                    } else if (checkedId == R.id.btnTabRegister) {
                        isLoginMode = false;
                        tilFullName.setVisibility(View.VISIBLE);
                        btnSubmit.setText("Create Account");
                    }
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoginMode) {
                    performLogin();
                } else {
                    performRegister();
                }
            }
        });
    }

    private void performLogin() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (username.isEmpty()) {
            etUsername.setError("Please enter your username.");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Please enter your password.");
            etPassword.requestFocus();
            return;
        }

        boolean success = DataManager.loginUser(this, username, password);
        if (success) {
            AlarmScheduler.scheduleUserAlarms(this);
            Toast.makeText(this, "Welcome back, " + username + "!", Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            Toast.makeText(this, "Invalid username or password. Please try again or create an account.", Toast.LENGTH_LONG).show();
        }
    }

    private void performRegister() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (fullName.isEmpty()) {
            etFullName.setError("Please enter your full name.");
            etFullName.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            etUsername.setError("Please choose a username.");
            etUsername.requestFocus();
            return;
        }

        if (username.length() < 3) {
            etUsername.setError("Username must be at least 3 characters long.");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Please enter a password.");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 4) {
            etPassword.setError("Password must be at least 4 characters long.");
            etPassword.requestFocus();
            return;
        }

        boolean success = DataManager.registerUser(this, fullName, username, password);
        if (success) {
            AlarmScheduler.scheduleUserAlarms(this);
            Toast.makeText(this, "Account created successfully! Welcome, " + fullName + ".", Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            etUsername.setError("Username already exists. Please choose a different username.");
            etUsername.requestFocus();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
