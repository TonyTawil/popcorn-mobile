package com.example.popcorn.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.popcorn.Adapters.MoviesAdapter;
import com.example.popcorn.Models.Movie;
import com.example.popcorn.DTOs.MoviesResponse;
import com.example.popcorn.Networking.ApiService;
import com.example.popcorn.Networking.RetrofitClient;
import com.example.popcorn.R;
import com.example.popcorn.Utils.LogoutManager;
import com.example.popcorn.Utils.NavigationManager;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView searchResultsRecyclerView;
    private MoviesAdapter moviesAdapter;
    private ArrayList<Movie> searchResults = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private NavigationManager navigationManager;
    private EditText searchEditText;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationManager = new NavigationManager(this, navigationView, drawerLayout);
        navigationManager.updateDrawerContents();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));


        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(view -> performSearch(searchEditText.getText().toString()));

        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        moviesAdapter = new MoviesAdapter(this, searchResults, false, "");
        searchResultsRecyclerView.setAdapter(moviesAdapter);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_watchlist) {
                startActivity(new Intent(this, WatchlistActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_watched) {
                startActivity(new Intent(this, WatchedActivity.class));
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

    private void performSearch(String query) {
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        apiService.searchMovies(query).enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    searchResults.clear();
                    searchResults.addAll(response.body().getResults());
                    moviesAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SearchActivity.this, "No results found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Error while searching: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
