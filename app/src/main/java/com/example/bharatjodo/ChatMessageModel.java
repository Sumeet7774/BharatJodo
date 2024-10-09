package com.example.bharatjodo;

public class ChatMessageModel {
    private String messageContent;
    private boolean isSender;

    public ChatMessageModel(String messageContent, boolean isSender) {
        this.messageContent = messageContent;
        this.isSender = isSender;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public boolean isSender() {
        return isSender;
    }
}
