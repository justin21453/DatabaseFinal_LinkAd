package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserAccount extends AppCompatActivity {

    BottomNavigationView bottomNavBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        bottomNavBar = findViewById(R.id.bottomNavBar);

        bottomNavBar.setSelectedItemId(R.id.nav_account);

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Toast.makeText(UserAccount.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), MainScreen.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(getApplicationContext(), Search.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_favorite:
                        startActivity(new Intent(getApplicationContext(), Favorite.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_account:
                        return true;
                }
                return false;
            }
        });
    }
}