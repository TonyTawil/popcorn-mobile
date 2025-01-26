package com.example.popcorn.Models;

import java.util.Date;
import java.util.List;

public class Review {
    private String _id;
    private int movieId;
    private User2 userId;
    private float rating;
    private String reviewText;
    private List<String> likes;
    private int likesCount;
    private List<Reply> replies;
    private Date createdAt;
    private int __v;

    public Review(String _id, int movieId, User2 userId, float rating, String reviewText, List<String> likes, int likesCount, List<Reply> replies, Date createdAt, int __v) {
        this._id = _id;
        this.movieId = movieId;
        this.userId = userId;
        this.rating = rating;
        this.reviewText = reviewText;
        this.likes = likes;
        this.likesCount = likesCount;
        this.replies = replies;
        this.createdAt = createdAt;
        this.__v = __v;
    }

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

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getV() {
        return __v;
    }

    public void setV(int __v) {
        this.__v = __v;
    }
}
