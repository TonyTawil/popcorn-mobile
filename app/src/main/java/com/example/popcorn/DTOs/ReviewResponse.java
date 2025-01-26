package com.example.popcorn.DTOs;

import java.util.Date;

public class ReviewResponse {
    private int movieId;
    private String userId;
    private float rating;
    private String _id;
    private Date createdAt;
    private int __v;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
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
