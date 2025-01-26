package com.example.popcorn.Models;

public class CrewMember {
    private String name;
    private String job;
    private String profile_path;

    public String getName() {
        return name;
    }

    public String getJob() {
        return job;
    }

    public String getImageUrl() {
        if (profile_path != null && !profile_path.isEmpty()) {
            return "https://image.tmdb.org/t/p/w500" + profile_path;
        }
        return null;
    }
}