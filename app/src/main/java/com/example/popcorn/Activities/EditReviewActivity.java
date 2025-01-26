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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditReviewActivity extends AppCompatActivity {
    private EditText reviewEditText;
    private RatingBar ratingBar;
    private Button saveReviewButton;
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);

        Toolbar toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        reviewEditText = findViewById(R.id.reviewEditText);
        ratingBar = findViewById(R.id.ratingBar);
        saveReviewButton = findViewById(R.id.saveReviewButton);

        loadInitialData();

        navigationManager.updateDrawerContents();
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        saveReviewButton.setOnClickListener(v -> submitReview());

        ImageView searchIcon = findViewById(R.id.search_icon);
        searchIcon.setOnClickListener(view -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });
    }

    private void loadInitialData() {
        Intent intent = getIntent();
        String reviewText = intent.getStringExtra("reviewText");
        float rating = intent.getFloatExtra("rating", 0);

        Log.d("EditReviewActivity", "Loaded review text: " + reviewText);
        Log.d("EditReviewActivity", "Loaded rating: " + rating);

        reviewEditText.setText(reviewText);
        ratingBar.setRating(rating);
    }


    private void submitReview() {
        String reviewText = reviewEditText.getText().toString();
        float rating = ratingBar.getRating();
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userId = prefs.getString("userId", "");
        int movieId = prefs.getInt("movieId", 0);
        String reviewId = getIntent().getStringExtra("reviewId");

        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Review text cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        ReviewRequest reviewRequest = new ReviewRequest(userId, movieId, rating, reviewText);
        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        Call<ReviewResponse> call = apiService.updateReview(reviewId, reviewRequest);

        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditReviewActivity.this, "Review updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditReviewActivity.this, ReviewsActivity.class);
                    intent.putExtra("movieId", movieId);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("EditReviewActivity", "Failed to update review: " + response.code());
                    Toast.makeText(EditReviewActivity.this, "Failed to update review", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                Log.e("EditReviewActivity", "Error connecting to the server", t);
                Toast.makeText(EditReviewActivity.this, "Error connecting to the server", Toast.LENGTH_LONG).show();
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
}
