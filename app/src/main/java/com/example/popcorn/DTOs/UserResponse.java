package com.example.popcorn.DTOs;

public class UserResponse {
    private String _id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String gender;

    public UserResponse(String _id, String firstName, String lastName, String username, String email, String gender) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.gender = gender;
    }

    public String getId() {
        return _id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    // Setters
    public void setId(String _id) {
        this._id = _id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
