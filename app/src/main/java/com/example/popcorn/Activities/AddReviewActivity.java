package com.example.popcorn.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.popcorn.DTOs.ReviewRequest;
import com.example.popcorn.DTOs.ReviewResponse;
import com.example.popcorn.Networking.ApiService;
import com.example.popcorn.Networking.RetrofitClient;
import com.example.popcorn.R;
import com.example.popcorn.Utils.LogoutManager;
import com.example.popcorn.Utils.NavigationManager;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddReviewActivity extends AppCompatActivity {
    private EditText reviewEditText;
    private Button addReviewButton;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;

    private RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        ratingBar = findViewById(R.id.ratingBar);
        if (ratingBar == null) {
            throw new IllegalStateException("RatingBar not found in layout");
        }

        Toolbar toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationManager = new NavigationManager(this, navigationView, drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));

        reviewEditText = findViewById(R.id.reviewEditText);
        addReviewButton = findViewById(R.id.addReviewButton);
        addReviewButton.setOnClickListener(v -> submitReview());

        ImageView searchIcon = findViewById(R.id.search_icon);
        searchIcon.setOnClickListener(view -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });

        navigationManager.updateDrawerContents();
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
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
    protected void onResume() {
        super.onResume();
        navigationManager.updateDrawerContents();
    }



    private void submitReview() {
        String reviewText = reviewEditText.getText().toString();
        float rating = ratingBar.getRating();
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userId = prefs.getString("userId", "");
        int movieId = prefs.getInt("movieId", 0);

        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Please enter a review", Toast.LENGTH_SHORT).show();
            return;
        }

        ReviewRequest reviewRequest = new ReviewRequest(userId, movieId, rating, reviewText);
        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        Call<ReviewResponse> call = apiService.addReview(reviewRequest);
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddReviewActivity.this, "Review submitted!", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(AddReviewActivity.this, ReviewsActivity.class);
                    intent.putExtra("movieId", movieId);
                    startActivity(intent);
                    finish();

                } else {
                    String errorMessage = "You've already reviewed this movie";
                    Toast.makeText(AddReviewActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                Log.e("AddReviewActivity", "Network error while adding review for user " + userId + " and movie " + movieId, t);
                Toast.makeText(AddReviewActivity.this, "Error connecting to the server: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
