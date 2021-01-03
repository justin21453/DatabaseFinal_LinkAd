package com.example.myapplication.adpater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

// Home页面顶部的Category的Adapter (横向Horizontal的RecyclerView)
public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.HomeCategoryHolder> {

    //初始化context用于接收对应activity
    Context     context;
    //初始化自定义的参数
    String categories[];
    private OnCardListener onCardListener;


    // constructor: 初始化Adapter, Context接activity, 其余则是自定义的para
    public HomeCategoryAdapter(Context context, String categories[], OnCardListener onCardListener) {
        this.context = context;
        this.categories = categories;
        this.onCardListener = onCardListener;
    }

    // 绑定View和Adapter的function
    public class HomeCategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener{
        TextView btn;
        ConstraintLayout btnConstraintLayout;

        OnCardListener onCardListener;

        public HomeCategoryHolder(@NonNull View itemView, OnCardListener onCardListener) {
            super(itemView);
            this.onCardListener = onCardListener;
            itemView.setOnClickListener(this);
            itemView.setOnTouchListener(this);
            btn = itemView.findViewById(R.id.category_btn);
            btnConstraintLayout = itemView.findViewById(R.id.btnConstraintLayout);
        }
        @Override
        public void onClick(View v) {
            onCardListener.onCategoryCardClick(getAdapterPosition());

        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            onCardListener.onCategoryCardTouch(btnConstraintLayout, event);

            return false;
        }
    }

    //初始化ViewHolder-用于渲染RecyclerView里面的具体Row
    @NonNull
    @Override
    public HomeCategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycle_row_home_category, parent, false);
        return new HomeCategoryHolder(view, onCardListener);
    }

    //数据绑定-绑定每一Row的数据
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull HomeCategoryHolder holder, int position) {
        //数据绑定
        holder.btn.setText(categories[position]);

    }

    @Override
    public int getItemCount() {
        //设置Item的数量
        return categories.length;
    }

    public interface OnCardListener {
        void onCategoryCardClick(int position);
        boolean onCategoryCardTouch(View v, MotionEvent event);
    }



}
