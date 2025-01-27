package com.example.popcorn.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.popcorn.Networking.ApiService;
import com.example.popcorn.R;
import com.example.popcorn.Networking.RetrofitClient;
import com.example.popcorn.DTOs.VerificationResponse;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

public class VerificationActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        Toolbar toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));

        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = prefs.getString("userId", null);

        Button verifiedButton = findViewById(R.id.verifiedButton);
        verifiedButton.setOnClickListener(v -> checkVerificationStatus());

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_signup) {
                Intent intent = new Intent(this, SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

            return false;
        });

        ImageView searchIcon = findViewById(R.id.search_icon);
        searchIcon.setOnClickListener(view -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });
    }

    private void checkVerificationStatus() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = prefs.getString("userId", null);

        if (userId == null) {
            Toast.makeText(this, "Please sign up or sign in first", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        Call<VerificationResponse> call = apiService.checkEmailVerified(userId);
        
        // Add log to see what userId we're checking
        Log.d("VerificationActivity", "Checking verification status for userId: " + userId);

        call.enqueue(new Callback<VerificationResponse>() {
            @Override
            public void onResponse(Call<VerificationResponse> call, Response<VerificationResponse> response) {
                // Add log for response
                Log.d("VerificationActivity", "Response code: " + response.code());
                if (response.body() != null) {
                    Log.d("VerificationActivity", "Is verified: " + response.body().isEmailVerified());
                }

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isEmailVerified()) {
                        Toast.makeText(VerificationActivity.this, "Email verified successfully", Toast.LENGTH_LONG).show();
                        // Save verification status
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("isEmailVerified", true);
                        editor.apply();
                        
                        Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(VerificationActivity.this, 
                            "Email not verified yet. Please check your email and click the verification link.", 
                            Toast.LENGTH_LONG).show();
                    }
                } else {
                    String errorMsg = "Failed to check verification status.";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " " + response.errorBody().string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("VerificationActivity", errorMsg);
                    Toast.makeText(VerificationActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<VerificationResponse> call, Throwable t) {
                Log.e("VerificationActivity", "Network error: " + t.getMessage());
                Toast.makeText(VerificationActivity.this, 
                    "Network error. Please check your connection and try again.", 
                    Toast.LENGTH_LONG).show();
            }
        });
    }
}
