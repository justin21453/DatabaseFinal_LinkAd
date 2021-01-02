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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.myapplication.R;
import com.example.myapplication.model.VideoCard;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

// Home页面ChannelCard呈现的Adapter (结合RecyclerView使用)
public class VideoCardAdapter extends RecyclerView.Adapter<VideoCardAdapter.VideoCardViewHolder> {

    //初始化context用于接收对应activity
    Context     context;
    //初始化自定义的参数
    ArrayList<VideoCard> videoCards;
    private OnCardListener onCardListener;

    // constructor: 初始化参数, Context接activity, 其余则是自定义的参数parameters
    public VideoCardAdapter(Context context, ArrayList<VideoCard> videoCards, OnCardListener onCardListener) {
        this.context = context;
        this.videoCards = videoCards;
        this.onCardListener = onCardListener;
    }

    // 绑定View和Adapter的function
    public class VideoCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener{
        CardView cv_video;
        ImageView img_card;
        TextView tags_1, tags_2, tags_3,
                tv_videoTitle, tv_viewValue, tv_category, tv_commentRate, tv_likeRate, tv_dislikeRate;

        OnCardListener onCardListener;

        public VideoCardViewHolder(@NonNull View itemView, OnCardListener onCardListener) {
            super(itemView);
            this.onCardListener = onCardListener;
            itemView.setOnClickListener(this);
            itemView.setOnTouchListener(this);
            //绑定Card里面的View
            cv_video        = itemView.findViewById(R.id.videoCard);
            img_card        = itemView.findViewById(R.id.imgVideo);
            tv_videoTitle   = itemView.findViewById(R.id.videoTitleValue);
            tv_category     = itemView.findViewById(R.id.tv_category);
            tv_viewValue    = itemView.findViewById(R.id.tv_viewValue);
            tv_commentRate  = itemView.findViewById(R.id.tv_commentRate);
            tv_likeRate     = itemView.findViewById(R.id.tv_likeRate);
            tv_dislikeRate  = itemView.findViewById(R.id.tv_dislikeRate);
            tags_1          = itemView.findViewById(R.id.tag_1);
            tags_2          = itemView.findViewById(R.id.tag_2);
            tags_3          = itemView.findViewById(R.id.tag_3);

        }

        @Override
        public void onClick(View v) {
            onCardListener.onVideoCardClick(getAdapterPosition());
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
    public VideoCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.from(parent.getContext()).inflate(R.layout.recycle_video, parent, false);
        return new VideoCardViewHolder(view, onCardListener);
    }

    //数据绑定-绑定每一Row的数据
    @Override
    public void onBindViewHolder(@NonNull VideoCardViewHolder holder, int position) {
        //卡片出现动画
        holder.cv_video.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
        //数据绑定, 将 VideoCard 中的Value, 对应设置到各个 textView 等
        VideoCard videoCard = videoCards.get(position);
        holder.tv_videoTitle.setText(videoCard.getTitle());
        holder.tv_viewValue.setText(videoCard.getViewCount() + "觀看");
        holder.tv_category.setText(videoCard.getCategoryId());

        convertToRateAndSet(videoCard, holder);

        List<String> tags = videoCards.get(position).getTag();
        //tags 可能为空
        if(tags != null)
            tagBinding(holder, tags);
        else {
            holder.tags_2.setVisibility(View.INVISIBLE);
            holder.tags_3.setVisibility(View.INVISIBLE);
        }

        String medium_photo_url = videoCards.get(position).getThumbnailsUrlHigh().replaceFirst("hq","mq");

        //图片加载设置
        DrawableCrossFadeFactory factory =
                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        RequestOptions requestOptions = new RequestOptions();
        //当图片没加载出来时候,显示占位符
        requestOptions.circleCropTransform();
        requestOptions.transform( new RoundedCorners(30));  //设置圆角Corner大小
        //load()可以放图片URL ("https://")
        Glide.with(context).load(medium_photo_url)
                .apply(requestOptions)
                .transition(withCrossFade(factory))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.shape_thumbnail_placeholder)
                .into(holder.img_card);
    }

