package com.example.popcorn.Models;

public class CastMember {
    private String name;
    private String character;
    private String profile_path;

    public String getName() {
        return name;
    }

    public String getCharacter() {
        return character;
    }

    public String getImageUrl() {
        if (profile_path != null && !profile_path.isEmpty()) {
            return "https://image.tmdb.org/t/p/w500" + profile_path;
        }
        return null;
    }
}