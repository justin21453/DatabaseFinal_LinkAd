package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.myapplication.Retrofit.IMyService;
import com.example.myapplication.Retrofit.RetrofitClient;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SignUp extends AppCompatActivity {

    TextView txt_have_account;
    MaterialEditText edt_register_email, edt_register_password, edt_register_name, edt_register_password_confirm;
    Button btn_SignUp;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

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
                startActivity(intent);
            }
        });

    }

    private void registerUser(String email, String name, String password) {
        compositeDisposable.add(iMyService.registerUser(email,name,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(SignUp.this, ""+response, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

}