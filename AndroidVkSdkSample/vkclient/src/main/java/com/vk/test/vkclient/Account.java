package com.vk.test.vkclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class Account {

    private static Account sAccount;
    private String accessToken;
    private long userId;

    public Account(Context context) {
        restore(context);
    }

    public static Account get(Context context) {
        if (sAccount == null) {
            sAccount = new Account(context);
        }
        return sAccount;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getUserId() {
        return userId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void save(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = prefs.edit();
        editor.putString("access_token", accessToken);
        editor.putLong("user_id", userId);
        editor.commit();
    }
    
    public void restore(Context context) {
        SharedPreferences prefs = null;

        if (context != null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            Log.w("START", "NULL!!!");
        }
        accessToken = prefs.getString("access_token", null);
        userId = prefs.getLong("user_id", 0);
    }
}
