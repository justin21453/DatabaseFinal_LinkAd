package com.example.myapplication.Retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class RetrofitClientGson {
    private static Retrofit instance;
    private static final String BASE_URL = "http://140.136.151.145:3000/";

    public static Retrofit getInstance() {
        if(instance == null)
            instance = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                    .build();
        return  instance;
    }
}
