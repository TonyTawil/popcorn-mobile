package com.example.popcorn.DTOs;

import com.example.popcorn.Models.Movie;
import java.util.List;

public class WatchlistAddResponse {
    private String message;
    private List<Movie> watchList;

    public WatchlistAddResponse(String message, List<Movie> watchList) {
        this.message = message;
        this.watchList = watchList;
    }

    public String getMessage() {
        return message;
    }

    public List<Movie> getWatchList() {
        return watchList;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setWatchList(List<Movie> watchList) {
        this.watchList = watchList;
    }
}
