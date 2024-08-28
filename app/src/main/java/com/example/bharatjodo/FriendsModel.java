package com.example.bharatjodo;

public class FriendsModel {
    private String friendshipId;
    private String username;
    private String phonenumber;

    public FriendsModel(String friendshipId,String username, String phonenumber)
    {
        this.friendshipId = friendshipId;
        this.username = username;
        this.phonenumber = phonenumber;
    }

    public String get_friendshipId()
    {
        return friendshipId;
    }

    public void set_friendshipId()
    {
        this.friendshipId = friendshipId;
    }

    public String get_username()
    {
        return username;
    }

    public void set_username(String username)
    {
        this.username = username;
    }

    public String get_phonenumber()
    {
        return phonenumber;
    }

    public void set_phonenumber(String phonenumber)
    {
        this.phonenumber = phonenumber;
    }
}