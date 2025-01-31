package com.example.popcorn.DTOs;

import com.example.popcorn.Models.User2;
import java.util.Date;

public class ReviewResponse {
    private String _id;
    private int movieId;
    private User2 userId;
    private float rating;
    private String reviewText;
    private Date createdAt;
    private int __v;

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public User2 getUserId() {
        return userId;
    }

    public void setUserId(User2 userId) {
        this.userId = userId;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getVersion() {
        return __v;
    }

    public void setVersion(int __v) {
        this.__v = __v;
    }
}
