package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.example.myapplication.adpater.CategoryCardAdapter;
import com.example.myapplication.model.CategoryCard;
import com.example.myapplication.model.ChannelMostVideo;
import com.example.myapplication.model.ChannelRecentVideo;
import com.example.myapplication.model.VideoMostCommentInfo;
import com.example.myapplication.model.VideoMostLikeInfo;
import com.example.myapplication.model.VideoMostViewInfo;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class Channel extends AppCompatActivity implements CategoryCardAdapter.OnCardListener {

    private static final String TAG = "成功";

    CardView cv_channel;
    ImageView img_avatar;
    TextView tv_channelName, tv_category, tv_channelStartTime, tv_subscribeValue, tv_viewValue, tv_channelVideoCountValue;
    RecyclerView categoriesRecyclerView;
    Button btn_changePieChart;
    //3种Video Card
    CardView cv_mostView, cv_mostComment, cv_mostLike;
    ImageView img_mostView, img_mostComment, img_mostLike;
    TextView tv_mostViewValue;
    TextView  tv_mostViewTag_1, tv_mostViewTag_2, tv_mostViewTag_3,
            tv_mostCommentValue, tv_mostCommentTag_1, tv_mostCommentTag_2, tv_mostCommentTag_3,
            tv_mostLikeValue, tv_mostLikeTag_1, tv_mostLikeTag_2, tv_mostLikeTag_3;

    // 初始化网络连接服务(呼叫后端用的 service)
    IMyService iMyService;

    CategoryCard categoryCards;
    CategoryCardAdapter categoriesCardAdapter;
    ArrayList<ChannelRecentVideo> channelRecentVideos;
    ChannelMostVideo channelMostVideo;

    private String channelId;

    private PieChartView chart;
    private PieChartData data;

    private boolean hasLabels = true;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = true;
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
        getDataAndBindView();
        //设置顶部channel card 点击事件
        setChannelCardTouch();

        // 宣告 Retrofit 进行网络链接,并取得服务
        iMyService = RetrofitClientGson.getInstance().create(IMyService.class);
        if (channelId != null) {
            Log.d(TAG, "onCreate: 频道ID是:" + channelId);
            categoriesCardRecyclerViewInit(channelId);  //初始化 Categories 的卡片
            channelVideoChart(channelId);               //初始化饼图
            getChannelMostVideo(channelId);             //取得Channel最多观看，评论，Like的Video
        }

        chart = (PieChartView) findViewById(R.id.pitChartView);
        chart.setOnValueTouchListener(new ValueTouchListener());
        chart.showContextMenu();

    }

    private void getDataAndBindView() {
        Intent intent = getIntent();
        String channelName = intent.getStringExtra("channelName");
        String category = intent.getStringExtra("category");
        String subscribeValue = intent.getStringExtra("subscribeValue");
        String viewValue = intent.getStringExtra("viewValue");
        String avatar_url = intent.getStringExtra("avatar");
        if (subscribeValue.equals("-1")) subscribeValue = "未公開";
        //用LinearLayoutManager将RecyclerView显示方式转为Horizontal
        categoriesRecyclerView = findViewById(R.id.channelCategoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //绑定 View
        cv_channel = findViewById(R.id.cardChannel);
        img_avatar = findViewById(R.id.imgChannelAvatar);
        tv_channelName = findViewById(R.id.videoTitle);
        tv_category = findViewById(R.id.channelCategory);
        tv_channelStartTime = findViewById(R.id.channelStartTime);
        tv_subscribeValue = findViewById(R.id.channelSubValue);
        tv_viewValue = findViewById(R.id.channelViewValue);
        tv_channelVideoCountValue = findViewById(R.id.channelVideoCountValue);
        tv_channelName.setText(channelName);
        category = category.equals("NULL")? "未分類":category;
        tv_category.setText(category);
        tv_subscribeValue.setText(subscribeValue);
        tv_viewValue.setText(viewValue);

        btn_changePieChart = findViewById(R.id.btn_changePieChart);

        cv_mostView = findViewById(R.id.cardMostViewVideo);
        cv_mostComment = findViewById(R.id.cardMostCommentVideo);
        cv_mostLike = findViewById(R.id.cardMostLikeVideo);
        tv_mostViewValue = findViewById(R.id.tvMostViewVideoViewValue);
        tv_mostCommentValue = findViewById(R.id.tvMostCommentVideoViewValue);
        tv_mostLikeValue = findViewById(R.id.tvMostLikeValue);

        img_mostView = findViewById(R.id.imgMostViewVideo);
        img_mostComment = findViewById(R.id.imgMostCommentVideo);
        img_mostLike = findViewById(R.id.imgMostLikeVideo);

        tv_mostViewTag_1 = findViewById(R.id.tvMostViewTag_1);
        tv_mostViewTag_2 = findViewById(R.id.tvMostViewTag_2);
        tv_mostViewTag_3 = findViewById(R.id.tvMostViewTag_3);
        tv_mostCommentTag_1 = findViewById(R.id.tvMostCommentTag_1);
        tv_mostCommentTag_2 = findViewById(R.id.tvMostCommentTag_2);
        tv_mostCommentTag_3 = findViewById(R.id.tvMostCommentTag_3);
        tv_mostLikeTag_1 = findViewById(R.id.tvMostLikeTag_1);
        tv_mostLikeTag_2 = findViewById(R.id.tvMostLikeTag_2);
        tv_mostLikeTag_3 = findViewById(R.id.tvMostLikeTag_3);

        //图片加载设置
        DrawableCrossFadeFactory factory =
                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        RequestOptions requestOptions = new RequestOptions();
        //当图片没加载出来时候,显示占位符
        requestOptions.circleCropTransform();
        requestOptions.transform( new RoundedCorners(30));  //设置圆角Corner大小
        //load()可以放图片URL ("https://")
        Glide.with(this).load(avatar_url)
                .apply(requestOptions)
                .transition(withCrossFade(factory))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.shape_avatar_placeholder)
                .into(img_avatar);

        //初始化 categories card 的 RecyclerView
        channelId = intent.getStringExtra("channelId");
    }

    //向Server请求ChannelVideo的信息, 并渲染饼图
    private void getChannelMostVideo(String channelId) {
        Log.d(TAG, "getChannelMostVideo: 初始化Most系列视频");
        Call<ChannelMostVideo> call = iMyService.getChannelMostVideo(channelId);
        call.enqueue(new Callback<ChannelMostVideo>() {
            @Override
            public void onResponse(Call<ChannelMostVideo> call, Response<ChannelMostVideo> response) {
                if (response.body() != null) {
                    channelMostVideo = response.body();
                    setMostVideoValue();
                    Log.d(TAG, "onResponse: 成功连接server,取得ChannelMostVideo");
                }
            }
            @Override
            public void onFailure(Call<ChannelMostVideo> call, Throwable t) {
                Toast.makeText(Channel.this,"加載失敗,可能是網絡問題", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //设置3个most video 卡片内容
    private void setMostVideoValue() {
        VideoMostViewInfo videoMostViewInfo = channelMostVideo.getVideoMostViewInfo();
        VideoMostCommentInfo videoMostCommentInfo = channelMostVideo.getVideoMostCommentInfo();
        VideoMostLikeInfo videoMostLikeInfo = channelMostVideo.getVideoMostLikeInfo();
        tv_mostViewValue.setText(videoMostViewInfo.getViewCount());
        tv_mostCommentValue.setText(videoMostCommentInfo.getCommentCount());
        tv_mostLikeValue.setText(videoMostLikeInfo.getLikeCount());

        String medium_photo_url;

        //图片加载设置
        DrawableCrossFadeFactory factory =
                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        RequestOptions requestOptions = new RequestOptions();
        //当图片没加载出来时候,显示占位符
        requestOptions.circleCropTransform();
        requestOptions.transform( new RoundedCorners(30));  //设置圆角Corner大小
        //load()可以放图片URL ("https://")
        medium_photo_url = videoMostViewInfo.getThumbnailsUrlHigh().replaceFirst("hq","mq");
        Glide.with(this).load(medium_photo_url)
                .apply(requestOptions)
                .transition(withCrossFade(factory))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.shape_thumbnail_placeholder)
                .into(img_mostView);
        medium_photo_url = videoMostCommentInfo.getThumbnailsUrlHigh().replaceFirst("hq","mq");
        Glide.with(this).load(medium_photo_url)
                .apply(requestOptions)
                .transition(withCrossFade(factory))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.shape_thumbnail_placeholder)
                .into(img_mostComment);
        medium_photo_url = videoMostLikeInfo.getThumbnailsUrlHigh().replaceFirst("hq","mq");
        Glide.with(this).load(medium_photo_url)
                .apply(requestOptions)
                .transition(withCrossFade(factory))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.shape_thumbnail_placeholder)
                .into(img_mostLike);

        if (videoMostViewInfo.getTag() != null && videoMostViewInfo.getTag().size() > 0) tagBinding(tv_mostViewTag_1, tv_mostViewTag_2, tv_mostViewTag_3, videoMostViewInfo.getTag());
        else {
            tv_mostViewTag_2.setVisibility(View.INVISIBLE);
            tv_mostViewTag_3.setVisibility(View.INVISIBLE);
        }
        if (videoMostCommentInfo.getTag() != null && videoMostCommentInfo.getTag().size() > 0) tagBinding(tv_mostCommentTag_1, tv_mostCommentTag_2, tv_mostCommentTag_3, videoMostCommentInfo.getTag());
        else {
            tv_mostCommentTag_2.setVisibility(View.INVISIBLE);
            tv_mostCommentTag_3.setVisibility(View.INVISIBLE);
        }
        if (videoMostLikeInfo.getTag() != null && videoMostLikeInfo.getTag().size() > 0) tagBinding(tv_mostLikeTag_1, tv_mostLikeTag_2, tv_mostLikeTag_3, videoMostLikeInfo.getTag());
        else {
            tv_mostLikeTag_2.setVisibility(View.INVISIBLE);
            tv_mostLikeTag_3.setVisibility(View.INVISIBLE);
        }
    }
    //绑定tag到3个tag_n上, 当tag不到3个或者tag的文字太长时, 进行一些处理
    public void tagBinding(TextView tags_1, TextView tags_2, TextView tags_3, List<String> tag) {
        tags_1.setText(tag.get(0));
        tags_1.setSingleLine(true);
        tags_2.setSingleLine(true);
        tags_3.setSingleLine(true);

        tags_1.setMaxEms(10);
        if(tag.size()>=3){
            tags_2.setVisibility(View.VISIBLE);
            tags_2.setText(tag.get(1));
            tags_1.setMaxEms(6);
            tags_2.setMaxEms(6);
            String str = "" + tag.get(0) + tag.get(1);
            int str_tag_2 = str.length();
            str += tag.get(2);
            int str_tag_3 = str.length();
            if (str_tag_3 > 12) {
                tags_3.setVisibility(View.INVISIBLE);
            } else {
                tags_3.setVisibility(View.VISIBLE);
                if (str_tag_2 > 12) {
                    tags_1.setMaxEms(4);
                    tags_2.setMaxEms(4);
                }
                tags_3.setText(tag.get(2));
                tags_3.setMaxEms(4);
            }
        } else if(tag.size()>=2){
            String str = "" + tag.get(0) + tag.get(1);
            tags_1.setMaxEms(6);
            tags_2.setMaxEms(6);
            if (str.length() > 12) {
                tags_1.setMaxEms(5);
                tags_2.setMaxEms(5);
            }
            tags_2.setText(tag.get(1));
            tags_2.setVisibility(View.VISIBLE);
            tags_3.setVisibility(View.INVISIBLE);
        } else if(tag.size()==1){
            tags_2.setVisibility(View.INVISIBLE);
            tags_3.setVisibility(View.INVISIBLE);

        }
    }

    //向Server请求ChannelInfo, 并将Categories 横向 RecyclerView 初始化, 设置ChannelCard
    public void categoriesCardRecyclerViewInit(String channelId) {
        //横向Category Card 的内容
        Log.d(TAG, "categoriesCardRecyclerViewInit: 初始化卡片");

        Call<CategoryCard> call = iMyService.getChannelInfo(channelId);
        call.enqueue(new Callback<CategoryCard>() {
            @Override
            public void onResponse(Call<CategoryCard> call, Response<CategoryCard> response) {
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: 成功连接server,取得Categories");
                    categoryCards = response.body();
                    tv_channelVideoCountValue.setText(categoryCards.getAllvideoCount());                       //获得总视频数量
                    tv_channelStartTime.setText("創建於 " + categoryCards.getPublishedAt().substring(0, 10));  //获得频道创建日期
                    categoriesCardAdapterInit();
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
    // 初始化 Categories 卡片
    public void categoriesCardAdapterInit() {
        Log.d(TAG, "initCardAdapter: 成功初始化Adapter, 当前拥有categories:" + categoryCards.getCategoryIds().size() + "张");
        //重设 Adapter
        categoriesCardAdapter = new CategoryCardAdapter(this, categoryCards, this);
        categoriesRecyclerView.setAdapter(categoriesCardAdapter);
    }
    // Category 卡片的点击事件
    @Override
    public void onCardClick(int position) {
    }
    // Category 卡片的触摸事件
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

    //向Server请求ChannelVideo的信息, 并渲染饼图
    private void channelVideoChart(String channelId) {
        Log.d(TAG, "channelVideoChart: 初始化饼图");
        Call<ArrayList<ChannelRecentVideo>> call = iMyService.getChannelRecentVideo(channelId);
        call.enqueue(new Callback<ArrayList<ChannelRecentVideo>>() {
            @Override
            public void onResponse(Call<ArrayList<ChannelRecentVideo>> call, Response<ArrayList<ChannelRecentVideo>> response) {
                if (response.body() != null) {
                    channelRecentVideos = response.body();
                    generateData(channelRecentVideos);
                    Log.d(TAG, "onResponse: 成功连接server,取得ChannelRecentVideo");
                }
            }
            @Override
            public void onFailure(Call<ArrayList<ChannelRecentVideo>> call, Throwable t) {
                Toast.makeText(Channel.this,"加載失敗,可能是網絡問題", Toast.LENGTH_SHORT).show();
            }
        });

    }
    //生成饼图 & 刷新饼图(都是用该 Function)
    private void generateData(ArrayList<ChannelRecentVideo> channelRecentVideos) {

        int numValues = channelRecentVideos.size();
        if (numValues == 0) {
            Toast.makeText(Channel.this,"近一個月無任何投稿", Toast.LENGTH_SHORT).show();
            return;
        }
        List<SliceValue> values = new ArrayList<SliceValue>();
        for (int i = 0; i < numValues; ++i) {
            ChannelRecentVideo channelRecentVideo = channelRecentVideos.get(i);
            int j = i % 5;
            SliceValue sliceValue = new SliceValue((float) Math.random() * 60 + 15, ChartUtils.COLORS[j]);
            switch (videoInfoEnum) {
                case VIEW:
                    sliceValue.setTarget(Float.valueOf(channelRecentVideo.getViewCount()));
                    break;
                case COMMENT:
                    sliceValue.setTarget(Float.valueOf(channelRecentVideo.getCommentCount()));
                    break;
                case LIKE:
                    sliceValue.setTarget(Float.valueOf(channelRecentVideo.getLikeCount()));
                    break;
                case DISLIKE:
                    sliceValue.setTarget(Float.valueOf(channelRecentVideo.getDislikeCount()));
                    break;
            }
            ((ArrayList) values).add(sliceValue);
        }

        data = new PieChartData(values);
        data.setCenterText1Typeface(Typeface.DEFAULT);
        data.setCenterText2Typeface(Typeface.DEFAULT);
        data.setCenterText1Color(R.color.white);
        data.setCenterText1FontSize(36);
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


        //初始化可視化Charts
        chart.setPieChartData(data);
        chart.startDataAnimation();
    }
    //饼图点击监听器
    private class ValueTouchListener implements PieChartOnValueSelectListener {
        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            //当饼图确实存在至少1块,产生提示,show出完整视频信息
            if (channelRecentVideos.size() != 0) {
                ChannelRecentVideo channelRecentVideo = channelRecentVideos.get(arcIndex);
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(channelRecentVideo.getVideoUrl())));
                    }
                };

                String str = "数据更新于" + channelRecentVideo.getDatatime() +
                        "\n影片发布日期:" + channelRecentVideo.getPublishedAt().substring(0, 10) +
                        "\n標題:" + channelRecentVideo.getTitle() +
                        "\n類型:" + channelRecentVideo.getCategoryId() +
                        "\n觀看:" + channelRecentVideo.getViewCount() +
                        "\n評論:" + channelRecentVideo.getCommentCount() +
                        "\n喜歡:" + channelRecentVideo.getLikeCount() +
                        "\n不喜歡:" + channelRecentVideo.getDislikeCount();


                AlertDialog.Builder builder = new AlertDialog.Builder(Channel.this);
                        builder.setTitle("視頻詳情");
                        builder.setMessage(str);
                        builder.setPositiveButton("前往YouTube", onClickListener);
                        builder.setNegativeButton("關閉", null);
                        builder.show();

            } else Toast.makeText(Channel.this, "近一個月無任何投稿", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }
    }


    //Categories 顶部频道卡片, 以及 most videos 卡片的触摸效果
    private void setChannelCardTouch() {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    v.animate().scaleX(0.94f).setDuration(130).start();
                    v.animate().scaleY(0.94f).setDuration(130).start();

                } else if (action == MotionEvent.ACTION_UP) {
                    v.animate().cancel();
                    v.animate().scaleX(1f).setDuration(130).start();
                    v.animate().scaleY(1f).setDuration(130).start();
                    if (v == cv_mostView)
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(channelMostVideo.getVideoMostViewInfo().getVideoUrl())));
                    else if (v == cv_mostComment)
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(channelMostVideo.getVideoMostCommentInfo().getVideoUrl())));
                    else if (v == cv_mostLike)
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(channelMostVideo.getVideoMostLikeInfo().getVideoUrl())));
                    else if (v == cv_channel)
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(categoryCards.getChannelUrl())));
                } else {
                    v.animate().cancel();
                    v.animate().scaleX(1f).setDuration(130).start();
                    v.animate().scaleY(1f).setDuration(130).start();
                }

                return true;
            }
        };
        cv_channel.setOnTouchListener(onTouchListener);
        cv_mostView.setOnTouchListener(onTouchListener);
        cv_mostComment.setOnTouchListener(onTouchListener);
        cv_mostLike.setOnTouchListener(onTouchListener);

        btn_changePieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateData(channelRecentVideos);
            }
        });

    }

    //改变进出场动画效果
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