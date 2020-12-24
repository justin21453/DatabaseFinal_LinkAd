package com.example.myapplication.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

// Home页面顶部的Category的Adapter (横向Horizontal的RecyclerView)
public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.HomeViewHolder> {

    //初始化context用于接收对应activity
    Context     context;
    //初始化自定义的参数
    String      data1[];

    // constructor: 初始化Adapter, Context接activity, 其余则是自定义的para
    public HomeCategoryAdapter(Context ct, String category[]) {
        context = ct;
        data1 = category;
    }

    // 绑定View和Adapter的function
    public class HomeViewHolder extends RecyclerView.ViewHolder {
        Button btn;
        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id.category_btn);
        }
    }

    //初始化ViewHolder-用于渲染RecyclerView里面的具体Row
    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycle_row_home_category, parent, false);
        return new HomeViewHolder(view);
    }

    //数据绑定-绑定每一Row的数据
    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        //数据绑定
        holder.btn.setText(data1[position]);
    }

    @Override
    public int getItemCount() {
        //设置Item的数量
        return data1.length;
    }



}
