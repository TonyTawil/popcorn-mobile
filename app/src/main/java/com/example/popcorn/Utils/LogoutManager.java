package com.example.popcorn.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.popcorn.Activities.SignInActivity;
import com.example.popcorn.Networking.ApiService;
import com.example.popcorn.Networking.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogoutManager {

    public static void logout(final Context context) {
        ApiService apiService = RetrofitClient.getRetrofitInstance(context).create(ApiService.class);
        Call<Void> call = apiService.logoutUser();

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    clearLocalData(context);
                    redirectToLogin(context);
                } else {
                    Toast.makeText(context, "Failed to logout", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void clearLocalData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    private static void redirectToLogin(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
