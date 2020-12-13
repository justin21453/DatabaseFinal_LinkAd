package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Search extends AppCompatActivity {

    BottomNavigationView bottomNavBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        bottomNavBar = findViewById(R.id.bottomNavBar);

        bottomNavBar.setSelectedItemId(R.id.nav_search);

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Toast.makeText(Search.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), MainScreen.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_search:
                        return true;
                    case R.id.nav_favorite:
                        startActivity(new Intent(getApplicationContext(), Favorite.class));
                        overridePendingTransition(0, 0);
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