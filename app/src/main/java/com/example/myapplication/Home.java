package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.myapplication.Retrofit.IMyService;
import com.example.myapplication.Retrofit.RetrofitClientGson;
import com.example.myapplication.adpater.HideScrollListener;
import com.example.myapplication.adpater.HomeChannelCardAdapter;
import com.example.myapplication.adpater.HomeCategoryAdapter;
import com.example.myapplication.adpater.NavScrollListener;
import com.example.myapplication.model.ChannelCard;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.WaitTime.TIME_EXIT;

// HideScrollListener 检测用户滑动的监听器, 用于自动隐藏NavBar
public class Home extends AppCompatActivity implements HideScrollListener, HomeChannelCardAdapter.OnCardListener {

    private static final String TAG = "成功";

    private long                mBackPressed;
    //初始化layout中的View等
    BottomNavigationView        bottomNavBar;
    RelativeLayout              bottomNav;
    //recyclerView 用于主页Card(Channel)的显示, recyclerViewHorizontal用于主页顶部的Category Button的显示
    RefreshLayout refreshLayout;
    RecyclerView                recyclerView, recyclerViewHorizontal;
    HomeChannelCardAdapter homeChannelCardAdapter;
    boolean loadMoreState = false;
    // 初始化网络连接服务(呼叫后端用的 service)
    IMyService                  iMyService;
    Call<ArrayList<ChannelCard>> callInit, callLoad;
    String                      category[];
    //接收server回传的json,通过converter(Gson)直接转换为Object List, 绑定到RecyclerView的卡片上
    ArrayList<ChannelCard>      channelCards = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addOnScrollListener(new NavScrollListener(this));  //绑定滑动监听器
        recyclerViewHorizontal = findViewById(R.id.homeRecyclerViewHorizontal);
        bottomNav = findViewById(R.id.bottomNav);
        bottomNavBar = findViewById(R.id.bottomNavBar);
        bottomNavBar.setSelectedItemId(R.id.nav_home);        // 设置 NavBar 上的选中元素为 home (房子)

        //底部导航栏Navigation bar 的初始化
        navBarInit(bottomNavBar);
        //TODO: 绑定顶部的 Category 数据, 完善 RecyclerViewHorizontal
        horizontalRecyclerViewInit();

        //第一次初始化 SmartRefreshView, 此层套在RecyclerView的外层
        smartRefreshInit();
        //初始化CardAdapter, 避免cardInit()时,因网络问题导致adapter未绑定而无法刷新，重新联网也无法重新加载的情况
        cardAdapterInit();

