package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class Channel extends AppCompatActivity {

    private static final String TAG = "成功";

    CardView cv_channel;
    ImageView img_card, img_avatar;
    TextView tv_channelName, tv_category, tv_subscribeValue, tv_viewValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        //绑定
        cv_channel = findViewById(R.id.cardChannel);
        img_card = findViewById(R.id.imgChannel);
        img_avatar = findViewById(R.id.imgChannelAvatar);
        tv_channelName = findViewById(R.id.channelName);
        tv_category = findViewById(R.id.category);
        tv_subscribeValue = findViewById(R.id.subscribeValue);
        tv_viewValue = findViewById(R.id.viewValue);

        Intent intent = getIntent();

        String channelId = intent.getStringExtra("channelId");
        String channelName = intent.getStringExtra("channelName");
        String category = intent.getStringExtra("category");
        String subscribeValue = intent.getStringExtra("subscribeValue");
        String viewValue = intent.getStringExtra("viewValue");
        String medium_photo_url = intent.getStringExtra("thumbnail").replaceFirst("hq","mq");
        String avatar_url = intent.getStringExtra("avatar");
        if (subscribeValue.equals("-1")) subscribeValue = "未公開";
        tv_channelName.setText(channelName);
        tv_category.setText(category);
        tv_subscribeValue.setText(subscribeValue);
        tv_viewValue.setText(viewValue);

        //图片加载设置
        DrawableCrossFadeFactory factory =
                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        RequestOptions requestOptions = new RequestOptions();
        //当图片没加载出来时候,显示占位符
        requestOptions.circleCropTransform();
        requestOptions.transform( new RoundedCorners(30));  //设置圆角Corner大小
        //load()可以放图片URL ("https://")
        Glide.with(this).load(medium_photo_url)
                .apply(requestOptions)
                .transition(withCrossFade(factory))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.video_image)
                .into(img_card);
        Glide.with(this).load(avatar_url)
                .apply(requestOptions)
                .transition(withCrossFade(factory))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.avatar_image)
                .into(img_avatar);

        cv_channel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    v.animate().scaleX(0.94f).setDuration(130).start();
                    v.animate().scaleY(0.94f).setDuration(130).start();

                } else {
                    v.animate().cancel();
                    v.animate().scaleX(1f).setDuration(130).start();
                    v.animate().scaleY(1f).setDuration(130).start();
                }

                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.enter_channel_page_animation, R.anim.hold_animation);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.leave_channel_page_animation);
    }

}