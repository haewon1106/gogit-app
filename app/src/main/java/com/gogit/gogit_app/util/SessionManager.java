package com.gogit.gogit_app.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    public static final String PREF_NAME = "LoginPrefs";
    public static final String KEY_USERID = "userid";
    public static final String KEY_TOKEN = "usertoken";
    public static final String KEY_PK = "userpk";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveLoginDetails(String userId, String token) {
        editor.putString(KEY_USERID, userId);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public void setUserPk(Long pk) {
        editor.putLong(KEY_PK, pk);
        editor.apply();
    }


    public String getUserId() {
        return pref.getString(KEY_USERID, null);
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public Long getPk() {
        return pref.getLong(KEY_PK, 1);
    }

    public void clearLoginDetails() {
        editor.remove(KEY_USERID);
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_PK);
        editor.apply();
    }

}
