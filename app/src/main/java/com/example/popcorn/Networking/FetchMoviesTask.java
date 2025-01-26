package com.example.popcorn.Networking;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popcorn.DTOs.CreditsResponse;
import com.example.popcorn.DTOs.MoviesResponse;
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
        for (Movie movieResponse : moviesResponse.getResults().subList(0, Math.min(itemsPerPage, moviesResponse.getResults().size()))) {
            int movieId = movieResponse.getMovieId();
            String title = movieResponse.getTitle();
            String posterPath = movieResponse.getPosterPath();
            String plot = movieResponse.getPlot();

            List<Person> cast = fetchCredits(movieId, "cast");
            List<Person> crew = fetchCredits(movieId, "crew");

            results.add(new Movie(movieId, title, posterPath, plot, cast, crew));
        }
        return results;
    }

    private List<Person> fetchCredits(int movieId, String type) {
        Call<CreditsResponse> call = apiService.fetchMovieCredits(movieId);

        try {
            Response<CreditsResponse> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                CreditsResponse credits = response.body();
                List<Person> people = new ArrayList<>();
                if ("cast".equals(type)) {
                    for (CastMember member : credits.getCast()) {
                        String imageUrl = member.getImageUrl();
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            imageUrl = "https://image.tmdb.org/t/p/w500" + imageUrl;
                        }
                        people.add(new Person(member.getName(), member.getCharacter(), imageUrl));
                    }
                } else if ("crew".equals(type)) {
                    for (CrewMember member : credits.getCrew()) {
                        String imageUrl = member.getImageUrl();
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            imageUrl = "https://image.tmdb.org/t/p/w500" + imageUrl;
                        }
                        people.add(new Person(member.getName(), member.getJob(), imageUrl));
                    }
                }
                return people;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching credits", e);
        }
        return new ArrayList<>();
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
