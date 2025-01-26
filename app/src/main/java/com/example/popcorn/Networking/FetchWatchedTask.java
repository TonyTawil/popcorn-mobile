package com.example.popcorn.Networking;

import android.content.Context;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popcorn.Adapters.MoviesAdapter;
import com.example.popcorn.DTOs.WatchedRemoveRequest;
import com.example.popcorn.DTOs.WatchedRequest;
import com.example.popcorn.DTOs.WatchedResponse;
import com.example.popcorn.Models.Movie;
import com.example.popcorn.Models.Person;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class FetchWatchedTask {
    private RecyclerView recyclerView;
    private String userId;
    private Context context;

    public FetchWatchedTask(RecyclerView recyclerView, String userId, Context context) {
        this.recyclerView = recyclerView;
        this.userId = userId;
        this.context = context;
    }

    public void fetchWatched() {
        ApiService apiService = RetrofitClient.getRetrofitInstance(context).create(ApiService.class);
        WatchedRequest request = new WatchedRequest(userId);
        Call<WatchedResponse> call = apiService.fetchWatched(request);

        call.enqueue(new Callback<WatchedResponse>() {
            @Override
            public void onResponse(Call<WatchedResponse> call, Response<WatchedResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = convertToMovieList(response.body().getWatched());
                    if (movies.isEmpty()) {
                        Toast.makeText(context, "Watched list is empty", Toast.LENGTH_LONG).show();
                    } else {
                        recyclerView.setAdapter(new MoviesAdapter(context, movies, true, "watched"));
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch watched list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WatchedResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Movie> convertToMovieList(List<WatchedResponse.Movie> watchedMovies) {
        List<Movie> movies = new ArrayList<>();
        for (WatchedResponse.Movie watchedMovie : watchedMovies) {
            Movie movie = new Movie(watchedMovie.getMovieId(), watchedMovie.getTitle(), "https://image.tmdb.org/t/p/w500" + watchedMovie.getCoverImage(), "", new ArrayList<>(), new ArrayList<>());
            movies.add(movie);
        }
        return movies;
    }


    public void removeMovieFromWatched(String userId, int movieId, Runnable onSuccess) {
        ApiService apiService = RetrofitClient.getRetrofitInstance(context).create(ApiService.class);
        Call<ResponseBody> call = apiService.removeFromWatched(new WatchedRemoveRequest(userId, movieId));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Movie removed from watched list", Toast.LENGTH_SHORT).show();
                    onSuccess.run();
                } else {
                    Toast.makeText(context, "Failed to remove movie", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
