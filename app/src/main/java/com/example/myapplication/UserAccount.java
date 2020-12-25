package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.example.myapplication.WaitTime.TIME_EXIT;

public class UserAccount extends AppCompatActivity {

    BottomNavigationView bottomNavBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        bottomNavBar = findViewById(R.id.bottomNavBar);
        // 设置 NavBar 上的选中元素为 account (头像)
        bottomNavBar.setSelectedItemId(R.id.nav_account);

        navBarInit(bottomNavBar);
    }


    private void navBarInit(BottomNavigationView bottomNavBar) {
        // 跳转到新页面，并 reorder 到前面,重新排序 activity (参考下面 Link)
        // https://www.jianshu.com/p/537aa221eec4/
        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), Home.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(getApplicationContext(), Search.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_favorite:
                        startActivity(new Intent(getApplicationContext(), Favorite.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_account:
                        return true;
                }
                return false;
            }
        });
    }

    private long mBackPressed;
    // 若再次返回会退出应用, 则User需要快速返回两次, 才能退出(目的是为了防误触)
    @Override
    public void onBackPressed(){
        if (isTaskRoot()) {
            if(mBackPressed+TIME_EXIT>System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                Toast.makeText(this,"再次返回以退出",Toast.LENGTH_SHORT).show();
                mBackPressed=System.currentTimeMillis();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(0, 0);
    }
    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        // 当从其他 activity 返回到该 activity 时, 恢复(解除暂停), 重设 NavBar的选中元素
        bottomNavBar.setSelectedItemId(R.id.nav_account);
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}