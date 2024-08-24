package com.example.bharatjodo;

public class UserModel {
    private String userId;
    private String username;
    private String phonenumber;
    private String friendshipStatus;

    public UserModel(String userId, String username, String phonenumber, String friendshipStatus) {
        this.userId = userId;
        this.username = username;
        this.phonenumber = phonenumber;
        this.friendshipStatus = friendshipStatus;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phonenumber;
    }

    public void setPhoneNumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getFriendshipStatus() {
        return friendshipStatus;
    }

    public void setFriendshipStatus(String friendshipStatus) {
        this.friendshipStatus = friendshipStatus;
    }
}
