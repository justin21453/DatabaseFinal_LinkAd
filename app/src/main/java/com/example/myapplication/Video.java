package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.myapplication.Retrofit.IMyService;
import com.example.myapplication.Retrofit.RetrofitClientGson;
import com.example.myapplication.model.CategoryCard;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class Video extends AppCompatActivity {

    private static final String TAG = "成功";

    private String channelId;
    private String channelName;
    private String medium_photo_url;

    //Video 卡片
//    CardView cv_video;
    ImageView img_video;
    TextView tv_category, tv_videoUpdateTimeValue, tv_videoPublishedTimeValue,
                tv_videoTitleValue, tv_videoViewValue, tv_videoCommentValue, tv_videoLikeValue,tv_videoDislikeValue;
    Button btn_youtube;
    ConstraintLayout btnYouTubeConstraintLayout;

    //前往 Channel 卡片
    CardView cv_goToChannelCard;
    ImageView img_goToChannelAvatar;
    TextView tv_goToChannelName;

    IMyService iMyService;
    Call<CategoryCard> callGoToChannelCard;
    boolean loadMoreState = false;

    CategoryCard channelCard;
    String videoUrl, channelUrl, avatarUrl;


    //图片加载设置
    DrawableCrossFadeFactory factory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
    RequestOptions requestOptions = new RequestOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        iMyService = RetrofitClientGson.getInstance().create(IMyService.class);

        getDataAndBindView();
        setGoToChannelCard();
        setButtonClick();
        goToChannelCardInit();

    }

    private void setGoToChannelCard() {
        callGoToChannelCard = iMyService.getChannelInfo(channelId);
        callGoToChannelCard.enqueue(new Callback<CategoryCard>() {
            @Override
            public void onResponse(Call<CategoryCard> call, Response<CategoryCard> response) {
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: 成功连接server,取得Categories");
                    channelCard = response.body();
                    channelUrl = channelCard.getChannelUrl();
                    avatarUrl = channelCard.getAvatarUrlHigh();
                    goToChannelCardInit();
                }
            }
            @Override
            public void onFailure(Call<CategoryCard> call, Throwable t) {
                if (!loadMoreState) {
                    loadMoreState = true;
                    if (t != null && (t instanceof IOException || t instanceof SocketTimeoutException || t instanceof ConnectException)) {
                        if (t instanceof SocketTimeoutException || t instanceof TimeoutException) {
                            Toast.makeText(getApplicationContext(),"出了點意外", Toast.LENGTH_SHORT).show();
                            //avsInterface.onError(call, new AvsException("Oops something went wrong, please try again later..."));
                        } else {
                            Toast.makeText(getApplicationContext(),"請檢查你的網絡", Toast.LENGTH_SHORT).show();
                            //avsInterface.onError(call, new AvsException("Please check your internet connection..."));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),"出了點意外", Toast.LENGTH_SHORT).show();
                    }
                }
                setGoToChannelCard();
            }
        });
    }

    //TODO: 初始化 前往頻道 Card
    private void goToChannelCardInit() {
        Glide.with(getApplicationContext()).load(avatarUrl)
                .apply(requestOptions)
                .transition(withCrossFade(factory))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.shape_avatar_placeholder)
                .into(img_goToChannelAvatar);

        cv_goToChannelCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //卡片点击事件: 获取对应卡片Data, 传到新开启的Channel Activity
                if (channelCard == null) {
                    Toast.makeText(getApplicationContext(),"請檢查你的網絡", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), Channel.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("channelId", channelId);
                    intent.putExtra("channelName", channelName);
                    intent.putExtra("category", channelCard.getCategoryIds().get(0).getId());
                    intent.putExtra("subscribeValue", channelCard.getSubscriber());
                    intent.putExtra("viewValue", channelCard.getAllviewCount());
                    intent.putExtra("avatar", avatarUrl);

                    //开启新的Activity
                    startActivity(intent);
                }
            }
        });
    }
    private void getDataAndBindView() {
        tv_goToChannelName = findViewById(R.id.goToChannelName);
        img_goToChannelAvatar = findViewById(R.id.goToChannelAvatar);

        img_video = findViewById(R.id.imgVideoPageVideo);
        tv_videoUpdateTimeValue = findViewById(R.id.videoUpdateTimeValue);
        tv_videoPublishedTimeValue = findViewById(R.id.videoPublishedTimeValue);
        tv_videoTitleValue = findViewById(R.id.videoPageVideoTitleValue);
        tv_category = findViewById(R.id.videoCategory);
        tv_videoViewValue = findViewById(R.id.videoViewValue);
        tv_videoCommentValue = findViewById(R.id.videoCommentValue);
        tv_videoLikeValue = findViewById(R.id.videoLikeValue);
        tv_videoDislikeValue = findViewById(R.id.videoDislikeValue);
        btn_youtube = findViewById(R.id.btnYouTube);
        btnYouTubeConstraintLayout = findViewById(R.id.btnYouTubeConstraintLayout);



        cv_goToChannelCard = findViewById(R.id.goToChannelCard);

        Intent intent = getIntent();
        channelId = intent.getStringExtra("channelId");
        channelName = intent.getStringExtra("channelName");
        tv_goToChannelName.setText(channelName);

        String thumbnail = intent.getStringExtra("thumbnail");
        String videoTitle = intent.getStringExtra("videoTitle");
        String updateTime = intent.getStringExtra("updateTime");
        String publishedTime = intent.getStringExtra("publishedTime");
        String viewValue = intent.getStringExtra("viewValue");
        String category = intent.getStringExtra("category");
        String likeValue = intent.getStringExtra("likeValue");
        String dislikeValue = intent.getStringExtra("dislikeValue");
        String commentValue = intent.getStringExtra("commentValue");
        videoUrl = intent.getStringExtra("videoUrl");



        tv_videoUpdateTimeValue.setText(updateTime);
        tv_videoPublishedTimeValue.setText(publishedTime);
        tv_videoTitleValue.setText(videoTitle);
        tv_category.setText(category);
        tv_videoViewValue.setText(viewValue);
        tv_videoCommentValue.setText(commentValue);
        tv_videoLikeValue.setText(likeValue);
        tv_videoDislikeValue.setText(dislikeValue);


        requestOptions.circleCropTransform();
        requestOptions.transform( new RoundedCorners(30));  //设置圆角Corner大小
        medium_photo_url = thumbnail.replaceFirst("hq","mq");
        //load()放图片URL ("https://")
        Glide.with(getApplicationContext()).load(medium_photo_url)
                .apply(requestOptions)
                .transition(withCrossFade(factory))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.shape_avatar_placeholder)
                .into(img_video);



    }
    @SuppressLint("ClickableViewAccessibility")
    private void setButtonClick() {
        btn_youtube.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    btnYouTubeConstraintLayout.animate().translationY(6f).setDuration(0).start();
                    btnYouTubeConstraintLayout.setBackground(getDrawable(R.color.transparent));

                } else {
                    v.animate().cancel();
                    btnYouTubeConstraintLayout.animate().translationY(0).setDuration(0).start();
                    btnYouTubeConstraintLayout.setBackground(getDrawable(R.drawable.btn_bg_14dp_green));
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));

                }

                return true;
            }
        });
    }

    //改变进出场动画效果
    @Override
    protected void onStart() {
        super.onStart();
//        overridePendingTransition(R.anim.enter_channel_page_animation, R.anim.hold_animation);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadMoreState = true;
        if (callGoToChannelCard != null)callGoToChannelCard.cancel();
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(0, R.anim.leave_channel_page_animation);
    }
}