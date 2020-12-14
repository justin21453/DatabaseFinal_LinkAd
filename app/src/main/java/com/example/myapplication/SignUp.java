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

    TextView txt_have_account;
    MaterialEditText edt_register_email, edt_register_password, edt_register_name, edt_register_password_confirm;
    Button btn_SignUp;

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
                Toast.makeText(SignUp.this, "Seems not connect to internet", Toast.LENGTH_LONG).show();
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
        setContentView(R.layout.activity_sign_up);

        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);

        edt_register_email = (MaterialEditText)findViewById(R.id.edt_email);
        edt_register_name = (MaterialEditText)findViewById(R.id.edt_name);
        edt_register_password = (MaterialEditText)findViewById(R.id.edt_password);
        edt_register_password_confirm = (MaterialEditText)findViewById(R.id.edt_confirmPassword);

        btn_SignUp = (Button) findViewById(R.id.btn_signUp);
        btn_SignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // call register function
                registerUser(edt_register_email.getText().toString(),
                        edt_register_name.getText().toString(),
                        edt_register_password.getText().toString()
                );

            }
        });

        txt_have_account = (TextView) findViewById(R.id.txt_have_account);
        txt_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, Login.class);
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    private void registerUser(String email, String name, String password) {
        // Check empty and confirmed password
        if (TextUtils.isEmpty(edt_register_email.getText().toString())) {
            Toast.makeText(SignUp.this, "Email cannot be null or empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(edt_register_name.getText().toString())) {
            Toast.makeText(SignUp.this, "Name cannot be null or empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(edt_register_password.getText().toString())) {
            Toast.makeText(SignUp.this, "Password cannot be null or empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!edt_register_password_confirm.getText().toString().equals(edt_register_password.getText().toString())) {
            Toast.makeText(SignUp.this, "Confirm Password is wrong", Toast.LENGTH_SHORT).show();
            return;
        }


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
                        if (s.equals("\"Registration success\""))
                        {
                            //成功注册
                            Toast.makeText(SignUp.this,s, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUp.this, Login.class);
                            intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        else
                        {
                            //邮箱已存在
                            Toast.makeText(SignUp.this,s, Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {
                        //Didn't connect to Internet
                        Toast.makeText(SignUp.this,"请检查网络", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onComplete() {
                    }
                });
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