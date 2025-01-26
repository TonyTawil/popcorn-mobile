package com.example.popcorn.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.popcorn.Adapters.ReviewsAdapter;
import com.example.popcorn.Models.Review;
import com.example.popcorn.Networking.ApiService;
import com.example.popcorn.Networking.RetrofitClient;
import com.example.popcorn.R;
import com.example.popcorn.Utils.LogoutManager;
import com.example.popcorn.Utils.NavigationManager;
import com.google.android.material.navigation.NavigationView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsActivity extends AppCompatActivity {
    private static final int EDIT_REVIEW_REQUEST = 100;
    private RecyclerView reviewsRecyclerView;
    private ReviewsAdapter reviewsAdapter;
    private int movieId;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        Toolbar toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationManager = new NavigationManager(this, navigationView, drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));

        Intent intent = getIntent();
        movieId = intent.getIntExtra("movieId", -1);
        if (movieId == -1) {
            finish();
            return;
        }

        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadReviews();

        ImageView searchIcon = findViewById(R.id.search_icon);
        searchIcon.setOnClickListener(view -> {
            Intent intent2 = new Intent(this, SearchActivity.class);
            startActivity(intent2);
        });

        navigationManager.updateDrawerContents();
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private void loadReviews() {
        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        Call<List<Review>> call = apiService.getReviewsByMovieId(movieId);
        call.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Review> reviews = response.body();
                    if (reviews.isEmpty()) {
                        Toast.makeText(ReviewsActivity.this, "No reviews available.", Toast.LENGTH_SHORT).show();
                    } else {
                        reviewsAdapter = new ReviewsAdapter(reviews, ReviewsActivity.this);
                        reviewsRecyclerView.setAdapter(reviewsAdapter);
                    }
                } else {
                    Toast.makeText(ReviewsActivity.this, "Failed to fetch reviews.", Toast.LENGTH_SHORT).show();
                    Log.e("ReviewsActivity", "Failed to fetch reviews: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Toast.makeText(ReviewsActivity.this, "Error fetching reviews.", Toast.LENGTH_LONG).show();
                Log.e("ReviewsActivity", "Error fetching reviews: " + t.getMessage());
            }
        });
    }


    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.nav_watchlist) {
            startActivity(new Intent(this, WatchlistActivity.class));
        } else if (id == R.id.nav_watched) {
            startActivity(new Intent(this, WatchedActivity.class));
        } else if (id == R.id.nav_logout) {
            LogoutManager.logout(this);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REVIEW_REQUEST && resultCode == RESULT_OK) {
            loadReviews();
        }
    }
}
