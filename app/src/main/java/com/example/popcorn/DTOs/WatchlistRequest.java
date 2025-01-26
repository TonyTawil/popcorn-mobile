package com.example.popcorn.DTOs;

public class WatchlistRequest {
    private String userId;

    public WatchlistRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}