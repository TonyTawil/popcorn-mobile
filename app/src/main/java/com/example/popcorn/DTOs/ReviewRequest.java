package com.example.popcorn.DTOs;

public class ReviewRequest {
    private String userId;
    private int movieId;
    private float rating;
    private String reviewText;

    public ReviewRequest(String userId, int movieId, float rating, String reviewText) {
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
        this.reviewText = reviewText;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}
