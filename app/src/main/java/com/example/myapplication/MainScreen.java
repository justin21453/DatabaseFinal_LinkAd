package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Retrofit.IMyService;
import com.example.myapplication.Retrofit.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import static com.example.myapplication.WaitTime.TIME_EXIT;

public class MainScreen extends AppCompatActivity {

    IMyService iMyService;
    private long mBackPressed;
    // 若再次返回会退出应用，则User需要快速返回两次
    @Override
    public void onBackPressed(){
        if (isTaskRoot())
        {
            if(mBackPressed+TIME_EXIT>System.currentTimeMillis()){
                super.onBackPressed();
                return;
            } else {
                Toast.makeText(this,"再次返回以退出",Toast.LENGTH_SHORT).show();
                mBackPressed=System.currentTimeMillis();
            }
        }
        else
        {
            super.onBackPressed();
            return;
        }
    }

    BottomNavigationView bottomNavBar;

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
        bottomNavBar.setSelectedItemId(R.id.nav_home);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);

        getData();

        bottomNavBar = findViewById(R.id.bottomNavBar);

        // 设置 NavBar 上的选中元素为 home (房子)
        bottomNavBar.setSelectedItemId(R.id.nav_home);

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Toast.makeText(MainScreen.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent;
                switch (item.getItemId()) {
                    // 跳转到新页面，并 reorder 到前面,重新排序 activity (参考下面 Link)
                    // https://www.jianshu.com/p/537aa221eec4/
                    case R.id.nav_home:
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
                        startActivity(new Intent(getApplicationContext(), UserAccount.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    private void getData() {

        // 进行注册判断(和后端连接)
        //subscribeOn和observeOn的解析 https://www.jianshu.com/p/1866eb720efa
        iMyService.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {
                        TextView textView=(TextView)findViewById(R.id.home);
                        try {
                            JSONArray array = new JSONArray(s);
                            String x="There are titles here:\n";
                            for(int i=0;i<array.length();i++) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                String c = jsonObject.getString("title");
                                x+=c+"\n";
                            }
                            textView.setTextSize(12);
                            textView.setText(x);
                        }catch (JSONException err){
                            err.getStackTrace();
                        }
                    }
                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        //没有连接网络
                        Toast.makeText(MainScreen.this,"请检查网络", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onComplete() {
                    }
                });
    }

}