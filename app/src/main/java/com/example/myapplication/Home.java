package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.myapplication.Retrofit.IMyService;
import com.example.myapplication.Retrofit.RetrofitClient;
import com.example.myapplication.adpater.HideScrollListener;
import com.example.myapplication.adpater.HomeCardAdapter;
import com.example.myapplication.adpater.HomeCategoryAdapter;
import com.example.myapplication.adpater.NavScrollListener;
import com.example.myapplication.model.ChannelCard;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.WaitTime.TIME_EXIT;
import static java.lang.Thread.sleep;

// HideScrollListener 检测用户滑动的监听器, 用于自动隐藏NavBar
public class Home extends AppCompatActivity implements HideScrollListener {

    private long mBackPressed;
    //初始化layout中的View等
    BottomNavigationView bottomNavBar;
    RelativeLayout bottomNav;
    //recyclerView 用于主页Card(Channel)的显示, recyclerViewHorizontal用于主页顶部的Category Button的显示
    RecyclerView recyclerView, recyclerViewHorizontal;
    // 初始化网络连接服务(呼叫后端用的 service)
    IMyService          iMyService;
    String category[];
    boolean check, checkUpdate;

    ArrayList<ChannelCard> channelCards = new ArrayList<>();
    //用于接收数据，与RecycleView, Adapter合作


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.homeRecyclerView);
        recyclerViewHorizontal = findViewById(R.id.homeRecyclerViewHorizontal);
        // bottomNav 是 NavBar外层的 RelativeLayout, 用于实现自动隐藏 NavBar效果
        bottomNav = findViewById(R.id.bottomNav);
        bottomNavBar = findViewById(R.id.bottomNavBar);
        // 设置 NavBar 上的选中元素为 home (房子)
        bottomNavBar.setSelectedItemId(R.id.nav_home);

        // 宣告 Retrofit 进行网络链接,并取得服务
        iMyService = RetrofitClient.getInstance_2().create(IMyService.class);

        //第一次初始化 SmartRefreshView, 此层套在RecyclerView的外层
        smartRefreshInit(channelCards);

        //初始化ChannelCard(刷新同样使用该 function)
        cardInit();

        //底部导航栏Navigation bar 的初始化
        navBarInit(bottomNavBar);
        //TODO: 绑定顶部的 Category 数据, 完善 RecyclerViewHorizontal
        horizontalRecyclerViewInit();

    }

    private void cardInit() {
        Call<ArrayList<ChannelCard>> call = iMyService.getAllChannelCards();
        call.enqueue(new Callback<ArrayList<ChannelCard>>() {
            @Override
            public void onResponse(Call<ArrayList<ChannelCard>> call, Response<ArrayList<ChannelCard>> response) {
                if (response.isSuccessful() && response.body() != null){
                    //因为刷新会清空原有的Card,所以需要重新初始化
                    //重新初始化 SmartRefreshView, 用新的数据重新绑定 Adapter
                    smartRefreshInit(response.body());
                }
            }
            @Override
            public void onFailure(Call<ArrayList<ChannelCard>> call, Throwable t) {
                Toast.makeText(Home.this,"请检查网络", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void cardLoadMore(ArrayList<ChannelCard> listChannelCard) {
        Call<ArrayList<ChannelCard>> call = iMyService.getAllChannelCards();
        call.enqueue(new Callback<ArrayList<ChannelCard>>() {
            @Override
            public void onResponse(Call<ArrayList<ChannelCard>> call, Response<ArrayList<ChannelCard>> response) {
                if (response.isSuccessful() && response.body() != null){
                    //添加更多Card
                    listChannelCard.addAll(response.body());
                    Log.d("成功添加更多卡片Card", String.valueOf(response.body().size()));
                }
            }
            @Override
            public void onFailure(Call<ArrayList<ChannelCard>> call, Throwable t) {
                Toast.makeText(Home.this,"请检查网络", Toast.LENGTH_SHORT).show();
            }

        });
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
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_favorite:
                        startActivity(new Intent(getApplicationContext(), Favorite.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_account:
                        startActivity(new Intent(getApplicationContext(), UserAccount.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                        overridePendingTransition(0, 0);
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
        HomeCategoryAdapter homeCategoryAdapter = new HomeCategoryAdapter(this, category);
        //绑定Adapter 和 RecyclerView
        recyclerViewHorizontal.setAdapter(homeCategoryAdapter);
        //用LinearLayoutManager将RecyclerView显示方式转为Horizontal
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHorizontal.setLayoutManager(linearLayoutManager);
    }

    private void smartRefreshInit(ArrayList<ChannelCard> channelCardsData) {
        //Smart Refresh 智能刷新, 下拉刷新, 上滑加载
        //https://github.com/scwang90/SmartRefreshLayout/blob/master/art/md_property.md
        //绑定RefreshLayout 和 它的 Header,Footer
        RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);

        refreshLayout.setEnableAutoLoadMore(false); //取消自动加载更多, 需要手动拉
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));

        ClassicsFooter classicsFooter =new ClassicsFooter(this);
        refreshLayout.setRefreshFooter(classicsFooter);
        refreshLayout.setFooterHeight(115);
        refreshLayout.setFooterMaxDragRate(5);
        refreshLayout.setDragRate(0.65f);
        Log.d("成功, 獲得的卡片數量是", String.valueOf(channelCardsData.size()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                //下拉刷新
                cardInit();
                refreshlayout.finishRefresh(650);//传入false表示加载失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                //上滑加载更多, 需要添加Card到指定的ArrayList才能实现
                cardLoadMore(channelCardsData);
                refreshlayout.finishLoadMore(2000);//传入false表示加载失败
            }
        });

        //初始化CardAdapter
        initCardAdapter(channelCardsData);

    }
    public void initCardAdapter(ArrayList<ChannelCard> channelCardsData) {
        //初始化 HomeAdapter 并赋予data
        HomeCardAdapter homeCardAdapter = new HomeCardAdapter(this, channelCardsData);
        //绑定Adapter 和 RecyclerView
        recyclerView.setAdapter(homeCardAdapter);
        //设置RecyclerView的每一row的样式
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //设置RecyclerView的滑动监听, 用于实现滑动隐藏NavBar
        recyclerView.addOnScrollListener(new NavScrollListener(this));
    }

    // 若再次返回会退出应用, 则User需要快速返回两次, 才能退出(目的是为了防误触)
    @Override
    public void onBackPressed(){
        if (isTaskRoot()) {
            if(mBackPressed+TIME_EXIT>System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                Toast.makeText(this,"再次返回以退出",Toast.LENGTH_SHORT).show();
                mBackPressed=System.currentTimeMillis();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(0, 0);    //取消界面切换动画
    }
    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);    //取消界面切换动画
        // 当从其他 activity 返回到该 activity 时, 恢复(解除暂停), 重设 NavBar的选中元素
        bottomNavBar.setSelectedItemId(R.id.nav_home);
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);    //取消界面切换动画
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