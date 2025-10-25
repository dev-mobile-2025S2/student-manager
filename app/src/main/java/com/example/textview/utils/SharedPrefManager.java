package com.example.textview.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.textview.models.User;

public class SharedPrefManager {
    private static final String PREF_NAME = "StudentManagerPrefs";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SharedPrefManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUser(User user) {
        editor.putString(KEY_USER_NAME, user.getFullName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public User getUser() {
        if (isLoggedIn()) {
            String name = sharedPreferences.getString(KEY_USER_NAME, "Estudante");
            String email = sharedPreferences.getString(KEY_USER_EMAIL, "estudante@email.com");
            return new User(name, email);
        }
        return null;
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "Estudante");
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "estudante@email.com");
    }

    public void clearUser() {
        editor.clear();
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
