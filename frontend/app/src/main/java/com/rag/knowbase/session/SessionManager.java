package com.rag.knowbase.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.rag.knowbase.data.dto.UserResponseDto;

public class SessionManager {

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USER  = "logged_user";

    private final SharedPreferences prefs;
    private final Gson gson;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson  = new Gson();
    }


    public void saveUser(UserResponseDto user) {
        String json = gson.toJson(user); //serialization
        prefs.edit().putString(KEY_USER, json).apply();
    }


    public UserResponseDto getUser() {
        String json = prefs.getString(KEY_USER, null);
        if (json == null) return null;
        return gson.fromJson(json, UserResponseDto.class);
    }


    public String getToken() {
        UserResponseDto user = getUser();
        return user != null ? user.getToken() : null;
    }


    public boolean isLoggedIn() {
        return getUser() != null && getUser().getAuthenticated();
    }


    public void logout() {
        prefs.edit().remove(KEY_USER).apply();
    }
}
