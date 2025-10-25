package com.example.textview.models;

public class User {
    private String fullName;
    private String email;

    public User() {
    }

    public User(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
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

    public String getFirstName() {
        if (fullName != null && !fullName.isEmpty()) {
            return fullName.split(" ")[0];
        }
        return "Estudante";
    }

    public String getInitial() {
        if (fullName != null && !fullName.isEmpty()) {
            return String.valueOf(fullName.charAt(0)).toUpperCase();
        } else if (email != null && !email.isEmpty()) {
            return String.valueOf(email.charAt(0)).toUpperCase();
        }
        return "E";
    }
}
