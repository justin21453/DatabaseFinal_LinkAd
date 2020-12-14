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

public class SignUp extends AppCompatActivity {

    // 初始化用于和UI界面绑定的 View和button等 object
    TextView            txt_have_account;
    MaterialEditText    edt_register_email, edt_register_password, edt_register_name, edt_register_password_confirm;
    Button              btn_SignUp;

    // 初始化网络连接服务(呼叫后端用的 service)
    IMyService          iMyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 宣告 Retrofit 进行网络链接
        Retrofit retrofitClient = RetrofitClient.getInstance();
        // 取得服务
        iMyService = retrofitClient.create(IMyService.class);

        // 绑定页面中的UI
        edt_register_email = findViewById(R.id.edt_email);
        edt_register_name = findViewById(R.id.edt_name);
        edt_register_password = findViewById(R.id.edt_password);
        edt_register_password_confirm = findViewById(R.id.edt_confirmPassword);

        btn_SignUp = findViewById(R.id.btn_signUp);
        // 注册按钮
        btn_SignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // call 注册的function
                registerUser(edt_register_email.getText().toString(),
                        edt_register_name.getText().toString(),
                        edt_register_password.getText().toString(),
                        edt_register_password_confirm.getText().toString()
                );
            }
        });

        txt_have_account = findViewById(R.id.txt_have_account);
        //返回登录界面的按钮
        txt_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, Login.class);
                //返回登录界面,并清空登录界面以上的activity,登录界面A,注册B，stack目前是AB,现在回到A，则清空B的记录
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //直接 finish() activity
//                finish();
            }
        });

    }

    private void registerUser(String email, String name, String password, String confirm) {
        // 判断是否有漏填的信息
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
            Toast.makeText(SignUp.this, "Space cannot be null or empty", Toast.LENGTH_SHORT).show();
            return;
        }
        // 判断第二次(确认)密码有没有和第一次一直
        if ( confirm.equals(password) == false ) {
            Toast.makeText(SignUp.this, "Confirm Password is wrong", Toast.LENGTH_SHORT).show();
            return;
        }

        // 进行注册判断(和后端连接)
        //subscribeOn和observeOn的解析 https://www.jianshu.com/p/1866eb720efa
        iMyService.registerUser(email,name,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                        }

                        @Override
                        public void onNext(@NonNull String s) {
                            // 网络状态正常，已和server完成通信，获得return value
                            if (s.contains("success"))
                            {
                                //如果回传 string 包含 success ,则注册成功
                                Toast.makeText(SignUp.this,s, Toast.LENGTH_LONG).show();
                                //返回登录界面
                                finish();
                            }
                            else
                            {
                                //邮箱已存在
                                Toast.makeText(SignUp.this,s, Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onError(@NonNull Throwable e) {
                            //没有连接网络
                            Toast.makeText(SignUp.this,"请检查网络", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onComplete() {
                        }
                });
    }

}