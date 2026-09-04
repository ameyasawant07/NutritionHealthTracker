package com.example.nutritionhealthtracker;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nutritionhealthtracker.utils.FoodRecognitionEngine;
import com.example.nutritionhealthtracker.utils.NetworkMonitor;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FoodScanActivity extends AppCompatActivity {

    private ImageView ivScanPreview;
    private LinearLayout layoutScanPlaceholder;
    private MaterialCardView cardProcessing;
    private TextView tvProcessingStep;
    private MaterialButton btnTakePhoto, btnSelectGallery, btnViewScanHistory;
    private Toolbar toolbar;

    private Uri currentPhotoUri;
    private String currentPhotoPath;
    private Bitmap selectedBitmap;

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Camera permission is required to scan food photos.", Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && currentPhotoPath != null) {
                    selectedBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                    if (selectedBitmap != null) {
                        displayImageAndProcess(selectedBitmap);
                    }
                }
            });

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    try (InputStream is = getContentResolver().openInputStream(uri)) {
                        selectedBitmap = BitmapFactory.decodeStream(is);
                        if (selectedBitmap != null) {
                            saveTempBitmap(selectedBitmap);
                            displayImageAndProcess(selectedBitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load image from gallery.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_scan);

        View rootScan = findViewById(R.id.rootScan);
        if (rootScan != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootScan, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }

        toolbar = findViewById(R.id.toolbarScan);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        ivScanPreview = findViewById(R.id.ivScanPreview);
        layoutScanPlaceholder = findViewById(R.id.layoutScanPlaceholder);
        cardProcessing = findViewById(R.id.cardProcessing);
        tvProcessingStep = findViewById(R.id.tvProcessingStep);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSelectGallery = findViewById(R.id.btnSelectGallery);
        btnViewScanHistory = findViewById(R.id.btnViewScanHistory);

        btnTakePhoto.setOnClickListener(v -> checkCameraPermissionAndLaunch());
        btnSelectGallery.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        btnViewScanHistory.setOnClickListener(v -> startActivity(new Intent(this, FoodScanHistoryActivity.class)));
    }

    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            File photoFile = createImageFile();
            currentPhotoUri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    photoFile
            );
            cameraLauncher.launch(currentPhotoUri);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not open camera.", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws Exception {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("FOOD_SCAN_" + timeStamp, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void saveTempBitmap(Bitmap bmp) {
        try {
            File file = createImageFile();
            try (FileOutputStream out = new FileOutputStream(file)) {
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayImageAndProcess(Bitmap bitmap) {
        layoutScanPlaceholder.setVisibility(View.GONE);
        ivScanPreview.setVisibility(View.VISIBLE);
        ivScanPreview.setImageBitmap(bitmap);

        if (!NetworkMonitor.isOnline(this)) {
            cardProcessing.setVisibility(View.GONE);
            new AlertDialog.Builder(this)
                    .setTitle("📡 Offline Mode")
                    .setMessage("Food recognition requires an internet connection.")
                    .setPositiveButton("Try Again When Online", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("Enter Food Manually", (dialog, which) -> {
                        startActivity(new Intent(FoodScanActivity.this, NutritionActivity.class));
                    })
                    .setCancelable(false)
                    .show();
            return;
        }

        cardProcessing.setVisibility(View.VISIBLE);

        Handler handler = new Handler(Looper.getMainLooper());
        tvProcessingStep.setText("1. Image Quality & Lighting Check...");

        handler.postDelayed(() -> {
            tvProcessingStep.setText("2. Food vs. Non-Food Classification...");
            handler.postDelayed(() -> {
                tvProcessingStep.setText("3. Detecting Multiple Foods & Portion Estimation...");
                handler.postDelayed(() -> {
                    cardProcessing.setVisibility(View.GONE);
                    runFoodAnalysis(bitmap);
                }, 400);
            }, 400);
        }, 400);
    }

    private void runFoodAnalysis(Bitmap bitmap) {
        FoodRecognitionEngine.AnalysisResult result = FoodRecognitionEngine.analyzeFoodImage(bitmap);

        if (result.status != FoodRecognitionEngine.Status.SUCCESS || !result.isFoodDetected) {
            showRejectionDialog(result);
            return;
        }

        // Confidence validation check
        if (result.confidencePercent < 85) {
            showMediumConfidenceDialog(result);
        } else {
            navigateToResults(result);
        }
    }

    private void showRejectionDialog(FoodRecognitionEngine.AnalysisResult result) {
        new AlertDialog.Builder(this)
                .setTitle("❌ " + result.messageTitle)
                .setMessage(result.messageBody + "\n\nTip: Make sure the photograph clearly shows food items under adequate light.")
                .setPositiveButton("Try Again", (dialog, which) -> checkCameraPermissionAndLaunch())
                .setNeutralButton("Choose Another Photo", (dialog, which) -> galleryLauncher.launch("image/*"))
                .setNegativeButton("Enter Manually", (dialog, which) -> startActivity(new Intent(FoodScanActivity.this, NutritionActivity.class)))
                .setCancelable(false)
                .show();
    }

    private void showMediumConfidenceDialog(FoodRecognitionEngine.AnalysisResult result) {
        StringBuilder foodsStr = new StringBuilder();
        for (int i = 0; i < result.detectedFoods.size(); i++) {
            foodsStr.append("• ").append(result.detectedFoods.get(i).foodName);
            if (i < result.detectedFoods.size() - 1) foodsStr.append("\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("🔍 Possible Food Detected (" + result.confidencePercent + "% confidence)")
                .setMessage("Detected Foods:\n" + foodsStr + "\n\nDoes this look correct?")
                .setPositiveButton("✓ Yes, Continue", (dialog, which) -> navigateToResults(result))
                .setNeutralButton("✏️ Edit Food", (dialog, which) -> navigateToResults(result))
                .setNegativeButton("↻ Scan Again", (dialog, which) -> checkCameraPermissionAndLaunch())
                .setCancelable(false)
                .show();
    }

    private void navigateToResults(FoodRecognitionEngine.AnalysisResult result) {
        Intent intent = new Intent(this, FoodScanResultActivity.class);
        intent.putExtra("photoPath", currentPhotoPath != null ? currentPhotoPath : "");
        FoodScanResultActivity.currentAnalysisResult = result;
        startActivity(intent);
    }
}
