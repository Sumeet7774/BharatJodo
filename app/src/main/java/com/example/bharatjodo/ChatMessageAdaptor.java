package com.example.bharatjodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdaptor extends RecyclerView.Adapter<ChatMessageAdaptor.ChatMessageViewHolder> {

    private List<ChatMessageModel> messageList;

    public ChatMessageAdaptor(List<ChatMessageModel> messageList) {
        this.messageList = messageList;
    }

    @Override
    public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_message_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_message_item, parent, false);
        }
        return new ChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatMessageViewHolder holder, int position) {
        ChatMessageModel chatMessage = messageList.get(position);
        holder.message_textview.setText(chatMessage.getMessageContent());
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).isSender() ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void addMessage(ChatMessageModel chatMessage) {
        messageList.add(chatMessage);
        notifyItemInserted(messageList.size() - 1);
    }

    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        TextView message_textview;

        public ChatMessageViewHolder(View itemView) {
            super(itemView);
            message_textview = itemView.findViewById(R.id.messageTextView);
        }
    }
}

