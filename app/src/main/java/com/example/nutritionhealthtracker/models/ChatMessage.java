package com.example.nutritionhealthtracker.models;

public class ChatMessage {
    private String text;
    private boolean isUser;
    private String timestamp;

    public ChatMessage(String text, boolean isUser, String timestamp) {
        this.text = text;
        this.isUser = isUser;
        this.timestamp = timestamp;
    }

    public String getText() { return text; }
    public boolean isUser() { return isUser; }
    public String getTimestamp() { return timestamp; }
}
