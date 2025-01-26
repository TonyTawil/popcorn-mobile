package com.example.popcorn.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.popcorn.Adapters.PeopleAdapter;
import com.example.popcorn.DTOs.CreditsResponse;
import com.example.popcorn.DTOs.MovieResponse;
import com.example.popcorn.DTOs.TrailerResponse;
import com.example.popcorn.DTOs.WatchedAddRequest;
import com.example.popcorn.DTOs.WatchedAddResponse;
import com.example.popcorn.DTOs.WatchlistAddRequest;
import com.example.popcorn.DTOs.WatchlistAddResponse;
import com.example.popcorn.Models.CastMember;
import com.example.popcorn.Models.CrewMember;
import com.example.popcorn.Models.Movie;
import com.example.popcorn.Models.Person;
import com.example.popcorn.Networking.ApiService;
import com.example.popcorn.Networking.FetchSimilarMoviesTask;
import com.example.popcorn.Networking.RetrofitClient;
import com.example.popcorn.R;
import com.example.popcorn.Utils.LogoutManager;
import com.example.popcorn.Utils.NavigationManager;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {
    private RecyclerView castRecyclerView, crewRecyclerView, similarMoviesRecyclerView;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;
    private Button addToWatchlistButton, markAsWatchedButton, addReviewButton, viewReviewsButton, playTrailerButton;
    private SharedPreferences sharedPreferences;
    private String apiKey = "0b8fd19c988298f0d68d8b054667e493";
    private int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        movieId = getIntent().getIntExtra("movieId", -1);
        if (movieId == -1) {
            Toast.makeText(this, "Invalid movie ID", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationManager = new NavigationManager(this, navigationView, drawerLayout);
        navigationManager.updateDrawerContents();

        setupDrawer(toolbar);
        setupNavigationMenu(navigationView);

        castRecyclerView = findViewById(R.id.castRecyclerView);
        crewRecyclerView = findViewById(R.id.crewRecyclerView);
        similarMoviesRecyclerView = findViewById(R.id.similarMoviesRecyclerView);

        similarMoviesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        displayMovieDetails();
        initRecyclerViews();
        fetchSimilarMovies();

        addToWatchlistButton = findViewById(R.id.addToWatchlistButton);
        markAsWatchedButton = findViewById(R.id.markAsWatchedButton);
        addReviewButton = findViewById(R.id.addReviewButton);
        viewReviewsButton = findViewById(R.id.viewAllReviewsButton);
        playTrailerButton = findViewById(R.id.playTrailerButton);

        addToWatchlistButton.setOnClickListener(v -> addToWatchlist());
        markAsWatchedButton.setOnClickListener(v -> addToWatched());
        addReviewButton.setOnClickListener(v -> openAddReviewActivity());
        viewReviewsButton.setOnClickListener(v -> openReviewsActivity());
        playTrailerButton.setOnClickListener(v -> playTrailer());

        ImageView searchIcon = findViewById(R.id.search_icon);
        searchIcon.setOnClickListener(view -> startActivity(new Intent(this, SearchActivity.class)));
    }

    private void playTrailer() {
        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        Call<TrailerResponse> call = apiService.fetchMovieTrailers(movieId);

        call.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TrailerResponse.Trailer> results = response.body().getResults();
                    if (!results.isEmpty()) {
                        String key = results.get(0).getKey();
                        runOnUiThread(() -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + key));
                            intent.putExtra("force_fullscreen", true);
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            } else {
                                intent.setData(Uri.parse("https://www.youtube.com/watch?v=" + key));
                                startActivity(intent);
                            }
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(MovieDetailsActivity.this, "No trailer available", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Error body is null";
                        Log.e("MovieDetailsActivity", "Unsuccessful response, error body: " + errorBody);
                    } catch (IOException e) {
                        Log.e("MovieDetailsActivity", "Error reading error body", e);
                    }
                    runOnUiThread(() -> Toast.makeText(MovieDetailsActivity.this, "Failed to fetch trailer", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Log.e("MovieDetailsActivity", "Network error while fetching trailer", t);
                runOnUiThread(() -> Toast.makeText(MovieDetailsActivity.this, "Error fetching trailer: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }


    private void fetchSimilarMovies() {
        new FetchSimilarMoviesTask(similarMoviesRecyclerView, movieId, this).execute();
    }

    private void setupDrawer(Toolbar toolbar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
    }

    private void displayMovieDetails() {
        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        Call<MovieResponse> call = apiService.getMovieDetails(movieId);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieResponse movie = response.body();

                    Log.d("MovieDetailsActivity", "Movie Response: " + movie.toString());

                    ImageView moviePosterImageView = findViewById(R.id.moviePosterImageView);
                    TextView movieTitleTextView = findViewById(R.id.movieTitleTextView);
                    TextView moviePlotTextView = findViewById(R.id.moviePlotTextView);

                    movieTitleTextView.setText(movie.getTitle());
                    moviePlotTextView.setText(movie.getOverview());

                    String posterPath = movie.getPosterPath();
                    if (posterPath != null && !posterPath.startsWith("http")) {
                        posterPath = "https://image.tmdb.org/t/p/w500" + posterPath;
                    }

                    Log.d("MovieDetailsActivity", "Poster Path: " + posterPath);

                    Glide.with(MovieDetailsActivity.this).load(posterPath).into(moviePosterImageView);
                } else {
                    Toast.makeText(MovieDetailsActivity.this, "Failed to load movie details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("MovieDetailsActivity", "Error loading movie details: " + t.getMessage());
                Toast.makeText(MovieDetailsActivity.this, "Error loading movie details: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void initRecyclerViews() {
        castRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        crewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        Call<CreditsResponse> call = apiService.fetchMovieCredits(movieId);

        call.enqueue(new Callback<CreditsResponse>() {
            @Override
            public void onResponse(Call<CreditsResponse> call, Response<CreditsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CreditsResponse creditsResponse = response.body();

                    List<Person> cast = new ArrayList<>();
                    for (CastMember member : creditsResponse.getCast()) {
                        cast.add(new Person(member.getName(), member.getCharacter(), member.getImageUrl()));
                    }

                    List<Person> crew = new ArrayList<>();
                    for (CrewMember member : creditsResponse.getCrew()) {
                        crew.add(new Person(member.getName(), member.getJob(), member.getImageUrl()));
                    }

                    castRecyclerView.setAdapter(new PeopleAdapter(MovieDetailsActivity.this, cast));
                    crewRecyclerView.setAdapter(new PeopleAdapter(MovieDetailsActivity.this, crew));
                } else {
                    Log.e("MovieDetailsActivity", "Failed to fetch credits: " + response.errorBody());
                    Toast.makeText(MovieDetailsActivity.this, "Failed to load credits", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreditsResponse> call, Throwable t) {
                Log.e("MovieDetailsActivity", "Error loading credits", t);
                Toast.makeText(MovieDetailsActivity.this, "Error loading credits: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void openAddReviewActivity() {
        String userId = sharedPreferences.getString("userId", null);
        if (userId == null) {
            Toast.makeText(this, "You must be logged in to add a review.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, AddReviewActivity.class);
        intent.putExtra("movieId", movieId);
        startActivity(intent);
    }

    private void openReviewsActivity() {
        Intent intent = new Intent(this, ReviewsActivity.class);
        intent.putExtra("movieId", movieId);
        startActivity(intent);
    }

    private void setupNavigationMenu(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_watchlist) {
                Intent intent = new Intent(this, WatchlistActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_watched) {
                Intent intent = new Intent(this, WatchedActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_logout) {
                LogoutManager.logout(this);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });
    }

    private void addToWatchlist() {
        String userId = sharedPreferences.getString("userId", null);
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        WatchlistAddRequest request = new WatchlistAddRequest(
                userId, movieId, getIntent().getStringExtra("title"), getIntent().getStringExtra("posterPath"));
        apiService.addToWatchlist(request).enqueue(new Callback<WatchlistAddResponse>() {
            @Override
            public void onResponse(Call<WatchlistAddResponse> call, Response<WatchlistAddResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(MovieDetailsActivity.this, "Added to watchlist", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 409) {
                    Toast.makeText(MovieDetailsActivity.this, "Movie already in watchlist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MovieDetailsActivity.this, "Failed to add to watchlist: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WatchlistAddResponse> call, Throwable t) {
                Toast.makeText(MovieDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToWatched() {
        String userId = sharedPreferences.getString("userId", null);
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        WatchedAddRequest request = new WatchedAddRequest(userId, movieId, getIntent().getStringExtra("title"), getIntent().getStringExtra("posterPath"));
        apiService.addToWatched(request).enqueue(new Callback<WatchedAddResponse>() {
            @Override
            public void onResponse(Call<WatchedAddResponse> call, Response<WatchedAddResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(MovieDetailsActivity.this, "Added to watched list", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MovieDetailsActivity.this, "Movie already marked as watched", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WatchedAddResponse> call, Throwable t) {
                Toast.makeText(MovieDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
