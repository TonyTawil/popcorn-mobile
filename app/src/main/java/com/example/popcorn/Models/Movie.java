package com.example.popcorn.Models;

import com.example.popcorn.Models.Person;

import java.util.List;

public class Movie {
    private int id;
    private String title;
    private String poster_path;
    private String plot;
    private List<Person> cast;
    private List<Person> crew;

    private static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500";

    public Movie(int id, String title, String poster_path, String plot, List<Person> cast, List<Person> crew) {
        this.id = id;
        this.title = title;
        setPosterPath(poster_path);
        this.plot = plot;
        this.cast = cast;
        this.crew = crew;
    }

    public Movie() {}

    public void setMovieId(int movieId) {
        this.id = movieId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosterPath(String poster_path) {
        if (poster_path != null && !poster_path.isEmpty()) {
            this.poster_path = BASE_IMAGE_URL + poster_path;
        } else {
            this.poster_path = null;
        }
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public void setCast(List<Person> cast) {
        this.cast = cast;
    }

    public void setCrew(List<Person> crew) {
        this.crew = crew;
    }

    public int getMovieId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public String getPlot() {
        return plot;
    }

    public List<Person> getCast() {
        return cast;
    }

    public List<Person> getCrew() {
        return crew;
    }
}
