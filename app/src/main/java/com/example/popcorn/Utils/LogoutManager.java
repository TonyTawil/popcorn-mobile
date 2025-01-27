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

    public static void logout(Context context) {
        ApiService apiService = RetrofitClient.getRetrofitInstance(context).create(ApiService.class);
        Call<Void> call = apiService.logoutUser();

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Clear SharedPreferences regardless of server response
                clearUserData(context);
                
                // Navigate to SignIn screen
                Intent intent = new Intent(context, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                
                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Even if the server call fails, we should still log out locally
                clearUserData(context);
                
                Intent intent = new Intent(context, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                
                Toast.makeText(context, "Logged out locally", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void clearUserData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
