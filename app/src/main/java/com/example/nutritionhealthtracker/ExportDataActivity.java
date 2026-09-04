package com.example.nutritionhealthtracker;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.nutritionhealthtracker.models.BMIRecord;
import com.example.nutritionhealthtracker.models.ExerciseRecord;
import com.example.nutritionhealthtracker.models.NutritionRecord;
import com.example.nutritionhealthtracker.models.SleepRecord;
import com.example.nutritionhealthtracker.models.WaterRecord;
import com.example.nutritionhealthtracker.models.WeightRecord;
import com.example.nutritionhealthtracker.utils.DataManager;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExportDataActivity extends AppCompatActivity {

    private TextView tvSummaryList;
    private MaterialButton btnExportCSV, btnExportPDF, btnExportWord;
    private Toolbar toolbar;

    // PDF layout constants
    private static final int PAGE_WIDTH  = 595;  // A4 width in pts
    private static final int PAGE_HEIGHT = 842;  // A4 height in pts
    private static final int MARGIN      = 40;
    private static final int LINE_HEIGHT = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_data);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Export My Data");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvSummaryList   = findViewById(R.id.tvExportSummaryList);
        btnExportCSV    = findViewById(R.id.btnExportCSV);
        btnExportPDF    = findViewById(R.id.btnExportPDF);
        btnExportWord   = findViewById(R.id.btnExportWord);

        loadRecordCounts();

        btnExportCSV.setOnClickListener(v  -> exportCSV());
        btnExportPDF.setOnClickListener(v  -> exportPDF());
        btnExportWord.setOnClickListener(v -> exportWord());
    }

    // ─────────────────────────────────────────────
    // SUMMARY
    // ─────────────────────────────────────────────
    private void loadRecordCounts() {
        String username = DataManager.getCurrentUsername(this);
        int bmiCount    = DataManager.getBMIHistory(this).size();
        int weightCount = DataManager.getWeightHistory(this).size();
        int nutCount    = DataManager.getNutritionHistory(this).size();
        int waterCount  = DataManager.getWaterHistory(this).size();
        int exCount     = DataManager.getExerciseHistory(this).size();
        int sleepCount  = DataManager.getSleepHistory(this).size();

        String summary = String.format(Locale.US,
                "• Active Account: %s\n• BMI Records: %d\n• Weight Records: %d\n" +
                "• Nutrition Logs: %d\n• Water Records: %d\n• Exercise Logs: %d\n• Sleep Records: %d",
                username, bmiCount, weightCount, nutCount, waterCount, exCount, sleepCount);
        tvSummaryList.setText(summary);
    }

    // ─────────────────────────────────────────────
    // CSV EXPORT
    // ─────────────────────────────────────────────
    private void exportCSV() {
        String username = DataManager.getCurrentUsername(this);
        String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        StringBuilder csv = new StringBuilder();

        csv.append("=== NUTRITION & HEALTH TRACKER EXPORT ===\n");
        csv.append("User,").append(username).append("\n");
        csv.append("Export Date,").append(todayStr).append("\n\n");

        csv.append("--- USER PROFILE ---\n");
        csv.append("Name,Age,Gender,Height (cm),Weight (kg)\n");
        csv.append(esc(DataManager.getProfileName(this))).append(",")
           .append(DataManager.getProfileAge(this)).append(",")
           .append(esc(DataManager.getProfileGender(this))).append(",")
           .append(DataManager.getProfileHeight(this)).append(",")
           .append(DataManager.getProfileWeight(this)).append("\n\n");

        csv.append("--- WEIGHT HISTORY ---\n");
        csv.append("Date,Time,Weight (kg)\n");
        for (WeightRecord r : DataManager.getWeightHistory(this)) {
            csv.append(esc(r.getDate())).append(",").append(esc(r.getTime())).append(",").append(r.getWeightKg()).append("\n");
        }
        csv.append("\n");

        csv.append("--- BMI HISTORY ---\n");
        csv.append("Date,Height (cm),Weight (kg),BMI Score,Category\n");
        for (BMIRecord r : DataManager.getBMIHistory(this)) {
            csv.append(esc(r.getDate())).append(",").append(r.getHeight()).append(",")
               .append(r.getWeight()).append(",").append(r.getBmi()).append(",")
               .append(esc(r.getCategory())).append("\n");
        }
        csv.append("\n");

        csv.append("--- WATER HISTORY ---\n");
        csv.append("Date,Consumed (ml),Target (ml)\n");
        for (WaterRecord r : DataManager.getWaterHistory(this)) {
            csv.append(esc(r.getDate())).append(",").append(r.getConsumedMl()).append(",").append(r.getTargetMl()).append("\n");
        }
        csv.append("\n");

        csv.append("--- NUTRITION HISTORY ---\n");
        csv.append("Date,Breakfast,Lunch,Dinner,Snacks,Total Calories\n");
        for (NutritionRecord r : DataManager.getNutritionHistory(this)) {
            csv.append(esc(r.getDate())).append(",").append(esc(r.getBreakfast())).append(",")
               .append(esc(r.getLunch())).append(",").append(esc(r.getDinner())).append(",")
               .append(esc(r.getSnacks())).append(",").append(r.getCalories()).append("\n");
        }
        csv.append("\n");

        csv.append("--- EXERCISE HISTORY ---\n");
        csv.append("Date,Time,Exercise Type,Duration (mins)\n");
        for (ExerciseRecord r : DataManager.getExerciseHistory(this)) {
            csv.append(esc(r.getDate())).append(",").append(esc(r.getTime())).append(",")
               .append(esc(r.getExerciseType())).append(",").append(r.getDurationMinutes()).append("\n");
        }
        csv.append("\n");

        csv.append("--- SLEEP HISTORY ---\n");
        csv.append("Date,Sleep Time,Wake Time,Duration (hrs)\n");
        for (SleepRecord r : DataManager.getSleepHistory(this)) {
            csv.append(esc(r.getDate())).append(",").append(esc(r.getSleepTime())).append(",")
               .append(esc(r.getWakeTime())).append(",").append(r.getDurationHours()).append("\n");
        }

        shareFile("NutritionHealthTracker_" + username + "_" + todayStr + ".csv",
                csv.toString().getBytes(), "text/csv", "Save / Share CSV Export");
    }

    // ─────────────────────────────────────────────
    // PDF EXPORT  (Android PdfDocument — no library)
    // ─────────────────────────────────────────────
    private void exportPDF() {
        String username = DataManager.getCurrentUsername(this);
        String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        PdfDocument pdf = new PdfDocument();

        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.parseColor("#0F766E")); // teal
        titlePaint.setTextSize(16);
        titlePaint.setFakeBoldText(true);

        Paint headPaint = new Paint();
        headPaint.setColor(Color.parseColor("#1E293B"));
        headPaint.setTextSize(13);
        headPaint.setFakeBoldText(true);

        Paint bodyPaint = new Paint();
        bodyPaint.setColor(Color.parseColor("#374151"));
        bodyPaint.setTextSize(11);

        Paint divPaint = new Paint();
        divPaint.setColor(Color.parseColor("#CBD5E1"));
        divPaint.setStrokeWidth(1);

        // Collect all lines to render
        java.util.ArrayList<Object[]> lines = new java.util.ArrayList<>(); // {Paint, String}

        lines.add(new Object[]{titlePaint, "NUTRITION & HEALTH TRACKER — DATA EXPORT"});
        lines.add(new Object[]{bodyPaint,  "User: " + username + "   |   Export Date: " + todayStr});
        lines.add(new Object[]{null,        null}); // divider
        lines.add(new Object[]{headPaint,  "USER PROFILE"});
        lines.add(new Object[]{bodyPaint,  "Name: " + DataManager.getProfileName(this)});
        lines.add(new Object[]{bodyPaint,  "Age: " + DataManager.getProfileAge(this) + "   Gender: " + DataManager.getProfileGender(this)});
        lines.add(new Object[]{bodyPaint,  "Height: " + DataManager.getProfileHeight(this) + " cm   Weight: " + DataManager.getProfileWeight(this) + " kg"});
        lines.add(new Object[]{null, null});

        lines.add(new Object[]{headPaint, "WEIGHT HISTORY"});
        for (WeightRecord r : DataManager.getWeightHistory(this)) {
            lines.add(new Object[]{bodyPaint, r.getDate() + " " + r.getTime() + "  →  " + r.getWeightKg() + " kg"});
        }
        lines.add(new Object[]{null, null});

        lines.add(new Object[]{headPaint, "BMI HISTORY"});
        for (BMIRecord r : DataManager.getBMIHistory(this)) {
            lines.add(new Object[]{bodyPaint, r.getDate() + "  BMI: " + String.format(Locale.US, "%.1f", r.getBmi()) + "  (" + r.getCategory() + ")  H:" + r.getHeight() + " W:" + r.getWeight()});
        }
        lines.add(new Object[]{null, null});

        lines.add(new Object[]{headPaint, "WATER HISTORY"});
        for (WaterRecord r : DataManager.getWaterHistory(this)) {
            lines.add(new Object[]{bodyPaint, r.getDate() + "  Consumed: " + r.getConsumedMl() + " ml  /  Target: " + r.getTargetMl() + " ml"});
        }
        lines.add(new Object[]{null, null});

        lines.add(new Object[]{headPaint, "NUTRITION HISTORY"});
        for (NutritionRecord r : DataManager.getNutritionHistory(this)) {
            lines.add(new Object[]{bodyPaint, r.getDate() + "  Calories: " + r.getCalories()});
            if (r.getBreakfast() != null && !r.getBreakfast().isEmpty())
                lines.add(new Object[]{bodyPaint, "   Breakfast: " + r.getBreakfast()});
            if (r.getLunch() != null && !r.getLunch().isEmpty())
                lines.add(new Object[]{bodyPaint, "   Lunch: " + r.getLunch()});
            if (r.getDinner() != null && !r.getDinner().isEmpty())
                lines.add(new Object[]{bodyPaint, "   Dinner: " + r.getDinner()});
        }
        lines.add(new Object[]{null, null});

        lines.add(new Object[]{headPaint, "EXERCISE HISTORY"});
        for (ExerciseRecord r : DataManager.getExerciseHistory(this)) {
            lines.add(new Object[]{bodyPaint, r.getDate() + " " + r.getTime() + "  " + r.getExerciseType() + "  " + r.getDurationMinutes() + " mins"});
        }
        lines.add(new Object[]{null, null});

        lines.add(new Object[]{headPaint, "SLEEP HISTORY"});
        for (SleepRecord r : DataManager.getSleepHistory(this)) {
            lines.add(new Object[]{bodyPaint, r.getDate() + "  Sleep: " + r.getSleepTime() + "  Wake: " + r.getWakeTime() + "  Duration: " + r.getDurationHours() + " hrs"});
        }

        // Paginate and draw
        int pageNum = 1;
        int y = MARGIN + 20;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        for (Object[] line : lines) {
            if (y > PAGE_HEIGHT - MARGIN) {
                pdf.finishPage(page);
                pageNum++;
                pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create();
                page = pdf.startPage(pageInfo);
                canvas = page.getCanvas();
                y = MARGIN + 20;
            }
            if (line[0] == null) {
                // divider line
                canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, divPaint);
                y += LINE_HEIGHT;
            } else {
                Paint p = (Paint) line[0];
                String text = (String) line[1];
                canvas.drawText(text != null ? text : "", MARGIN, y, p);
                y += LINE_HEIGHT + (p == titlePaint ? 4 : 0) + (p == headPaint ? 2 : 0);
            }
        }
        pdf.finishPage(page);

        try {
            String fileName = "NutritionHealthTracker_" + username + "_" + todayStr + ".pdf";
            File file = new File(getCacheDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            pdf.writeTo(fos);
            fos.close();
            pdf.close();

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Health Data PDF — " + username);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Save / Share PDF Export"));
            Toast.makeText(this, "PDF Export generated!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate PDF.", Toast.LENGTH_SHORT).show();
        }
    }

    // ─────────────────────────────────────────────
    // WORD EXPORT  (RTF format — opens in MS Word / WPS / Google Docs)
    // ─────────────────────────────────────────────
    private void exportWord() {
        String username = DataManager.getCurrentUsername(this);
        String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        StringBuilder rtf = new StringBuilder();

        // RTF header
        rtf.append("{\\rtf1\\ansi\\deff0\n");
        rtf.append("{\\fonttbl{\\f0 Arial;}}\n");
        rtf.append("{\\colortbl;\\red15\\green118\\blue110;\\red30\\blue51\\red30\\green41\\blue59;\\red55\\green65\\blue81;}\n");

        // Title
        rtf.append("\\f0\\fs32\\b\\cf1 NUTRITION & HEALTH TRACKER — DATA EXPORT\\b0\\cf0\\fs22\\par\n");
        rtf.append("\\fs20 User: ").append(rtfEsc(username))
           .append("   |   Export Date: ").append(todayStr).append("\\par\n");
        rtf.append("\\par\\pard\\brdrb\\brdrs\\brdrw10\\brdr\\par\n");

        // Profile
        rtf.append("\\b\\fs24 USER PROFILE\\b0\\fs20\\par\n");
        rtf.append("Name: ").append(rtfEsc(DataManager.getProfileName(this))).append("\\par\n");
        rtf.append("Age: ").append(DataManager.getProfileAge(this))
           .append("  |  Gender: ").append(rtfEsc(DataManager.getProfileGender(this))).append("\\par\n");
        rtf.append("Height: ").append(DataManager.getProfileHeight(this))
           .append(" cm  |  Weight: ").append(DataManager.getProfileWeight(this)).append(" kg\\par\\par\n");

        // Weight
        rtf.append("\\b\\fs24 WEIGHT HISTORY\\b0\\fs20\\par\n");
        for (WeightRecord r : DataManager.getWeightHistory(this)) {
            rtf.append(rtfEsc(r.getDate())).append("  ").append(rtfEsc(r.getTime()))
               .append("  \\u8594? ").append(r.getWeightKg()).append(" kg\\par\n");
        }
        rtf.append("\\par\n");

        // BMI
        rtf.append("\\b\\fs24 BMI HISTORY\\b0\\fs20\\par\n");
        for (BMIRecord r : DataManager.getBMIHistory(this)) {
            rtf.append(rtfEsc(r.getDate())).append("  BMI: ").append(String.format(Locale.US, "%.1f", r.getBmi()))
               .append(" (").append(rtfEsc(r.getCategory())).append(")  H:").append(r.getHeight())
               .append(" cm  W:").append(r.getWeight()).append(" kg\\par\n");
        }
        rtf.append("\\par\n");

        // Water
        rtf.append("\\b\\fs24 WATER HISTORY\\b0\\fs20\\par\n");
        for (WaterRecord r : DataManager.getWaterHistory(this)) {
            rtf.append(rtfEsc(r.getDate())).append("  Consumed: ").append(r.getConsumedMl())
               .append(" ml  /  Target: ").append(r.getTargetMl()).append(" ml\\par\n");
        }
        rtf.append("\\par\n");

        // Nutrition
        rtf.append("\\b\\fs24 NUTRITION HISTORY\\b0\\fs20\\par\n");
        for (NutritionRecord r : DataManager.getNutritionHistory(this)) {
            rtf.append("\\b ").append(rtfEsc(r.getDate())).append("\\b0  Calories: ").append(r.getCalories()).append("\\par\n");
            if (r.getBreakfast() != null && !r.getBreakfast().isEmpty())
                rtf.append("  Breakfast: ").append(rtfEsc(r.getBreakfast())).append("\\par\n");
            if (r.getLunch() != null && !r.getLunch().isEmpty())
                rtf.append("  Lunch: ").append(rtfEsc(r.getLunch())).append("\\par\n");
            if (r.getDinner() != null && !r.getDinner().isEmpty())
                rtf.append("  Dinner: ").append(rtfEsc(r.getDinner())).append("\\par\n");
            if (r.getSnacks() != null && !r.getSnacks().isEmpty())
                rtf.append("  Snacks: ").append(rtfEsc(r.getSnacks())).append("\\par\n");
        }
        rtf.append("\\par\n");

        // Exercise
        rtf.append("\\b\\fs24 EXERCISE HISTORY\\b0\\fs20\\par\n");
        for (ExerciseRecord r : DataManager.getExerciseHistory(this)) {
            rtf.append(rtfEsc(r.getDate())).append("  ").append(rtfEsc(r.getTime()))
               .append("  ").append(rtfEsc(r.getExerciseType())).append("  ")
               .append(r.getDurationMinutes()).append(" mins\\par\n");
        }
        rtf.append("\\par\n");

        // Sleep
        rtf.append("\\b\\fs24 SLEEP HISTORY\\b0\\fs20\\par\n");
        for (SleepRecord r : DataManager.getSleepHistory(this)) {
            rtf.append(rtfEsc(r.getDate())).append("  Sleep: ").append(rtfEsc(r.getSleepTime()))
               .append("  Wake: ").append(rtfEsc(r.getWakeTime()))
               .append("  Duration: ").append(r.getDurationHours()).append(" hrs\\par\n");
        }

        rtf.append("}"); // close RTF

        shareFile("NutritionHealthTracker_" + username + "_" + todayStr + ".doc",
                rtf.toString().getBytes(), "application/msword", "Save / Share Word Export");
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────
    private void shareFile(String fileName, byte[] data, String mimeType, String chooserTitle) {
        try {
            File file = new File(getCacheDir(), fileName);
            FileOutputStream out = new FileOutputStream(file);
            out.write(data);
            out.close();

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Health Data Export");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, chooserTitle));
            Toast.makeText(this, fileName + " generated!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate export file.", Toast.LENGTH_SHORT).show();
        }
    }

    /** Escapes a value for CSV */
    private String esc(String val) {
        if (val == null) return "\"\"";
        return "\"" + val.replace("\"", "\"\"") + "\"";
    }

    /** Escapes special chars for RTF */
    private String rtfEsc(String val) {
        if (val == null) return "";
        return val.replace("\\", "\\\\").replace("{", "\\{").replace("}", "\\}").replace("\n", "\\par\n");
    }
}
