package com.example.popcorn.DTOs;

import java.util.List;

public class WatchedResponse {
    private List<Movie> watched;

    public List<Movie> getWatched() {
        return watched;
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
