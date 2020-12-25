package com.example.myapplication.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChannelCard {

    @SerializedName("channelId")
    @Expose
    private String channelId;
    @SerializedName("channelTitle")
    @Expose
    private String channelTitle;
    @SerializedName("avatar_url_high")
    @Expose
    private String avatarUrlHigh;
    @SerializedName("avatar_url_small")
    @Expose
    private String avatarUrlSmall;
    @SerializedName("subscriber")
    @Expose
    private String subscriber;
    @SerializedName("allViewCount")
    @Expose
    private String allViewCount;
    @SerializedName("video_url")
    @Expose
    private String videoUrl;
    @SerializedName("tag")
    @Expose
    private List<String> tag = null;
    @SerializedName("channelCategory")
    @Expose
    private String channelCategory;
    @SerializedName("thumbnails_url_high")
    @Expose
    private String thumbnailsUrlHigh;
    @SerializedName("thumbnails_url_small")
    @Expose
    private String thumbnailsUrlSmall;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getAvatarUrlHigh() {
        return avatarUrlHigh;
    }

    public void setAvatarUrlHigh(String avatarUrlHigh) {
        this.avatarUrlHigh = avatarUrlHigh;
    }

    public String getAvatarUrlSmall() {
        return avatarUrlSmall;
    }

    public void setAvatarUrlSmall(String avatarUrlSmall) {
        this.avatarUrlSmall = avatarUrlSmall;
    }

    public String getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(String subscriber) {
        this.subscriber = subscriber;
    }

    public String getAllViewCount() {
        return allViewCount;
    }

    public void setAllViewCount(String allViewCount) {
        this.allViewCount = allViewCount;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public String getChannelCategory() {
        return channelCategory;
    }

    public void setChannelCategory(String channelCategory) {
        this.channelCategory = channelCategory;
    }

    public String getThumbnailsUrlHigh() {
        return thumbnailsUrlHigh;
    }

    public void setThumbnailsUrlHigh(String thumbnailsUrlHigh) {
        this.thumbnailsUrlHigh = thumbnailsUrlHigh;
    }

    public String getThumbnailsUrlSmall() {
        return thumbnailsUrlSmall;
    }

    public void setThumbnailsUrlSmall(String thumbnailsUrlSmall) {
        this.thumbnailsUrlSmall = thumbnailsUrlSmall;
    }


}