package com.example.popcorn.Models;

import java.util.Date;
import java.util.List;

public class Reply {
    private String _id;
    private String userId;
    private String replyText;
    private List<String> likes;
    private int likesCount;
    private Date createdAt;

    public Reply(String _id, String userId, String replyText, List<String> likes, int likesCount, Date createdAt) {
        this._id = _id;
        this.userId = userId;
        this.replyText = replyText;
        this.likes = likes;
        this.likesCount = likesCount;
        this.createdAt = createdAt;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
