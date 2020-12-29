package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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
import com.example.myapplication.Retrofit.IMyService;
import com.example.myapplication.Retrofit.RetrofitClient;
import com.example.myapplication.Retrofit.RetrofitClientGson;
import com.example.myapplication.Retrofit.RetrofitClientGson2;
import com.example.myapplication.adpater.CategoryCardAdapter;
import com.example.myapplication.adpater.ChannelCardAdapter;
import com.example.myapplication.adpater.HideScrollListener;
import com.example.myapplication.adpater.HomeCategoryAdapter;
import com.example.myapplication.model.CategoryCard;
import com.example.myapplication.model.ChannelVideo;
import com.example.myapplication.model.VideoInMonthInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.PieChartView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class Channel extends AppCompatActivity implements CategoryCardAdapter.OnCardListener {

    private static final String TAG = "成功";

    CardView cv_channel;
    ImageView img_card, img_avatar;
    TextView tv_channelName, tv_category, tv_subscribeValue, tv_viewValue, tv_channelVideoCountValue;
    RecyclerView categoriesRecyclerView;

    // 初始化网络连接服务(呼叫后端用的 service)
    IMyService iMyService;

    CategoryCard categoryCards;
    CategoryCardAdapter categoriesCardAdapter;
    ChannelVideo channelVideo;

    private PieChartView chart;
    private PieChartData data;

    private boolean hasLabels = true;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = true;
    private boolean hasCenterText1 = true;
    private boolean hasCenterText2 = true;
    private boolean isExploded = false;
    private boolean hasLabelForSelected = false;
    VideoInfoEnum videoInfoEnum = VideoInfoEnum.VIEW;

    public enum VideoInfoEnum {
        VIEW,
        COMMENT,
        LIKE,
        DISLIKE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        //从Intent 取得 data
        Intent intent = getIntent();

        String channelName = intent.getStringExtra("channelName");
        String category = intent.getStringExtra("category");
        String subscribeValue = intent.getStringExtra("subscribeValue");
        String viewValue = intent.getStringExtra("viewValue");
        String medium_photo_url = intent.getStringExtra("thumbnail").replaceFirst("hq","mq");
        String avatar_url = intent.getStringExtra("avatar");
        if (subscribeValue.equals("-1")) subscribeValue = "未公開";
        //用LinearLayoutManager将RecyclerView显示方式转为Horizontal
        categoriesRecyclerView = findViewById(R.id.channelCategoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //绑定 View
        cv_channel = findViewById(R.id.cardChannel);
        img_card = findViewById(R.id.imgChannel);
        img_avatar = findViewById(R.id.imgChannelAvatar);
        tv_channelName = findViewById(R.id.channelName);
        tv_category = findViewById(R.id.channelCategory);
        tv_subscribeValue = findViewById(R.id.channelSubValue);
        tv_viewValue = findViewById(R.id.channelViewValue);
        tv_channelVideoCountValue = findViewById(R.id.channelVideoCountValue);
        tv_channelName.setText(channelName);
        category = category.equals("NULL")? "未分類":category;
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

        //设置顶部channel card 点击事件
        setChannelCardTouch();

        //初始化 categories card 的 RecyclerView
        String channelId = intent.getStringExtra("channelId");
        // 宣告 Retrofit 进行网络链接,并取得服务

        iMyService = RetrofitClientGson.getInstance().create(IMyService.class);
        if (channelId != null) {
            categoriesCardRecyclerViewInit(channelId);  //初始化 Categories 的卡片
            channelVideoChart(channelId);    //初始化饼图
        }

        chart = (PieChartView) findViewById(R.id.pitChartView);
        chart.setOnValueTouchListener(new ValueTouchListener());
        chart.showContextMenu();

    }

    //频道视频饼图初始化
    private void channelVideoChart(String channelId) {
        Log.d(TAG, "channelVideoChart: 初始化饼图");
        Call<ChannelVideo> call = iMyService.getChannelVideo(channelId);
        call.enqueue(new Callback<ChannelVideo>() {
            @Override
            public void onResponse(Call<ChannelVideo> call, Response<ChannelVideo> response) {
                if (response.body() != null) {
                    channelVideo = response.body();
                    generateData(channelVideo.getVideoInMonthInfo());
                }
            }
            @Override
            public void onFailure(Call<ChannelVideo> call, Throwable t) {
                Toast.makeText(Channel.this,"加載失敗,可能是網絡問題", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateData(List<VideoInMonthInfo> videoInMonthInfos) {

        int numValues = videoInMonthInfos.size();
        if (numValues == 0) {
            Toast.makeText(Channel.this,"近一個月無任何投稿", Toast.LENGTH_SHORT).show();
            return;
        }
        List<SliceValue> values = new ArrayList<SliceValue>();
        for (int i = 0; i < numValues; ++i) {
            VideoInMonthInfo videoInMonthInfo = videoInMonthInfos.get(i);
            int j = i % 5;
            SliceValue sliceValue = new SliceValue((float) Math.random() * 100 + 15, ChartUtils.COLORS[j]);
            switch (videoInfoEnum) {
                case VIEW:
                    sliceValue.setTarget(Float.valueOf(videoInMonthInfo.getViewCount()));
                    break;
                case COMMENT:
                    sliceValue.setTarget(Float.valueOf(videoInMonthInfo.getCommentCount()));
                    break;
                case LIKE:
                    sliceValue.setTarget(Float.valueOf(videoInMonthInfo.getLikeCount()));
                    break;
                case DISLIKE:
                    sliceValue.setTarget(Float.valueOf(videoInMonthInfo.getDislikeCount()));
                    break;
            }
            ((ArrayList) values).add(sliceValue);
        }

        data = new PieChartData(values);
        //切换饼图显示的内容
        switch (videoInfoEnum) {
            case VIEW:
                data.setCenterText1("觀看數");
                videoInfoEnum = VideoInfoEnum.COMMENT;
                break;
            case COMMENT:
                data.setCenterText1("評論數");
                videoInfoEnum = VideoInfoEnum.LIKE;
                break;
            case LIKE:
                data.setCenterText1("喜歡數");
                videoInfoEnum = VideoInfoEnum.DISLIKE;
                break;
            case DISLIKE:
                data.setCenterText1("不喜歡數");
                videoInfoEnum = VideoInfoEnum.VIEW;
                break;
        }
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        data.setHasLabelsOutside(hasLabelsOutside);
        data.setHasCenterCircle(hasCenterCircle);
        data.setCenterText1Typeface(Typeface.DEFAULT);
        data.setCenterText2Typeface(Typeface.DEFAULT);
        data.setCenterText1FontSize(32);
        data.setCenterText2FontSize(16);
        data.setCenterText2Color(R.color.grey);
        data.setCenterText2("點擊查看更多");

        //初始化可視化Charts
        chart.setPieChartData(data);
        chart.startDataAnimation();
    }

    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            //点击饼图的某一部分时
            //当饼图确实存在至少1块,产生提示,show出完整视频信息
            if (channelVideo.getVideoInMonthInfo().size() != 0) {
                VideoInMonthInfo videoInMonthInfo = channelVideo.getVideoInMonthInfo().get(arcIndex);
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        generateData(channelVideo.getVideoInMonthInfo());
                    }
                };
                String str = "数据更新于" + videoInMonthInfo.getDatatime() +
                        "\n发布日期:" + videoInMonthInfo.getPublishedAt() +
                        "\n標題:" + videoInMonthInfo.getTitle() +
                        "\n分類:" + videoInMonthInfo.getCategoryId() +
                        "\n觀看:" + videoInMonthInfo.getViewCount() +
                        "\n評論:" + videoInMonthInfo.getCommentCount() +
                        "\n喜歡:" + videoInMonthInfo.getLikeCount() +
                        "\n不喜歡:" + videoInMonthInfo.getDislikeCount();
                new AlertDialog.Builder(Channel.this)
                        .setTitle("視頻詳情")
                        .setMessage(str)
                        .setPositiveButton("切換餅圖", onClickListener)
                        .setNegativeButton("關閉", null)
                        .show();

            } else Toast.makeText(Channel.this, "近一個月無任何投稿", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }
    }

    private void setChannelCardTouch() {
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

    public void categoriesCardRecyclerViewInit(String channelId) {
        //横向Category Card 的内容
        Log.d(TAG, "categoriesCardRecyclerViewInit: 卡片初始化");
        Log.d(TAG, "categoriesCardRecyclerViewInit: 频道ID是:" + channelId);

        Call<CategoryCard> call = iMyService.getChannelInfo(channelId);
        call.enqueue(new Callback<CategoryCard>() {
            @Override
            public void onResponse(Call<CategoryCard> call, Response<CategoryCard> response) {
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: 成功连接server" + response.body());
                    categoryCards = response.body();
                    tv_channelVideoCountValue.setText(categoryCards.getAllvideoCount());

                    cardAdapterInit();
                }
                else {
                    Log.d(TAG, "onResponse: 成功连接server, 但无回传值");
                }
            }

            @Override
            public void onFailure(Call<CategoryCard> call, Throwable t) {
                Log.d(TAG, "onResponse: 无法连接到server");

            }
        });

    }
    public void cardAdapterInit() {
        Log.d(TAG, "initCardAdapter: 成功初始化Adapter, 当前拥有categories:" + categoryCards.getCategoryIds().size() + "张");
        //重设 Adapter
        categoriesCardAdapter = new CategoryCardAdapter(this, categoryCards, this);
        categoriesRecyclerView.setAdapter(categoriesCardAdapter);
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

    @Override
    public void onCardClick(int position) {

    }
    @Override
    public boolean onCardTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            v.animate().scaleX(0.96f).setDuration(130).start();
            v.animate().scaleY(0.96f).setDuration(130).start();

        } else {
            v.animate().cancel();
            v.animate().scaleX(1f).setDuration(150).start();
            v.animate().scaleY(1f).setDuration(150).start();
        }

        return true;  //this should be true to get further event's
    }
}