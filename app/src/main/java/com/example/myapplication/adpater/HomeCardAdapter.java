package com.example.myapplication.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

// Home数据呈现的Adapter (结合RecyclerView使用)
public class HomeCardAdapter extends RecyclerView.Adapter<HomeCardAdapter.HomeCardViewHolder> {

    //初始化context用于接收对应activity
    Context     context;
    //初始化自定义的参数
    String channel_name[], category[], subscribe[], view[];

    // constructor: 初始化参数, Context接activity, 其余则是自定义的参数parameters
    public HomeCardAdapter(Context context, String channel_name[], String category[], String subscribe[], String view[]) {
        this.context = context;
        this.channel_name = channel_name;
        this.category = category;
        this.subscribe = subscribe;
        this.view = view;
    }

    // 绑定View和Adapter的function
    public class HomeCardViewHolder extends RecyclerView.ViewHolder {
        CardView cv_channel;
        TextView tv_channelName, tv_category, tv_subscribeValue, tv_viewValue;
        ImageView img_card, img_avatar;
        public HomeCardViewHolder(@NonNull View itemView) {
            super(itemView);
            //绑定Card里面的View
            cv_channel = itemView.findViewById(R.id.cardChannel);
            img_card = itemView.findViewById(R.id.imgChannel);
            img_avatar = itemView.findViewById(R.id.imgChannelAvatar);
            tv_channelName = itemView.findViewById(R.id.channelName);
            tv_category = itemView.findViewById(R.id.category);
            tv_subscribeValue = itemView.findViewById(R.id.subscribeValue);
            tv_viewValue = itemView.findViewById(R.id.viewValue);

        }
    }


    //初始化ViewHolder-用于渲染RecyclerView里面的具体Row
    @NonNull
    @Override
    public HomeCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycle_row_home, parent, false);
        return new HomeCardViewHolder(view);
    }

    //数据绑定-绑定每一Row的数据
    @Override
    public void onBindViewHolder(@NonNull HomeCardViewHolder holder, int position) {
        //卡片出现动画
        //holder.cardView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
        //数据绑定
        holder.tv_subscribeValue.setText(channel_name[position]);

        RequestOptions requestOptions = new RequestOptions();
        //当图片没加载出来时候,显示占位符
        requestOptions.placeholder(R.drawable.video_image);
        requestOptions.circleCropTransform();
        requestOptions.transform( new RoundedCorners(30));
        //load()可以放图片URL ("https://")
        Glide.with(context).load(R.drawable.video_image)
                .apply(requestOptions)
                .into(holder.img_card);
        Glide.with(context).load(R.drawable.avatar_image)
                .apply(requestOptions)
                .into(holder.img_avatar);
    }

    @Override
    public int getItemCount() {
        //设置Item的数量
        return channel_name.length;
    }



}
