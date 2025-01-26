package com.example.popcorn.Models;

public class User {
    String firstName;
    String lastName;
    String username;
    String email;
    String password;
    String confirmPassword;
    String gender;
    Boolean isGoogleAccount;

    public User(String firstName, String lastName, String username, String email, String password, String confirmPassword, String gender, boolean isGoogleAccount) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.gender = gender;
        this.isGoogleAccount = isGoogleAccount;
    }

}
