package com.example.popcorn.DTOs;

import java.util.List;

public class WatchlistResponse {
    private List<Movie> watchList;

    public List<Movie> getWatchList() {
        return watchList;
    }

    public static class Movie {
        private int movieId;
        private String title;
        private String coverImage;

        public int getMovieId() {
            return movieId;
        }

        public String getTitle() {
            return title;
        }

        public String getCoverImage() {
            return coverImage;
        }
    }
}