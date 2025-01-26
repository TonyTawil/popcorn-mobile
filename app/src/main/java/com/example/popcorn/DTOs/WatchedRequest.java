package com.example.popcorn.DTOs;

public class WatchedRequest {
    private String userId;

    public WatchedRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
