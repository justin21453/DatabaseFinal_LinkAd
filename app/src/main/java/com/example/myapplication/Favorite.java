package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Favorite extends AppCompatActivity {

    BottomNavigationView bottomNavBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        bottomNavBar = findViewById(R.id.bottomNavBar);

        bottomNavBar.setSelectedItemId(R.id.nav_favorite);

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Toast.makeText(Favorite.this, item.getTitle(), Toast.LENGTH_SHORT).show();
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
                        return true;
                    case R.id.nav_account:
                        startActivity(new Intent(getApplicationContext(), UserAccount.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }
}