        iMyService = RetrofitClientGson.getInstance().create(IMyService.class);        // 宣告 Retrofit 进行网络链接,并取得服务
        //启动App, 或者刷新时, 都会调用 cardInit(), 目的是去server抓ChannelCard的信息
        cardInit();
    }

    //初始化目前存儲的channelCards, 重新抓取, 並重設adapter
    private void cardInit() {
        callInit = iMyService.getAllChannelCards(2);
        callInit.enqueue(new Callback<ArrayList<ChannelCard>>() {
            //onResponse 只有成功接收到response才会执行, 同时,由于是多线程进行, call之后的语句不会等到Call执行完才执行
            @Override
            public void onResponse(Call<ArrayList<ChannelCard>> call, Response<ArrayList<ChannelCard>> response) {
                if (response.isSuccessful() && response.body() != null){
                    //用返还的ArrayList覆盖现有的 channelCards 中的data(相当于初始化)
                    channelCards = response.body();
                    cardAdapterInit();      //重置 Adapter, 刷新数据

                    loadMore(refreshLayout);
                }
            }
            @Override
            public void onFailure(Call<ArrayList<ChannelCard>> call, Throwable t) {
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
                cardInit();
            }
        });
    }

    private void cardLoadMore(int limit) {
        callLoad = iMyService.getAllChannelCards(limit);
        callLoad.enqueue(new Callback<ArrayList<ChannelCard>>() {
            @Override
            public void onResponse(Call<ArrayList<ChannelCard>> call, Response<ArrayList<ChannelCard>> response) {
                if (response.isSuccessful() && response.body() != null){
                    //添加更多Card
                    channelCards.addAll(response.body());
                    Log.d(TAG, "onResponse: 成功加载更多卡片,现有共" + channelCards.size() + "张");
                }
            }
            @Override
            public void onFailure(Call<ArrayList<ChannelCard>> call, Throwable t) {
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
                cardLoadMore(limit);
            }

        });
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

    @Override
    public void onChannelCardClick(int position) {
        //卡片点击事件: 获取对应卡片Data, 传到新开启的Channel Activity
        Log.d(TAG, "onCardClick: 成功点击卡片:" + position);
        ChannelCard channelCard = channelCards.get(position);

        Intent intent = new Intent(getApplicationContext(), Channel.class);
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

    //Smart Refresh 智能刷新, 下拉刷新, 上滑加载
    //https://github.com/scwang90/SmartRefreshLayout/blob/master/art/md_property.md
    private void smartRefreshInit() {
        Log.d(TAG, "smartRefreshInit: 成功初始化 SmartRefresh");
        refreshLayout = findViewById(R.id.refreshLayout);

        refreshLayout.setEnableAutoLoadMore(false); //取消自动加载更多, 需要手动拉
        refreshLayout.setRefreshHeader(new ClassicsHeader(getApplicationContext()));

        ClassicsFooter classicsFooter =new ClassicsFooter(getApplicationContext());
        refreshLayout.setRefreshFooter(classicsFooter);
        refreshLayout.setFooterHeight(115);
        refreshLayout.setFooterMaxDragRate(5);
        refreshLayout.setDragRate(0.65f);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                //下拉刷新
                loadMoreState = false;
                cardInit();
                refreshlayout.finishRefresh(1500);//传入false表示加载失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                //上滑加载更多, 需要添加Card到指定的ArrayList才能实现
                loadMore(refreshlayout);    //多段式加载
            }
        });
    }

    private void loadMore(@NonNull RefreshLayout refreshlayout) {
        loadMoreState = false;
        cardLoadMore(2);
        cardLoadMore(3);
        cardLoadMore(5);
        cardLoadMore(5);
        cardLoadMore(5);
        cardLoadMore(5);
        refreshlayout.finishLoadMore(2000);//传入false表示加载失败
    }

    public void cardAdapterInit() {
        Log.d(TAG, "initCardAdapter: 成功初始化Adapter, 当前拥有卡片:" + channelCards.size() + "张");
        //重设 Adapter
        homeChannelCardAdapter = new HomeChannelCardAdapter(getApplicationContext(), channelCards, this);
        recyclerView.setAdapter(homeChannelCardAdapter);
    }

    private void navBarInit(BottomNavigationView bottomNavBar) {
        // 跳转到新页面，并 reorder 到前面,重新排序 activity (参考下面 Link)
        // https://www.jianshu.com/p/537aa221eec4/
        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(getApplicationContext(), Search.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
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

    private void horizontalRecyclerViewInit() {
        //横向Category的文本内容
        category = getResources().getStringArray(R.array.recycle_row_home_category);
        ////初始化 HomeCategoryAdapter 并赋予data
        HomeCategoryAdapter homeCategoryAdapter = new HomeCategoryAdapter(getApplicationContext(), category);
        //绑定Adapter 和 RecyclerView
        recyclerViewHorizontal.setAdapter(homeCategoryAdapter);
        //用LinearLayoutManager将RecyclerView显示方式转为Horizontal
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHorizontal.setLayoutManager(linearLayoutManager);
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

    @Override
    protected void onResume() {
        super.onResume();

//        overridePendingTransition(0, 0);    //取消界面切换动画
        // 当从其他 activity 返回到该 activity 时, 恢复(解除暂停), 重设 NavBar的选中元素
        bottomNavBar.setSelectedItemId(R.id.nav_home);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadMoreState = true;
        if (callLoad != null) callLoad.cancel();
        if (callInit != null) callInit.cancel();
    }

    //滑动自动隐藏NavBar
    @Override
    public void onHide() {
        //隐藏动画
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomNav.getLayoutParams();
        bottomNav.animate().translationY(bottomNav.getHeight() + layoutParams.bottomMargin).setInterpolator(new AccelerateInterpolator(3));
    }
    @Override
    public void onShow() {
        //显示动画
        bottomNav.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));
    }
}