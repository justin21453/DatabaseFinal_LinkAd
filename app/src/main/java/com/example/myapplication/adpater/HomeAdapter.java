package com.example.myapplication.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

// Home数据呈现的Adapter (结合RecyclerView使用)
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    //初始化context用于接收对应activity
    Context     context;
    //初始化自定义的参数
    String      data1[], data2[];


    // constructor: 初始化Adapter, Context接activity, 其余则是自定义的para
    public HomeAdapter(Context ct, String s1[], String s2[]) {
        context = ct;
        data1 = s1;
        data2 = s2;
    }

    // 绑定View和Adapter的function
    public class HomeViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView cardText1, cardText2;
        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            cardText1 = itemView.findViewById(R.id.cardText1);
            cardText2 = itemView.findViewById(R.id.cardText2);
        }
    }

    //初始化ViewHolder-用于渲染RecyclerView里面的具体Row
    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycle_row_home, parent, false);
        return new HomeViewHolder(view);
    }

    //数据绑定-绑定每一Row的数据
    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        //卡片出现动画
        //holder.cardView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
        //数据绑定
        holder.cardText1.setText(data1[position]);
        holder.cardText2.setText(data2[position]);
    }

    @Override
    public int getItemCount() {
        //设置Item的数量
        return data1.length;
    }



}