    private void convertToRateAndSet(VideoCard videoCard, VideoCardViewHolder holder) {
        double viewCount = Double.parseDouble(videoCard.getViewCount());
        java.text.DecimalFormat myFormat = new java.text.DecimalFormat("0.000");
        String commentRate, likeRate, dislikeRate;
        if (viewCount != 0){
           commentRate = myFormat.format((double)Double.parseDouble(videoCard.getCommentCount()) / viewCount * 100);
           likeRate = myFormat.format((double)Double.parseDouble(videoCard.getLikeCount()) / viewCount * 100);
           dislikeRate = myFormat.format((double)Double.parseDouble(videoCard.getDislikeCount()) / viewCount * 100);
        } else {
            commentRate="0.000";
            likeRate="0.000";
            dislikeRate="0.000";
        }
        holder.tv_commentRate.setText(commentRate + "%評論");
        holder.tv_likeRate.setText(likeRate + "%喜歡");
        holder.tv_dislikeRate.setText(dislikeRate + "%不喜歡");
    }

    @Override
    public int getItemCount() {
        //设置Item的数量, 这里指总共有几张卡片
        return videoCards.size();
    }

    public interface OnCardListener {
        void onVideoCardClick(int position);
        boolean onCardTouch(View v, MotionEvent event);
    }

    //绑定tag到3个tag_n上, 当tag不到3个或者tag的文字太长时, 进行一些处理
    public void tagBinding(@NonNull VideoCardViewHolder holder, List<String> tag) {
        holder.tags_1.setText(tag.get(0));
        holder.tags_1.setSingleLine(true);
        holder.tags_2.setSingleLine(true);
        holder.tags_3.setSingleLine(true);

        holder.tags_1.setMaxEms(10);
        if(tag.size()>=3){
            holder.tags_2.setVisibility(View.VISIBLE);
            holder.tags_2.setText(tag.get(1));
            holder.tags_1.setMaxEms(6);
            holder.tags_2.setMaxEms(6);
            String str = "" + tag.get(0) + tag.get(1);
            int str_tag_2 = str.length();
            str += tag.get(2);
            int str_tag_3 = str.length();
            if (str_tag_3 > 12) {
                holder.tags_3.setVisibility(View.INVISIBLE);
            } else {
                holder.tags_3.setVisibility(View.VISIBLE);
                if (str_tag_2 > 12) {
                    holder.tags_1.setMaxEms(4);
                    holder.tags_2.setMaxEms(4);
                }
                holder.tags_3.setText(tag.get(2));
                holder.tags_3.setMaxEms(4);
            }
        } else if(tag.size()>=2){
            String str = "" + tag.get(0) + tag.get(1);
            holder.tags_1.setMaxEms(6);
            holder.tags_2.setMaxEms(6);
            if (str.length() > 12) {
                holder.tags_1.setMaxEms(5);
                holder.tags_2.setMaxEms(5);
            }
            holder.tags_2.setText(tag.get(1));
            holder.tags_2.setVisibility(View.VISIBLE);
            holder.tags_3.setVisibility(View.INVISIBLE);
        } else if(tag.size()==1){
            holder.tags_2.setVisibility(View.INVISIBLE);
            holder.tags_3.setVisibility(View.INVISIBLE);

        }
    }

    //將訂閱和觀看轉化為縮寫(一千=1K, 一百萬=1M, 十億=1B)
    public String convertBigInteger(String str) {
        if(str.length() > 9){
            str = str.substring(0, str.length()-9);
            str += "B";
        } else if(str.length() > 6){
            str = str.substring(0, str.length()-6);
            str += "M";
        } else if (str.length() > 3){
            str = str.substring(0, str.length()-3);
            str += "K";
        }
        return str;
    }
}