package com.example.myapplication;

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

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import retrofit2.Retrofit;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class Login extends AppCompatActivity {

    // 初始化用于和UI界面绑定的 View和button等 object
    TextView            txt_create_account;
    MaterialEditText    edt_login_email, edt_login_password;
    Button              btn_login;
    // 初始化网络连接服务(呼叫后端用的 service)
    IMyService          iMyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);    //绑定具体的UI视图(.xml文件)

        // 宣告 Retrofit 进行网络链接
        Retrofit retrofitClient = RetrofitClient.getInstance();
        // 取得服务
        iMyService = retrofitClient.create(IMyService.class);

        // 绑定页面中的UI
        edt_login_email = findViewById(R.id.edt_email);
        edt_login_password = findViewById(R.id.edt_password);

        // 登录按钮
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                loginUser(edt_login_email.getText().toString(), edt_login_password.getText().toString());
            }
        });

        // 前往注册界面的按钮(实际上是 TextView)
        txt_create_account = findViewById(R.id.txt_create_account);
        txt_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email,String password){
        // 判断是否有漏填的信息
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Space cannot be null or empty", Toast.LENGTH_SHORT).show();
            return;
        }
        //进行登录判断(和后端连接)
        //subscribeOn和observeOn的解析 https://www.jianshu.com/p/1866eb720efa
        iMyService.loginUser(email,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        // 网络状态正常，已和server完成通信，获得return value
                        if (s.contains("Login success"))
                        {
                            // 成功登陆，前往 MainScreen 主界面
                            Intent intent = new Intent(Login.this, MainScreen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);

                            // 结束当前 activity, 即返回的时候, 不会再返回登录界面, 而会直接退出
                            finish();
                        }
                        else
                        {
                            // 密码错误, Toast print 出 string 提醒 user
                            Toast.makeText(Login.this,s, Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {
                        //没有连接网络
                        Toast.makeText(Login.this,"请检查网络", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onComplete() {
                    }
                });

    }

}