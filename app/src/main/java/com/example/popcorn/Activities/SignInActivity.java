package com.example.popcorn.Activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.popcorn.DTOs.LoginUser;
import com.example.popcorn.DTOs.UserResponse;
import com.example.popcorn.Networking.ApiService;
import com.example.popcorn.Networking.RetrofitClient;
import com.example.popcorn.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button signInButton;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private TextView signUpClickable;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Toolbar toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));


        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signInButton = findViewById(R.id.signInButton);
        signUpClickable = findViewById(R.id.signUpClickable);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton googleSignInButton = findViewById(R.id.sign_in_with_google_button);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        signInButton.setOnClickListener(v -> performLogin());
        signUpClickable.setOnClickListener(v -> navigateToSignUp());

        navigationView.setNavigationItemSelectedListener(this::handleNavigationItemSelected);
    }

    private void signInWithGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                Gson gson = new Gson();
                String accountJson = gson.toJson(account);
                Log.i(TAG, "Google Sign In was successful: " + accountJson);
                String idToken = account.getIdToken();
                Log.i(TAG, "Google ID Token: " + idToken);

                fetchUserFromBackend(account.getEmail());
            } else {
                Log.w(TAG, "signInResult:failed - account is null");
                updateUI(null);
            }
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void fetchUserFromBackend(String email) {
        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        Call<UserResponse> call = apiService.getUserByEmail(email);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();
                    saveUserDetailsAndNavigate(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
                } else {
                    Log.w(TAG, "User fetch failed: " + response.message());
                    updateUI(null);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                updateUI(null);
            }
        });
    }

    private void saveUserDetailsAndNavigate(String userId, String firstName, String lastName, String email) {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userId", userId);
        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.putString("email", email);
        editor.apply();

        Toast.makeText(this, "Signed in as: " + email, Toast.LENGTH_SHORT).show();
        navigateToMainActivity();
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            Toast.makeText(this, "Signed in as: " + account.getEmail(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to sign in with Google.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void performLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        Call<UserResponse> call = apiService.loginUser(new LoginUser(username, password));
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();
                    saveUserDetailsAndNavigate(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
                } else {
                    handleLoginError(response);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(SignInActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleLoginError(Response<UserResponse> response) {
        try {
            Toast.makeText(SignInActivity.this, "Login failed: " + response.errorBody().string(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(SignInActivity.this, "Error parsing error response", Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private boolean handleNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_signup) {
            navigateToSignUp();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }
}
