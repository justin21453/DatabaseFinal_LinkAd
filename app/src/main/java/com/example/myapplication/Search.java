package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.myapplication.Retrofit.IMyService;
import com.example.myapplication.Retrofit.RetrofitClientGson;
import com.example.myapplication.adpater.HideScrollListener;
import com.example.myapplication.adpater.HomeChannelCardAdapter;
import com.example.myapplication.adpater.NavScrollListener;
import com.example.myapplication.adpater.VideoCardAdapter;
import com.example.myapplication.model.ChannelCard;
import com.example.myapplication.model.VideoCard;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.WaitTime.TIME_EXIT;

public class Search extends AppCompatActivity implements HideScrollListener, HomeChannelCardAdapter.OnCardListener, VideoCardAdapter.OnCardListener {

    private static final String TAG = "成功";
    private long mBackPressed;                  //连续返回2次——退出
    boolean toolbarState = false;               //判断 Toolbar 的显示状态
    boolean loadMoreState = false;              //判断 加载状态 （避免联网自动加载, 不停跳toast提示
    boolean searchVideoState = false;           //判断 是否在搜寻影片, 会取消之前的搜索频道操作
    boolean searchChannelState = false;         //判断 是否在搜寻频道, 会取消之前的搜索影片操作

    BottomNavigationView bottomNavBar;
    RelativeLayout bottomNav, toolbarRelativeLayout;

    //recyclerView 用于主页Card(Channel)的显示, recyclerViewHorizontal用于主页顶部的Category Button的显示
    RecyclerView recyclerView;
    HomeChannelCardAdapter homeChannelCardAdapter;
    VideoCardAdapter videoCardAdapter;

    // 初始化网络连接服务(呼叫后端用的 service)
    IMyService iMyService;
    Call<ArrayList<ChannelCard>> callChannelInit, callChannelLoad;
    Call<ArrayList<VideoCard>> callVideoCardInit, callVideoCardLoad;
    //接收server回传的json,通过converter(Gson)直接转换为Object List, 绑定到RecyclerView的卡片上
    ArrayList<ChannelCard>      channelCards = new ArrayList<>();
    ArrayList<VideoCard>      videoCards = new ArrayList<>();

    ConstraintLayout btnVideoConstraintLayout, btnChannelConstraintLayout;
    Button btnSearchVideo, btnSearchChannel;
    EditText searchText;
    String text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        iMyService = RetrofitClientGson.getInstance().create(IMyService.class);

        recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addOnScrollListener(new NavScrollListener(this));  //绑定滑动监听器

        //顶栏Toolbar和底栏Navbar
        toolbarRelativeLayout = findViewById(R.id.toolbarRelativeLayout);
        bottomNav = findViewById(R.id.searchBottomNav);
        //bottomNavBar内容
        bottomNavBar = findViewById(R.id.searchBottomNavBar);
        bottomNavBar.setSelectedItemId(R.id.nav_search);         // 设置 NavBar 上的选中元素为 search (放大镜)

        btnSearchVideo = (Button)findViewById(R.id.btnSearchVideo);
        btnSearchChannel = (Button)findViewById(R.id.btnSearchChannel);
        btnVideoConstraintLayout = findViewById(R.id.btnVideoConstraintLayout);
        btnChannelConstraintLayout = findViewById(R.id.btnChannelConstraintLayout);
        searchText = findViewById(R.id.searchText);

        //第一次初始化 SmartRefreshView, 此层套在RecyclerView的外层
        smartRefreshInit();

        searchInit();

