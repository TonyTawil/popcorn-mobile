package com.example.popcorn.Utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.example.popcorn.DTOs.CreditsResponse;
import com.example.popcorn.DTOs.MoviesResponse;
import com.example.popcorn.Models.Movie;
import com.example.popcorn.Networking.ApiService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import retrofit2.Response;
import java.util.concurrent.TimeUnit;

public class ConcurrentApiHelper {
    private static final String TAG = "ConcurrentApiHelper";
    private static final int BATCH_SIZE = 5; // Reduced batch size for better parallelization
    private final ApiService apiService;
    private final ExecutorService executor;
    private final Handler mainHandler;
    private boolean isShutdown = false;

    public ConcurrentApiHelper(ApiService apiService) {
        this.apiService = apiService;
        // Create a thread pool with number of cores + 1 threads
        int numberOfThreads = Runtime.getRuntime().availableProcessors() + 1;
        this.executor = Executors.newFixedThreadPool(numberOfThreads);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public void fetchAllMovieLists(ApiCallback<List<MoviesResponse>> callback) {
        if (isShutdown) {
            callback.onError(new IllegalStateException("ConcurrentApiHelper has been shut down"));
            return;
        }

        executor.execute(() -> {
            try {
                List<MoviesResponse> allResponses = new ArrayList<>();
                CountDownLatch latch = new CountDownLatch(3); // For popular, now_playing, and upcoming
                List<Exception> errors = new ArrayList<>();

                // Fetch all types concurrently
                String[] types = {"popular", "now_playing", "upcoming"};
                for (String type : types) {
                    executor.execute(() -> {
                        try {
                            fetchMoviesWithCredits(type, 1, new ApiCallback<MoviesResponse>() {
                                @Override
                                public void onSuccess(MoviesResponse result) {
                                    synchronized (allResponses) {
                                        allResponses.add(result);
                                    }
                                    latch.countDown();
                                }

                                @Override
                                public void onError(Exception e) {
                                    synchronized (errors) {
                                        errors.add(e);
                                    }
                                    latch.countDown();
                                }
                            });
                        } catch (Exception e) {
                            synchronized (errors) {
                                errors.add(e);
                            }
                            latch.countDown();
                        }
                    });
                }

                // Wait for all requests to complete
                latch.await();

                if (!errors.isEmpty()) {
                    throw new Exception("Error fetching one or more movie lists: " + errors.get(0).getMessage());
                }

                mainHandler.post(() -> callback.onSuccess(allResponses));

            } catch (Exception e) {
                Log.e(TAG, "Error fetching all movie lists", e);
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    public void fetchMoviesWithCredits(String type, int page, ApiCallback<MoviesResponse> callback) {
        executor.execute(() -> {
            try {
                // First fetch movies
                Response<MoviesResponse> moviesResponse = apiService.getMoviesByType(type, page).execute();
                if (!moviesResponse.isSuccessful() || moviesResponse.body() == null) {
                    throw new Exception("Failed to fetch movies");
                }

                MoviesResponse movies = moviesResponse.body();
                if (movies.getResults() == null || movies.getResults().isEmpty()) {
                    mainHandler.post(() -> callback.onSuccess(movies));
                    return;
                }

                List<Integer> movieIds = new ArrayList<>();
                for (Movie movie : movies.getResults()) {
                    movieIds.add(movie.getMovieId());
                }

                // Calculate number of batches
                int totalBatches = (movieIds.size() + BATCH_SIZE - 1) / BATCH_SIZE;
                CountDownLatch latch = new CountDownLatch(totalBatches);
                List<Exception> errors = new ArrayList<>();

                // Process each batch in parallel
                for (int i = 0; i < movieIds.size(); i += BATCH_SIZE) {
                    final int startIndex = i;
                    executor.execute(() -> {
                        try {
                            int endIndex = Math.min(startIndex + BATCH_SIZE, movieIds.size());
                            List<Integer> batchIds = movieIds.subList(startIndex, endIndex);
                            
                            Response<Map<String, CreditsResponse>> creditsResponse = 
                                apiService.fetchMovieCreditsInBatch(batchIds).execute();
                            
                            if (creditsResponse.isSuccessful() && creditsResponse.body() != null) {
                                synchronized (movies) {
                                    creditsResponse.body().forEach((movieId, credits) -> {
                                        movies.getResults().stream()
                                            .filter(movie -> movie.getMovieId() == Integer.parseInt(movieId))
                                            .findFirst()
                                            .ifPresent(movie -> movie.addCredits(credits));
                                    });
                                }
                            }
                        } catch (Exception e) {
                            synchronized (errors) {
                                errors.add(e);
                            }
                            Log.e(TAG, "Error processing batch", e);
                        } finally {
                            latch.countDown();
                        }
                    });
                }

                // Wait for all batches to complete
                latch.await();

                if (!errors.isEmpty()) {
                    throw new Exception("Error processing one or more batches");
                }

                mainHandler.post(() -> callback.onSuccess(movies));

            } catch (Exception e) {
                Log.e(TAG, "Error fetching movies with credits", e);
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    public void shutdown() {
        isShutdown = true;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public boolean isShutdown() {
        return isShutdown;
    }
} 