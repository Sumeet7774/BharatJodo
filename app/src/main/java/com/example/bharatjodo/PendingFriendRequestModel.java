package com.example.bharatjodo;

public class PendingFriendRequestModel {
    private String username;
    private String phoneNumber;

    public PendingFriendRequestModel(String username, String phoneNumber) {
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public String getUsernameofFriend() {
        return username;
    }

    public String getPhoneNumberofFriend() {
        return phoneNumber;
    }
}