        navBarInit(bottomNavBar);

    }


    //初始化搜索相关控件
    @SuppressLint("ClickableViewAccessibility")
    private void searchInit() {
        btnSearchVideo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    btnVideoConstraintLayout.animate().translationY(10f).setDuration(0).start();
                    btnVideoConstraintLayout.setBackground(getDrawable(R.color.transparent));
                } else {
                    v.animate().cancel();
                    btnVideoConstraintLayout.animate().translationY(0).setDuration(0).start();
                    btnVideoConstraintLayout.setBackground(getDrawable(R.drawable.btn_bg_14dp_orange));

                    loadMoreState = false;
                    searchVideoState = true;
                    searchChannelState = false;
                    if (callChannelInit != null) callChannelInit.cancel();
                    btnChannelConstraintLayout.setBackground(getDrawable(R.drawable.btn_bg_14dp_green));

                    Log.d(TAG, "onClick: 点击了影片按钮");
                    text = searchText.getText().toString();
                    if (TextUtils.isEmpty(text)) {
                        Toast.makeText(getApplicationContext(), "搜索栏不能为空", Toast.LENGTH_SHORT).show();
                    } else {

                        videoCardInit(7);
                    }
                }

                return true;
            }
        });
        btnSearchChannel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    btnChannelConstraintLayout.animate().translationY(10f).setDuration(0).start();
                    btnChannelConstraintLayout.setBackground(getDrawable(R.color.transparent));
                } else {
                    v.animate().cancel();
                    btnChannelConstraintLayout.animate().translationY(0).setDuration(0).start();
                    btnChannelConstraintLayout.setBackground(getDrawable(R.drawable.btn_bg_14dp_orange));

                    loadMoreState = false;
                    searchChannelState = true;
                    searchVideoState = false;
                    if (callVideoCardInit != null) callVideoCardInit.cancel();
                    btnVideoConstraintLayout.setBackground(getDrawable(R.drawable.btn_bg_14dp_green));

                    Log.d(TAG, "onClick: 点击了频道按钮");
                    text = searchText.getText().toString();
                    if (TextUtils.isEmpty(text)) {
                        Toast.makeText(getApplicationContext(), "搜索栏不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        channelCardInit(6);
                    }
                }

                return true;
            }
        });

    }

    //初始化目前存儲的channelCards, 重新抓取, 並重設adapter
    private void channelCardInit(int limit) {
        callChannelInit = iMyService.searchChannel(text, 0, limit);
        callChannelInit.enqueue(new Callback<ArrayList<ChannelCard>>() {
            @Override
            public void onResponse(Call<ArrayList<ChannelCard>> call, Response<ArrayList<ChannelCard>> response) {
                if (response.isSuccessful() && response.body() != null){

                    if (searchChannelState) {
                        videoCards.clear();                 //清空另一种的 ArrayList
                        channelCards = response.body();
                        channelCardAdapterInit();      //重置 Adapter, 刷新数据
                        channelCardLoadMore();          //加載更多Card
                        toggleToolbar();
                    }
                    btnChannelConstraintLayout.setBackground(getDrawable(R.drawable.btn_bg_14dp_green));
                    btnVideoConstraintLayout.setBackground(getDrawable(R.drawable.btn_bg_14dp_green));
                }
            }
            @Override
            public void onFailure(Call<ArrayList<ChannelCard>> call, Throwable t) {
                if (!loadMoreState && searchChannelState) {
                    Toast.makeText(getApplicationContext(),"加載失敗,可能是網絡問題", Toast.LENGTH_SHORT).show();
                    loadMoreState = true;
                }
                if (searchChannelState) {
                    btnChannelConstraintLayout.setBackground(getDrawable(R.drawable.btn_bg_14dp_red));
                    btnVideoConstraintLayout.setBackground(getDrawable(R.drawable.btn_bg_14dp_green));
                    channelCardInit(limit);
                }
            }
        });
    }
    //初始化目前存儲的videoCards, 重新抓取, 並重設adapter
    private void videoCardInit(int limit) {
        callVideoCardInit = iMyService.searchVideo(text, 0, limit);
        callVideoCardInit.enqueue(new Callback<ArrayList<VideoCard>>() {
            @Override
            public void onResponse(Call<ArrayList<VideoCard>> call, Response<ArrayList<VideoCard>> response) {
                if (response.isSuccessful() && response.body() != null){

                    if (searchVideoState) {
                        videoCards = response.body();
                        channelCards.clear();            //清空另一种的 ArrayList
                        videoCardAdapterInit();      //重置 Adapter, 刷新数据
                        videoCardLoadMore();          //加載更多Card
                        toggleToolbar();
                    }
                    btnVideoConstraintLayout.setBackground(getDrawable(R.drawable.btn_bg_14dp_green));
                    btnChannelConstraintLayout.setBackground(getDrawable(R.drawable.btn_bg_14dp_green));

                }
            }
            @Override
            public void onFailure(Call<ArrayList<VideoCard>> call, Throwable t) {

                if (!loadMoreState && searchVideoState) {
                    Toast.makeText(getApplicationContext(),"加載失敗,可能是網絡問題", Toast.LENGTH_SHORT).show();
                    loadMoreState = true;
                }
                if (searchVideoState) {
                    btnVideoConstraintLayout.setBackground(getDrawable(R.drawable.btn_bg_14dp_red));
                    btnChannelConstraintLayout.setBackground(getDrawable(R.drawable.btn_bg_14dp_green));
                    videoCardInit(limit);
                }
            }
        });
    }



    //多段式加载 Channel 卡片
    private void channelCardLoadMore() {
        //多线程
        channelCardLoad(channelCards.size(), 5);
        channelCardLoad(channelCards.size() + 5, 5);
        channelCardLoad(channelCards.size() + 10, 10);
        channelCardLoad(channelCards.size() + 20, 10);

    }
    private void channelCardLoad(int size, int limit) {
        callChannelLoad = iMyService.searchChannel(text, size, limit);
        callChannelLoad.enqueue(new Callback<ArrayList<ChannelCard>>() {
            @Override
            public void onResponse(Call<ArrayList<ChannelCard>> call, Response<ArrayList<ChannelCard>> response) {
                if (response.isSuccessful() && response.body() != null){
                    //添加更多Card
                    channelCards.addAll(response.body());
                    homeChannelCardAdapter.notifyItemRangeInserted(size, limit);

                    Log.d(TAG, "onResponse: 成功加载更多channel卡片,现有共" + channelCards.size() + "张");
                } else if(response.body() == null) {
                    //已经无更多数据
                }
            }
            @Override
            public void onFailure(Call<ArrayList<ChannelCard>> call, Throwable t) {
                if (!loadMoreState && searchChannelState == true) {
                    Toast.makeText(getApplicationContext(),"加載失敗,可能是網絡問題", Toast.LENGTH_SHORT).show();
                    loadMoreState = true;
                }
                channelCardLoad(channelCards.size(), limit);
            }

        });
    }

    //多段式加载 Video 卡片
    private void videoCardLoadMore() {
        //多线程
        videoCardLoad(videoCards.size(), 5);
        videoCardLoad(videoCards.size() + 5, 5);
        videoCardLoad(videoCards.size() + 10, 10);
        videoCardLoad(videoCards.size() + 20, 10);

    }
    private void videoCardLoad(int size ,int limit) {
        callVideoCardLoad = iMyService.searchVideo(text, size, limit);
        callVideoCardLoad.enqueue(new Callback<ArrayList<VideoCard>>() {
            @Override
            public void onResponse(Call<ArrayList<VideoCard>> call, Response<ArrayList<VideoCard>> response) {
                if (response.isSuccessful() && response.body() != null){
                    //用返还的ArrayList覆盖现有的 channelCards 中的data(相当于初始化)
                    videoCards.addAll(response.body());
                    videoCardAdapter.notifyItemRangeInserted(size, limit);

                    Log.d(TAG, "onResponse: 成功加载更多video卡片,现有共" + videoCards.size() + "张");
                }
            }
            @Override
            public void onFailure(Call<ArrayList<VideoCard>> call, Throwable t) {
                if (!loadMoreState) {
                    Toast.makeText(getApplicationContext(),"加載失敗,可能是網絡問題", Toast.LENGTH_SHORT).show();
                    loadMoreState = true;
                }
                videoCardLoad(videoCards.size(), limit);
            }
        });
    }





    //Video 和 Channel 两种 Card 的卡片触碰事件
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
    //Channel 卡片点击事件
    @Override
    public void onChannelCardClick(int position) {
        //卡片点击事件: 获取对应卡片Data, 传到新开启的Channel Activity
        Log.d(TAG, "onCardClick: 成功点击Channel卡片:" + position);
        ChannelCard channelCard = channelCards.get(position);

        Intent intent = new Intent(getApplicationContext(), Channel.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("channelId", channelCard.getChannelId());
        intent.putExtra("channelName", channelCard.getChannelTitle());
        intent.putExtra("category", channelCard.getChannelCategory());
        intent.putExtra("subscribeValue", channelCard.getSubscriber());
        intent.putExtra("viewValue", channelCard.getAllViewCount());
        intent.putExtra("thumbnail", channelCard.getThumbnailsUrlHigh());
        intent.putExtra("avatar", channelCard.getAvatarUrlHigh());

        //开启新的Activity
        startActivity(intent);
    }
    //Video 卡片点击事件
    @Override
    public void onVideoCardClick(int position) {
        //卡片点击事件: 获取对应卡片Data, 传到新开启的 Video Activity
        Log.d(TAG, "onCardClick: 成功点击Video卡片:" + position);
        VideoCard videoCard = videoCards.get(position);

        Intent intent = new Intent(getApplicationContext(), Video.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("channelId", videoCard.getChannelId());
        intent.putExtra("channelName", videoCard.getChannelTitle());
        intent.putExtra("thumbnail", videoCard.getThumbnailsUrlHigh());
        intent.putExtra("videoTitle", videoCard.getTitle());
        intent.putExtra("updateTime", videoCard.getDatatime());
        intent.putExtra("publishedTime", videoCard.getPublishedAt());
        intent.putExtra("viewValue", videoCard.getViewCount());
        intent.putExtra("category", videoCard.getCategoryId());
        intent.putExtra("likeValue", videoCard.getLikeCount());
        intent.putExtra("dislikeValue", videoCard.getDislikeCount());
        intent.putExtra("commentValue", videoCard.getCommentCount());
        intent.putExtra("videoUrl", videoCard.getVideoUrl());

        //开启新的Activity
        startActivity(intent);
    }



    //Smart Refresh 智能刷新, 下拉刷新, 上滑加载
    //https://github.com/scwang90/SmartRefreshLayout/blob/master/art/md_property.md
    private void smartRefreshInit() {
        Log.d(TAG, "smartRefreshInit: 成功初始化 SmartRefresh");
        RefreshLayout refreshLayout = findViewById(R.id.searchRefreshLayout);

        refreshLayout.setEnableAutoLoadMore(false);                     //取消自动加载更多, 需要手动拉
        //refreshLayout.setRefreshHeader(new ClassicsHeader(this));     //搜索页面不需要下拉刷新
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);       //不满一页, 不允许上滑加载更多
        refreshLayout.setEnableRefresh(false);
        ClassicsFooter classicsFooter =new ClassicsFooter(getApplicationContext());
        refreshLayout.setRefreshFooter(classicsFooter);
        refreshLayout.setFooterHeight(115);
        refreshLayout.setFooterMaxDragRate(5);
        refreshLayout.setDragRate(0.65f);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                //搜索页面不需要下拉刷新
                //channelCardInit();
                //refreshlayout.finishRefresh(2000);//传入false表示加载失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                //上滑加载更多, 需要添加Card到指定的ArrayList才能实现
                Log.d(TAG, "onLoadMore: 现在channel和video卡片数量分别是" + channelCards.size() + ", " + videoCards.size());
                loadMoreState = false;
                if (channelCards.size() == 0) {
                    videoCardLoadMore();          //加載更多Card
                }
                else if (videoCards.size() == 0) {
                    channelCardLoadMore();
                }

                refreshlayout.finishLoadMore(2000);//传入false表示加载失败
            }
        });
    }

    //重设 Adapter 为 Channel Card
    public void channelCardAdapterInit() {
        Log.d(TAG, "initCardAdapter: 成功初始化Adapter, 当前拥有频道卡片:" + channelCards.size() + "张");
        homeChannelCardAdapter = new HomeChannelCardAdapter(getApplicationContext(), channelCards, this);
        recyclerView.setAdapter(homeChannelCardAdapter);
    }
    //重设 Adapter 为 Video Card
    public void videoCardAdapterInit() {
        Log.d(TAG, "videoCardAdapterInit: 成功初始化Adapter, 当前拥有影片卡片:" + videoCards.size() + "张");
        videoCardAdapter = new VideoCardAdapter(getApplicationContext(), videoCards, this);
        recyclerView.setAdapter(videoCardAdapter);
    }



    private void navBarInit(BottomNavigationView bottomNavBar) {
        // 跳转到新页面，并 reorder 到前面,重新排序 activity (参考下面 Link)
        // https://www.jianshu.com/p/537aa221eec4/
        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_search:
                        toggleToolbar();                //隐藏or显示 Toolbar
                        return true;
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), Home.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                        return true;
                    case R.id.nav_favorite:
                        startActivity(new Intent(getApplicationContext(), Favorite.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                        return true;
                    case R.id.nav_account:
                        startActivity(new Intent(getApplicationContext(), UserAccount.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                        return true;
                }
                return false;
            }
        });
    }




    // 若再次返回会退出应用, 则User需要快速返回两次, 才能退出(目的是为了防误触)
    @Override
    public void onBackPressed(){
        if (isTaskRoot()) {
            if(mBackPressed+TIME_EXIT>System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                Toast.makeText(getApplicationContext(),"再次返回以退出",Toast.LENGTH_SHORT).show();
                mBackPressed=System.currentTimeMillis();
            }
        } else {
            super.onBackPressed();
        }
    }

    //修复内存泄露
    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadMoreState = true;
        if (callChannelInit != null) callChannelInit.cancel();
        if (callChannelLoad != null) callChannelLoad.cancel();
        if (callVideoCardInit != null) callVideoCardInit.cancel();
        if (callVideoCardLoad != null) callVideoCardLoad.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 当从其他 activity 返回到该 activity 时, 恢复(解除暂停), 重设 NavBar的选中元素
        bottomNavBar.setSelectedItemId(R.id.nav_search);
    }
    //滑动自动隐藏NavBar
    @Override
    public void onHide() {
        //隐藏动画
        if (toolbarState) toggleToolbar();

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomNav.getLayoutParams();
        bottomNav.animate().translationY(bottomNav.getHeight() + layoutParams.bottomMargin).setInterpolator(new AccelerateInterpolator(2));

    }

    private void toggleToolbar() {
        if (toolbarState) {
            RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) toolbarRelativeLayout.getLayoutParams();
            toolbarRelativeLayout.animate().translationY(layoutParams2.bottomMargin - toolbarRelativeLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2));
            toolbarState = false;
        } else {
            toolbarRelativeLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));
            toolbarState = true;
        }

    }

    @Override
    public void onShow() {
        //显示动画
        bottomNav.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));
        toolbarRelativeLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));
        toolbarState = true;
    }
}