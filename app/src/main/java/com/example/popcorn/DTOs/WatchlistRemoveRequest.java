package com.example.popcorn.DTOs;

public class WatchlistRemoveRequest {
    private String userId;
    private int movieId;

    public WatchlistRemoveRequest(String userId, int movieId) {
        this.userId = userId;
        this.movieId = movieId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
}
