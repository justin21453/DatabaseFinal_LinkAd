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
import com.example.myapplication.model.ChannelCard;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

// Home页面ChannelCard呈现的Adapter (结合RecyclerView使用)
public class ChannelCardAdapter extends RecyclerView.Adapter<ChannelCardAdapter.ChannelCardViewHolder> {

    //初始化context用于接收对应activity
    Context     context;
    //初始化自定义的参数
    ArrayList<ChannelCard> channelCards;
    private OnCardListener onCardListener;

    // constructor: 初始化参数, Context接activity, 其余则是自定义的参数parameters
    public ChannelCardAdapter(Context context, ArrayList<ChannelCard> channelCards, OnCardListener onCardListener) {
        this.context = context;
        this.channelCards = channelCards;
        this.onCardListener = onCardListener;
    }

    // 绑定View和Adapter的function
    public class ChannelCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener{
        CardView cv_channel;
        ImageView img_card, img_avatar;
        TextView tv_channelName, tv_category, tv_subscribeValue, tv_viewValue;
        TextView tags_1, tags_2, tags_3;

        OnCardListener onCardListener;

        public ChannelCardViewHolder(@NonNull View itemView, OnCardListener onCardListener) {
            super(itemView);
            this.onCardListener = onCardListener;
            itemView.setOnClickListener(this);
            itemView.setOnTouchListener(this);
            //绑定Card里面的View
            cv_channel = itemView.findViewById(R.id.cardChannel);
            img_card = itemView.findViewById(R.id.imgChannel);
            img_avatar = itemView.findViewById(R.id.imgChannelAvatar);
            tv_channelName = itemView.findViewById(R.id.channelName);
            tv_category = itemView.findViewById(R.id.channelCategory);
            tv_subscribeValue = itemView.findViewById(R.id.categoriesViewValue);
            tv_viewValue = itemView.findViewById(R.id.channelViewValue);
            tags_1 = itemView.findViewById(R.id.tag_1);
            tags_2 = itemView.findViewById(R.id.tag_2);
            tags_3 = itemView.findViewById(R.id.tag_3);
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
    public ChannelCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.from(parent.getContext()).inflate(R.layout.recycle_row_home, parent, false);
        return new ChannelCardViewHolder(view, onCardListener);
    }

    //数据绑定-绑定每一Row的数据
    @Override
    public void onBindViewHolder(@NonNull ChannelCardViewHolder holder, int position) {
        //卡片出现动画
        holder.cv_channel.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
        //数据绑定
        holder.tv_channelName.setText(channelCards.get(position).getChannelTitle());
        holder.tv_channelName.setSingleLine(true);
        String category = channelCards.get(position).getChannelCategory();
        if(category.equals("NULL"))category = "None";   //如果Category是NULL的話
        holder.tv_category.setText(category);
        String subscribe = convertBigInteger(channelCards.get(position).getSubscriber());
        if (subscribe.equals("-1")) subscribe = "未公開";
        holder.tv_subscribeValue.setText(subscribe);
        holder.tv_viewValue.setText(convertBigInteger(channelCards.get(position).getAllViewCount()));

        List<String> tags = channelCards.get(position).getTag();
        //tags 可能为空
        if(tags != null)
            tagBinding(holder, tags);
        else {
            holder.tags_2.setVisibility(View.INVISIBLE);
            holder.tags_3.setVisibility(View.INVISIBLE);
        }

        String medium_photo_url = channelCards.get(position).getThumbnailsUrlHigh().replaceFirst("hq","mq");

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
                .placeholder(R.drawable.video_image)
                .into(holder.img_card);
        Glide.with(context).load(channelCards.get(position).getAvatarUrlHigh())
                .apply(requestOptions)
                .transition(withCrossFade(factory))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.avatar_image)
                .into(holder.img_avatar);
    }

    @Override
    public int getItemCount() {
        //设置Item的数量, 这里指总共有几张卡片
        return channelCards.size();
    }

    public interface OnCardListener {
        void onCardClick(int position);
        boolean onCardTouch(View v, MotionEvent event);
    }

    //绑定tag到3个tag_n上, 当tag不到3个或者tag的文字太长时, 进行一些处理
    public void tagBinding(@NonNull ChannelCardViewHolder holder, List<String> tag) {
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