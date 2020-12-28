package com.example.myapplication.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.CategoryCard;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

// Home页面ChannelCard呈现的Adapter (结合RecyclerView使用)
public class CategoryCardAdapter extends RecyclerView.Adapter<CategoryCardAdapter.CategoryCardViewHolder> {

    //初始化context用于接收对应activity
    Context     context;
    //初始化自定义的参数
    //存放频道下的所有分类(只要有影片是这个分类就算频道含有这个分类)
    ArrayList<CategoryCard> categoryCards;
    private OnCardListener onCardListener;

    // constructor: 初始化参数, Context接activity, 其余则是自定义的参数parameters
    public CategoryCardAdapter(Context context, ArrayList<CategoryCard> categoryCards, OnCardListener onCardListener) {
        this.context = context;
        this.categoryCards = categoryCards;
        this.onCardListener = onCardListener;
    }

    // 绑定View和Adapter的function
    public class CategoryCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener{
        CardView cv_category;
        ImageView img_category;
        TextView tv_category, tv_categoriesVideoValue, tv_categoriesViewValue, tv_categoriesJudgeValue;

        OnCardListener onCardListener;

        public CategoryCardViewHolder(@NonNull View itemView, OnCardListener onCardListener) {
            super(itemView);
            this.onCardListener = onCardListener;
            itemView.setOnClickListener(this);
            itemView.setOnTouchListener(this);
            //绑定Card里面的View
            cv_category                 = itemView.findViewById(R.id.categoriesCard);
            img_category                = itemView.findViewById(R.id.categoriesImage);
            tv_category                 = itemView.findViewById(R.id.categoriesName);
            tv_categoriesVideoValue     = itemView.findViewById(R.id.categoriesVideoValue);
            tv_categoriesViewValue      = itemView.findViewById(R.id.categoriesViewValue);
            tv_categoriesJudgeValue     = itemView.findViewById(R.id.categoriesJudgeValue);
        }

        @Override
        public void onClick(View v) {
            onCardListener.onCardClick(getAdapterPosition());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            onCardListener.onCardTouch(v, event);
            return false;
        }
    }

    //初始化ViewHolder-用于渲染RecyclerView里面的具体Row
    @NonNull
    @Override
    public CategoryCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.from(parent.getContext()).inflate(R.layout.recycle_row_channel_category, parent, false);
        return new CategoryCardViewHolder(view, onCardListener);
    }

    //数据绑定-绑定每一Row的数据
    //TODO: 绑定 categories card 的数据
    @Override
    public void onBindViewHolder(@NonNull CategoryCardViewHolder holder, int position) {
        //卡片出现动画
        holder.cv_category.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
        //数据绑定

    }

    @Override
    public int getItemCount() {
        //设置Item的数量, 这里指总共有几张卡片
        return categoryCards.size();
    }

    public interface OnCardListener {
        void onCardClick(int position);
        boolean onCardTouch(View v, MotionEvent event);
    }

}