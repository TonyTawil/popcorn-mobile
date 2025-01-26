package com.example.popcorn.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.popcorn.Activities.MainActivity;
import com.example.popcorn.Activities.SignInActivity;
import com.example.popcorn.Activities.SignUpActivity;
import com.example.popcorn.R;
import com.google.android.material.navigation.NavigationView;
import android.util.Log;

public class NavigationManager {
    private Context context;
    private SharedPreferences sharedPreferences;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    public NavigationManager(Context context, NavigationView navigationView, DrawerLayout drawerLayout) {
        this.context = context;
        this.navigationView = navigationView;
        this.drawerLayout = drawerLayout;
        this.sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    public void updateDrawerContents() {
        boolean isLoggedIn = sharedPreferences.contains("userId");
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderTextView = headerView.findViewById(R.id.nav_header_textView);
        Menu menu = navigationView.getMenu();
        menu.clear();

        if (isLoggedIn) {
            navigationView.inflateMenu(R.menu.logged_in_drawer_menu);
            String firstName = sharedPreferences.getString("firstName", "");
            String lastName = sharedPreferences.getString("lastName", "");

            if (firstName.equals("N/A") || lastName.equals("N/A")) {
                navHeaderTextView.setText("Welcome!");
            } else {
                String fullName = firstName + " " + lastName;
                navHeaderTextView.setText("Welcome " + fullName.trim() + "!");
            }
        } else {
            navigationView.inflateMenu(R.menu.drawer_menu);
            navHeaderTextView.setText("Welcome to Popcorn!");
            setupSignUpMenuItem();
        }
    }


    private void setupSignUpMenuItem() {
        MenuItem signUpItem = navigationView.getMenu().findItem(R.id.nav_signup);
        if (signUpItem != null) {
            signUpItem.setOnMenuItemClickListener(item -> {
                context.startActivity(new Intent(context, SignUpActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }
    }
}

