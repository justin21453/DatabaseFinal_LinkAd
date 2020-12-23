package com.example.myapplication.adpater;

import androidx.recyclerview.widget.RecyclerView;

// RecyclerView的滑动监听
public class NavScrollListener extends RecyclerView.OnScrollListener {
    private HideScrollListener listener;
    private static final int THRESHOLD = 20;    //触发Hide/Show的阈值
    private int distance = 0;                   //计算用户滑动距离
    private boolean visible = true;             //当前NavBar的可见状态, 初始为可见

    //constructor 初始化
    public NavScrollListener(HideScrollListener listener) {
        this.listener = listener;
    }

    //检测滑动
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //当上滑距离大于THRESHOLD(阈值=20px)时, 隐藏, 反之亦然
        if (distance > THRESHOLD && visible) {
            //隐藏动画
            visible = false;
            listener.onHide();
            distance = 0;
        } else if (distance < -20 && !visible) {
            //显示动画
            visible = true;
            listener.onShow();
            distance = 0;
        }
        //当Nav可见时候上滑 或者 Nav不可见时候下滑, 便开始计算用户滑动距离
        if (visible && dy > 0 || (!visible && dy < 0)) {
            distance += dy;
        }
    }
}
