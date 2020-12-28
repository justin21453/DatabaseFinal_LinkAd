package com.example.myapplication.Retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class RetrofitClientGson2 {
    private static Retrofit instance;
    private static final String BASE_URL = "http://140.136.151.145:3000/";

    public static Retrofit getInstance(){
        //我们使用 converter 转换为JSON
        if(instance == null)
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        return  instance;
    }

}
