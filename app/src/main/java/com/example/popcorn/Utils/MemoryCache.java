package com.example.popcorn.Utils;

import com.example.popcorn.Models.Movie;

import java.util.HashMap;
import java.util.List;

public class MemoryCache {
    private static HashMap<String, List<Movie>> cache = new HashMap<>();

    public static List<Movie> getMovies(String key) {
        return cache.get(key);
    }

    public static void putMovies(String key, List<Movie> movies) {
        cache.put(key, movies);
    }

    public static boolean hasKey(String key) {
        return cache.containsKey(key);
    }

    public static void clear() {
        cache.clear();
    }
}
