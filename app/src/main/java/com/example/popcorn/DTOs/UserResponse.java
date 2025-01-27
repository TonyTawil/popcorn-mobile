package com.example.popcorn.DTOs;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    private String message;
    private User user;

    public static class User {
        private String id;
        private String email;
        private String username;
        private String name;
        private boolean verified;

        // Getters
        public String getId() { return id; }
        public String getEmail() { return email; }
        public String getUsername() { return username; }
        public String getName() { return name; }
        public boolean isVerified() { return verified; }

        // Setters
        public void setId(String id) { this.id = id; }
        public void setEmail(String email) { this.email = email; }
        public void setUsername(String username) { this.username = username; }
        public void setName(String name) { this.name = name; }
        public void setVerified(boolean verified) { this.verified = verified; }
    }

    // Getters
    public String getMessage() { return message; }
    public User getUser() { return user; }

    // Setters
    public void setMessage(String message) { this.message = message; }
    public void setUser(User user) { this.user = user; }
}
