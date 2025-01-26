package com.example.popcorn.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.popcorn.DTOs.UserResponse;
import com.example.popcorn.Models.User;
import com.example.popcorn.Networking.ApiService;
import com.example.popcorn.Networking.RetrofitClient;
import com.example.popcorn.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Spinner genderSpinner;
    private Button signUpButton;
    private ImageView googleSignUpButton;
    private TextView signInPrompt, signInClickable;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));



        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        genderSpinner = findViewById(R.id.genderSpinner);
        signUpButton = findViewById(R.id.signUpButton);
        signInPrompt = findViewById(R.id.signInPrompt);
        signInClickable = findViewById(R.id.signInClickable);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, R.layout.spinner_selected_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        genderSpinner.setAdapter(adapter);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignUpButton = findViewById(R.id.googleSignUpButton);
        googleSignUpButton.setOnClickListener(v -> signInWithGoogle());

        signUpButton.setOnClickListener(v -> attemptSignUp());
        signInClickable.setOnClickListener(v -> navigateToSignIn());

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private void signInWithGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                registerUser(account);
            }
        } catch (ApiException e) {
            Log.w("SignUpActivity", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void registerUser(GoogleSignInAccount account) {
        String firstName = account.getGivenName() != null ? account.getGivenName() : "N/A";
        String lastName = account.getFamilyName() != null ? account.getFamilyName() : "N/A";
        String email = account.getEmail();
        String defaultPassword = "Google" + System.currentTimeMillis();
        String gender = "other";
        String username = email.split("@")[0];

        User newUser = new User(firstName, lastName, username, email, defaultPassword, defaultPassword, gender, true);
        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        Call<UserResponse> call = apiService.createUser(newUser);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SignUpActivity.this, "User registered successfully.", Toast.LENGTH_LONG).show();
                    saveUserDetails(response.body().getId(), firstName, lastName, email);
                    navigateToMainActivity();
                } else {
                    try {
                        Toast.makeText(SignUpActivity.this, "Registration failed: " + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveUserDetails(String userId, String firstName, String lastName, String email) {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userId", userId);
        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.putString("email", email);
        editor.apply();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void attemptSignUp() {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        User newUser = new User(firstName, lastName, username, email, password, confirmPassword, gender, false);
        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        Call<UserResponse> call = apiService.createUser(newUser);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SignUpActivity.this, "User registered successfully. Please verify your email.", Toast.LENGTH_LONG).show();
                    SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("userId", response.body().getId());
                    editor.apply();
                    navigateToVerification();
                } else {
                    try {
                        Toast.makeText(SignUpActivity.this, "Registration failed: " + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
    }

    private void navigateToVerification() {
        Intent intent = new Intent(SignUpActivity.this, VerificationActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean onNavigationItemSelected(MenuItem item) {
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
    }
}
