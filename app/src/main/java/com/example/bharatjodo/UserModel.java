package com.example.bharatjodo;

public class UserModel {
    private String userId;
    private String username;
    private String phonenumber;

    public UserModel(String userId, String username, String phonenumber) {
        this.userId = userId;
        this.username = username;
        this.phonenumber = phonenumber;
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

}
