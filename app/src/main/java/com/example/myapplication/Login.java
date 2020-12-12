package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Retrofit.IMyService;
import com.example.myapplication.Retrofit.RetrofitClient;

import com.rengwuxian.materialedittext.MaterialEditText;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import retrofit2.Retrofit;

public class Login extends AppCompatActivity {

    TextView txt_create_account;
    MaterialEditText edt_login_email,edt_login_password;
    Button btn_login;
    
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    @Override
    public void onResume()
    {
        super.onResume();
        // 註冊mConnReceiver，並用IntentFilter設置接收的事件類型為網路開關
        this.registerReceiver(mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        // 解除註冊
        this.unregisterReceiver(mConnReceiver);
    }

    // 建立一個BroadcastReceiver，名為mConnReceiver
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 當使用者開啟或關閉網路時會進入這邊
            // 判斷目前有無網路
            if(isNetworkAvailable()) {
                // 以連線至網路，做更新資料等事情
            }
            else {
                // 沒有網路
                Toast.makeText(Login.this, "Seems not connect to internet", Toast.LENGTH_LONG).show();
            }
        }
    };
    @Override
    protected void onStop(){
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);

        edt_login_email = (MaterialEditText)findViewById(R.id.edt_email);
        edt_login_password = (MaterialEditText)findViewById(R.id.edt_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                loginUser(edt_login_email.getText().toString(),
                        edt_login_password.getText().toString());
            }
        });

        txt_create_account = (TextView) findViewById(R.id.txt_create_account);
        txt_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email,String password){
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Email cannot be null or empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Password cannot be null or empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isNetworkAvailable())
        {
            Toast.makeText(this,"Didn't connect to Internet", Toast.LENGTH_LONG).show();
            return;
        }
        compositeDisposable.add(iMyService.loginUser(email,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(Login.this, ""+response, Toast.LENGTH_SHORT).show();
                    }
                }));

    }
    // 回傳目前是否已連線至網路
    public boolean isNetworkAvailable()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null &&
                networkInfo.isConnected();
    }
}