package com.example.myapplication.Retrofit;

import com.example.myapplication.Channel;
import com.example.myapplication.model.CategoryCard;
import com.example.myapplication.model.ChannelCard;
import com.example.myapplication.model.ChannelMostVideo;
import com.example.myapplication.model.ChannelRecentVideo;
import com.example.myapplication.model.VideoCard;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IMyService {
    @POST("register")
    @FormUrlEncoded
    Observable<String> registerUser(@Field("email") String email,
                                    @Field("name") String name,
                                    @Field("password") String password);
    @POST("login")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("email") String email,
                                 @Field("password") String password);
    @POST("searchC")
    @FormUrlEncoded
    Call<ArrayList<ChannelCard>> searchChannel(@Field("text") String text, @Field("skip") int skip, @Field("limit") int limit);

    @POST("searchV")
    @FormUrlEncoded
    Call<ArrayList<VideoCard>> searchVideo(@Field("text") String text, @Field("skip") int skip, @Field("limit") int limit);

    @GET("home")
    Call<ArrayList<ChannelCard>> getAllChannelCards(@Header("limit") int limit);

    @GET("channelInfo")
    Call<CategoryCard> getChannelInfo(@Header("channelId") String channelId );

    @GET("channelRecentVideo")
    Call<ArrayList<ChannelRecentVideo>> getChannelRecentVideo(@Header("channelId") String channelId );

    @GET("channelMostVideo")
    Call<ChannelMostVideo> getChannelMostVideo(@Header("channelId") String channelId );

}
