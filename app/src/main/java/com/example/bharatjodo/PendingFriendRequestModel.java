package com.example.bharatjodo;

public class PendingFriendRequestModel {
    private String username;
    private String phoneNumber;
    private int friendshipId;

    public PendingFriendRequestModel(String username, String phoneNumber, int friendshipId) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.friendshipId = friendshipId;
    }

    public String getUsernameofFriend() {
        return username;
    }

    public String getPhoneNumberofFriend() {
        return phoneNumber;
    }

    public int getFriendshipId() {
        return friendshipId;
    }
}
