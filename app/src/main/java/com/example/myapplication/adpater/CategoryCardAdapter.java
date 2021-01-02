package com.example.myapplication.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.CategoryCard;
import com.example.myapplication.model.CategoryId;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

// Home页面ChannelCard呈现的Adapter (结合RecyclerView使用)
public class CategoryCardAdapter extends RecyclerView.Adapter<CategoryCardAdapter.CategoryCardViewHolder> {

    //初始化context用于接收对应activity
    Context     context;
    //初始化自定义的参数
    //存放频道下的所有分类(只要有影片是这个分类就算频道含有这个分类)
    CategoryCard categoryCards;
    List<CategoryId> categoryId;
    private OnCardListener onCardListener;

    // constructor: 初始化参数, Context接activity, 其余则是自定义的参数parameters
    public CategoryCardAdapter(Context context, CategoryCard categoryCards, OnCardListener onCardListener) {
        this.context = context;
        this.categoryCards = categoryCards;
        this.onCardListener = onCardListener;
        categoryId = categoryCards.getCategoryIds();
    }

    // 绑定View和Adapter的function
    public class CategoryCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener{
        CardView cv_category;
        TextView tv_category, tv_categoriesVideoValue, tv_categoriesViewValue, tv_categoriesJudge, tv_categoriesLikeRate;

        OnCardListener onCardListener;

        public CategoryCardViewHolder(@NonNull View itemView, OnCardListener onCardListener) {
            super(itemView);
            this.onCardListener = onCardListener;
            itemView.setOnClickListener(this);
            itemView.setOnTouchListener(this);
            //绑定Card里面的View
            cv_category                 = itemView.findViewById(R.id.categoriesCard);
            tv_category                 = itemView.findViewById(R.id.categoriesName);
            tv_categoriesVideoValue     = itemView.findViewById(R.id.categoriesVideoValue);
            tv_categoriesViewValue      = itemView.findViewById(R.id.categoriesViewValue);
            tv_categoriesJudge          = itemView.findViewById(R.id.categoriesJudge);
            tv_categoriesLikeRate       = itemView.findViewById(R.id.categoriesLikeRate);
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
        View view = inflater.from(parent.getContext()).inflate(R.layout.recycle_channel_category, parent, false);
        return new CategoryCardViewHolder(view, onCardListener);
    }

    //数据绑定-绑定每一Row的数据
    //TODO: 绑定 categories card 的数据
    @Override
    public void onBindViewHolder(@NonNull CategoryCardViewHolder holder, int position) {
        //卡片出现动画
        holder.cv_category.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
        //数据绑定
        CategoryId categoryId_tmp = this.categoryId.get(position);
        double allVideoCount = Double.parseDouble(this.categoryCards.getAllvideoCount());
        double allVideoView = Double.parseDouble(this.categoryCards.getAllviewCount());

        java.text.DecimalFormat myFormat = new java.text.DecimalFormat("0.00");
        String videoRate = myFormat.format(categoryId_tmp.getVideoCount() / allVideoCount * 100);
        String viewRate = myFormat.format(categoryId_tmp.getVideoViews() / allVideoView * 100);
        double likeRate = ((double)categoryId_tmp.getTotalLike() / (double)categoryId_tmp.getVideoViews()) * 100.0;
        holder.tv_categoriesLikeRate.setText(myFormat.format(likeRate) + "%觀眾讚了");
        if (likeRate > 1.5f) {
            holder.tv_categoriesLikeRate.setBackgroundResource(R.drawable.shape_4dp_corners_dark_green);
        } else if (likeRate > 0.7f) {
            holder.tv_categoriesLikeRate.setBackgroundResource(R.drawable.shape_4dp_corners_dark_orange);
        } else {
            holder.tv_categoriesLikeRate.setBackgroundResource(R.drawable.shape_4dp_corners_dark_red);
        }

        String category = categoryId_tmp.getId().equals("NULL") ? "無分類": categoryId_tmp.getId();
        holder.tv_category.setText(category);
        holder.tv_categoriesVideoValue.setText(videoRate + "%");
        holder.tv_categoriesViewValue.setText(viewRate + "%");
        double judge = categoryId_tmp.getAvgRating() == null? 0: categoryId_tmp.getAvgRating();

        holder.tv_categoriesJudge.setText(myFormat.format(judge) + "%好评");
        if (judge > 97.0f) {
            holder.tv_categoriesJudge.setBackgroundResource(R.drawable.shape_4dp_corners_dark_green);
        } else if (judge > 90.0f) {
            holder.tv_categoriesJudge.setBackgroundResource(R.drawable.shape_4dp_corners_dark_orange);
        } else {
            holder.tv_categoriesJudge.setBackgroundResource(R.drawable.shape_4dp_corners_dark_red);
        }

    }

    @Override
    public int getItemCount() {
        //设置Item的数量, 这里指总共有几张卡片
        return categoryId.size();
    }

    public interface OnCardListener {
        void onCardClick(int position);
        boolean onCardTouch(View v, MotionEvent event);
    }

}