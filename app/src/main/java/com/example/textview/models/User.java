package com.example.textview.models;

import java.util.Locale;

public class User {
    private String fullName;
    private String email;
    private String password;

    public User() {
    }

    public User(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        if (fullName != null && !fullName.isEmpty()) {
            return fullName.split(" ")[0];
        }
        return "Estudante";
    }

    public String getInitial() {
        if (fullName != null && !fullName.isEmpty()) {
            return String.valueOf(fullName.charAt(0)).toUpperCase(Locale.ROOT);
        } else if (email != null && !email.isEmpty()) {
            return String.valueOf(email.charAt(0)).toUpperCase(Locale.ROOT);
        }
        return "E";
    }
}
