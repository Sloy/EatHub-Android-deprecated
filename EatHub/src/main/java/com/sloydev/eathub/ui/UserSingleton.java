package com.sloydev.eathub.ui;


import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.sloydev.eathub.model.User;
import com.sloydev.eathub.ui.activities.LoginActivity;

public class UserSingleton {

    public static final String AUTHORIZATION_KEY = "auth";

    private User mCurrentUser;
    private String mCurrentAuthorization;

    private static UserSingleton instance;

    public static UserSingleton getUserSingleton() {
        if (instance == null) {
            instance = new UserSingleton();
        }
        return instance;
    }

    public String getStoredAuthorization(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(AUTHORIZATION_KEY, null);

    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public String getCurrentAuthorization() {
        return mCurrentAuthorization;
    }

    public UserSingleton setCurrentUser(User user) {
        mCurrentUser = user;
        return this;
    }

    public UserSingleton setCurrentAuthorization(Context context, String authorization) {
        mCurrentAuthorization = authorization;
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(AUTHORIZATION_KEY, authorization)
        .commit();
        return this;
    }

}
