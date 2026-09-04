package com.example.nutritionhealthtracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.adapters.ChatMessageAdapter;
import com.example.nutritionhealthtracker.models.ChatMessage;
import com.example.nutritionhealthtracker.utils.AIHealthAssistantEngine;
import com.example.nutritionhealthtracker.utils.NetworkMonitor;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AIHealthAssistantActivity extends AppCompatActivity {

    private RecyclerView rvChatMessages;
    private TextInputEditText etChatMessage;
    private MaterialButton btnSendMessage;
    private Toolbar toolbar;

    private ChatMessageAdapter chatAdapter;
    private final List<ChatMessage> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_health_assistant);

        View rootAssistant = findViewById(R.id.rootAssistant);
        if (rootAssistant != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootAssistant, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout() | WindowInsetsCompat.Type.ime());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }

        toolbar = findViewById(R.id.toolbarAssistant);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        rvChatMessages = findViewById(R.id.rvChatMessages);
        etChatMessage = findViewById(R.id.etChatMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChatMessages.setLayoutManager(layoutManager);

        chatAdapter = new ChatMessageAdapter(messageList);
        rvChatMessages.setAdapter(chatAdapter);

        setupSuggestionChips();

        btnSendMessage.setOnClickListener(v -> sendUserMessage(etChatMessage.getText() != null ? etChatMessage.getText().toString() : ""));

        // Initial welcome greeting from AI Health Assistant
        sendWelcomeGreeting();
    }

    private void setupSuggestionChips() {
        bindChip(R.id.chipSuggestEatToday, "What should I eat today?");
        bindChip(R.id.chipSuggestProtein, "How can I increase my protein intake?");
        bindChip(R.id.chipSuggestExplain, "Explain my nutrition today");
        bindChip(R.id.chipSuggestDinner, "Suggest a healthy dinner");
        bindChip(R.id.chipSuggestCalories, "What foods are high in calories?");
        bindChip(R.id.chipSuggestLimit, "What foods should I limit?");
    }

    private void bindChip(int chipId, String promptText) {
        Chip chip = findViewById(chipId);
        if (chip != null) {
            chip.setOnClickListener(v -> sendUserMessage(promptText));
        }
    }

    private void sendWelcomeGreeting() {
        AIHealthAssistantEngine.ContextSummary ctx = AIHealthAssistantEngine.getUserContext(this);
        String welcome = "👋 Hello " + ctx.userName + "! I'm your **AI Health & Nutrition Assistant**.\n\n" +
                "I have synced with your profile and logs:\n" +
                "• **Goal**: " + ctx.dietGoal + " (" + ctx.dietaryType + ")\n" +
                "• **Calorie Target**: " + ctx.targetCalories + " kcal/day\n" +
                "• **Protein Target**: ~" + ctx.targetProteinGrams + "g/day\n\n" +
                "Ask me anything about your nutrition, meals, substitutes, workouts, or daily progress!";
        
        addMessage(welcome, false);
    }

    private void sendUserMessage(String text) {
        if (TextUtils.isEmpty(text) || text.trim().isEmpty()) return;
        String query = text.trim();

        addMessage(query, true);
        etChatMessage.setText("");

        if (!NetworkMonitor.isOnline(this)) {
            addMessage("🔴 AI Health Assistant is currently unavailable offline. Your saved health data is still available.", false);
            return;
        }

        // Generate response from AI Health Assistant Engine
        String aiResponse = AIHealthAssistantEngine.generateResponse(this, query);
        addMessage(aiResponse, false);
    }

    private void addMessage(String text, boolean isUser) {
        String time = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
        messageList.add(new ChatMessage(text, isUser, time));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChatMessages.smoothScrollToPosition(messageList.size() - 1);
    }
}
