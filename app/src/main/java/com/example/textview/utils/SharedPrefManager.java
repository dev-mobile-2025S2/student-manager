package com.example.textview.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.textview.models.User;

public class SharedPrefManager {
    private static final String PREF_NAME = "StudentManagerPrefs";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PASSWORD = "user_password";
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
        if (user.getPassword() != null) {
            editor.putString(KEY_USER_PASSWORD, user.getPassword());
        }
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public void registerUser(User user) {
        editor.putString(KEY_USER_NAME, user.getFullName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        if (user.getPassword() != null) {
            editor.putString(KEY_USER_PASSWORD, user.getPassword());
        }
        // Don't set logged in to true
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }

    public User getUser() {
        if (isLoggedIn()) {
            String name = sharedPreferences.getString(KEY_USER_NAME, "Estudante");
            String email = sharedPreferences.getString(KEY_USER_EMAIL, "estudante@email.com");
            String password = sharedPreferences.getString(KEY_USER_PASSWORD, "");
            User user = new User(name, email);
            user.setPassword(password);
            return user;
        }
        return null;
    }

    public boolean validateLogin(String email, String password) {
        String savedEmail = sharedPreferences.getString(KEY_USER_EMAIL, "");
        String savedPassword = sharedPreferences.getString(KEY_USER_PASSWORD, "");
        return !savedEmail.isEmpty() && email.equals(savedEmail) && password.equals(savedPassword);
    }

    public boolean emailExists(String email) {
        String savedEmail = sharedPreferences.getString(KEY_USER_EMAIL, "");
        return !savedEmail.isEmpty() && savedEmail.equals(email);
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
        // Only use this to completely remove user data (e.g., delete account)
        editor.clear();
        editor.apply();
    }

    public void logout() {
        // Only change logged in status, keep user data saved
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }
}
