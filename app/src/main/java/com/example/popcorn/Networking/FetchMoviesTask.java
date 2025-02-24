package com.example.popcorn.Networking;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popcorn.DTOs.CreditsResponse;
import com.example.popcorn.DTOs.MoviesResponse;
import com.example.popcorn.DTOs.MovieDetailsResponse;
import com.example.popcorn.Models.CastMember;
import com.example.popcorn.Models.CrewMember;
import com.example.popcorn.Models.Movie;
import com.example.popcorn.Models.Person;
import com.example.popcorn.Adapters.MoviesAdapter;
import com.example.popcorn.Utils.MemoryCache;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class FetchMoviesTask extends AsyncTask<Void, Void, List<Movie>> {
    private static final String TAG = "FetchMoviesTask";
    private RecyclerView recyclerView;
    private int page;
    private int itemsPerPage;
    private String movieType;
    private ApiService apiService;

    public FetchMoviesTask(RecyclerView recyclerView, int page, int itemsPerPage, String movieType, Context context) {
        this.recyclerView = recyclerView;
        this.page = page;
        this.itemsPerPage = itemsPerPage;
        this.movieType = movieType;
        this.apiService = RetrofitClient.getRetrofitInstance(context).create(ApiService.class);
    }

    protected List<Movie> doInBackground(Void... voids) {
        String cacheKey = movieType + "_page_" + page;
        if (MemoryCache.hasKey(cacheKey)) {
            return MemoryCache.getMovies(cacheKey);
        }

        Call<MoviesResponse> call = apiService.getMoviesByType(movieType, page);
        try {
            Response<MoviesResponse> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                List<Movie> movies = parseMoviesFromJson(response.body());
                MemoryCache.putMovies(cacheKey, movies);
                return movies;
            } else {
                Log.e(TAG, "Unsuccessful API Call: " + response.errorBody().string());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching movies", e);
        }
        return new ArrayList<>();
    }

    private List<Movie> parseMoviesFromJson(MoviesResponse moviesResponse) {
        List<Movie> results = new ArrayList<>();
        List<Movie> movieResponses = moviesResponse.getResults()
                .subList(0, Math.min(itemsPerPage, moviesResponse.getResults().size()));
        
        // Build comma-separated list of movie IDs
        StringBuilder movieIds = new StringBuilder();
        for (int i = 0; i < movieResponses.size(); i++) {
            if (i > 0) movieIds.append(",");
            movieIds.append(movieResponses.get(i).getMovieId());
        }

        // Fetch details for all movies in one call
        Call<List<MovieDetailsResponse>> call = apiService.fetchMoviesDetails(movieIds.toString());
        try {
            Response<List<MovieDetailsResponse>> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                List<MovieDetailsResponse> detailsResponses = response.body();
                
                for (int i = 0; i < movieResponses.size(); i++) {
                    Movie movieResponse = movieResponses.get(i);
                    MovieDetailsResponse details = detailsResponses.get(i);
                    
                    List<Person> cast = new ArrayList<>();
                    List<Person> crew = new ArrayList<>();
                    
                    if (details.getCredits() != null) {
                        // Parse cast
                        for (CastMember member : details.getCredits().getCast()) {
                            String imageUrl = member.getImageUrl();
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                imageUrl = "https://image.tmdb.org/t/p/w500" + imageUrl;
                            }
                            cast.add(new Person(member.getName(), member.getCharacter(), imageUrl));
                        }
                        
                        // Parse crew
                        for (CrewMember member : details.getCredits().getCrew()) {
                            String imageUrl = member.getImageUrl();
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                imageUrl = "https://image.tmdb.org/t/p/w500" + imageUrl;
                            }
                            crew.add(new Person(member.getName(), member.getJob(), imageUrl));
                        }
                    }

                    results.add(new Movie(
                        movieResponse.getMovieId(),
                        movieResponse.getTitle(),
                        movieResponse.getPosterPath(),
                        movieResponse.getPlot(),
                        cast,
                        crew
                    ));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching movie details", e);
        }
        return results;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        if (movies != null && !movies.isEmpty()) {
            recyclerView.setAdapter(new MoviesAdapter(recyclerView.getContext(), movies, false, ""));
        } else {
            Toast.makeText(recyclerView.getContext(), "Failed to fetch movies data", Toast.LENGTH_LONG).show();
        }
    }
}
