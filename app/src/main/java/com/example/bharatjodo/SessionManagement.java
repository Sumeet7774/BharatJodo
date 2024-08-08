package com.example.bharatjodo;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    private SharedPreferences prefs;

    public SessionManagement(Context context)
    {
        prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    public String getUserId()
    {
        return prefs.getString("user_id", "");
    }

    public String getUsername()
    {
        return prefs.getString("username", "");
    }

    public String getEmailId()
    {
        return prefs.getString("email_id", "");
    }

    public String getPhoneNumber()
    {
        return prefs.getString("phone_number", "");
    }

    public void setUserId(String userId)
    {
        prefs.edit().putString("user_id", userId).apply();
    }

    public void setUsername(String Username)
    {
        prefs.edit().putString("username", Username).apply();
    }

    public void setEmailId(String emailId)
    {
        prefs.edit().putString("email_id", emailId).apply();
    }

    public void setPhoneNumber(String phoneNumber)
    {
        prefs.edit().putString("phone_number", phoneNumber).apply();
    }

    public void logout()
    {
        prefs.edit().remove("user_id").apply();
        prefs.edit().remove("username").apply();
        prefs.edit().remove("email_id").apply();
        prefs.edit().remove("phone_number").apply();
    }
}
