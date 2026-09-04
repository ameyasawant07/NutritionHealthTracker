package com.example.nutritionhealthtracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionhealthtracker.R;
import com.example.nutritionhealthtracker.models.ChatMessage;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatViewHolder> {

    private final List<ChatMessage> messageList;

    public ChatMessageAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        if (message.isUser()) {
            holder.layoutUserMessage.setVisibility(View.VISIBLE);
            holder.layoutAssistantMessage.setVisibility(View.GONE);
            holder.tvUserText.setText(message.getText());
            holder.tvUserTime.setText(message.getTimestamp());
        } else {
            holder.layoutAssistantMessage.setVisibility(View.VISIBLE);
            holder.layoutUserMessage.setVisibility(View.GONE);
            holder.tvAssistantText.setText(message.getText());
            holder.tvAssistantTime.setText(message.getTimestamp());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutAssistantMessage, layoutUserMessage;
        TextView tvAssistantText, tvAssistantTime;
        TextView tvUserText, tvUserTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutAssistantMessage = itemView.findViewById(R.id.layoutAssistantMessage);
            layoutUserMessage = itemView.findViewById(R.id.layoutUserMessage);
            tvAssistantText = itemView.findViewById(R.id.tvAssistantText);
            tvAssistantTime = itemView.findViewById(R.id.tvAssistantTime);
            tvUserText = itemView.findViewById(R.id.tvUserText);
            tvUserTime = itemView.findViewById(R.id.tvUserTime);
        }
    }
}
