package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.myapplication.adpater.HideScrollListener;
import com.example.myapplication.adpater.HomeAdapter;
import com.example.myapplication.adpater.NavScrollListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import static com.example.myapplication.WaitTime.TIME_EXIT;
// HideScrollListenr 检测用户滑动的监听器, 用于自动隐藏NavBar
public class Home extends AppCompatActivity implements HideScrollListener {

    private long mBackPressed;
    //初始化layout中的View等
    BottomNavigationView bottomNavBar;
    RelativeLayout bottomNav;
    RecyclerView recyclerView;
    //用于接收数据，与RecycleView, Adapter合作
    String s1[], s2[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.mainScreenRecyclerView);
        bottomNavBar = findViewById(R.id.bottomNavBar);
        bottomNav = findViewById(R.id.bottomNav);
        // 设置 NavBar 上的选中元素为 home (房子)
        bottomNavBar.setSelectedItemId(R.id.nav_home);

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // 跳转到新页面，并 reorder 到前面,重新排序 activity (参考下面 Link)
                // https://www.jianshu.com/p/537aa221eec4/
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
        //TODO: 主页面 RecyclerView 的数据呈现
        //Smart Refresh 智能刷新, 下拉刷新, 上滑加载
        //https://github.com/scwang90/SmartRefreshLayout/blob/master/art/md_property.md

        //绑定RefreshLayout 和 它的 Header,Footer
        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));

        //TODO: 实现下拉刷新 Data
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //下拉刷新
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        //TODO: 实现上滑加载更多的 Data
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                //上滑加载更多
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });

        //测试用RecyclerView的Title和Description的Data
        s1 = getResources().getStringArray(R.array.recycle_row_main_screen_title);
        s2 = getResources().getStringArray(R.array.recycle_row_main_screen_description);

        //初始化Adapter并赋予data
        HomeAdapter homeAdapter = new HomeAdapter(this, s1, s2);
        //绑定Adapter和RecyclerView
        recyclerView.setAdapter(homeAdapter);
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
                return;
            } else {
                Toast.makeText(this,"再次返回以退出",Toast.LENGTH_SHORT).show();
                mBackPressed=System.currentTimeMillis();
            }
        } else {
            super.onBackPressed();
            return;
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