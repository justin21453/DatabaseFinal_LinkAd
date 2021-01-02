package com.example.myapplication.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryCard {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("allvideoCount")
    @Expose
    private String allvideoCount;
    @SerializedName("allviewCount")
    @Expose
    private String allviewCount;
    @SerializedName("avatar_url_high")
    @Expose
    private String avatarUrlHigh;
    @SerializedName("avatar_url_small")
    @Expose
    private String avatarUrlSmall;
    @SerializedName("channelId")
    @Expose
    private String channelId;
    @SerializedName("channelTitle")
    @Expose
    private String channelTitle;
    @SerializedName("channel_url")
    @Expose
    private String channelUrl;
    @SerializedName("datatime")
    @Expose
    private String datatime;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("publishedAt")
    @Expose
    private String publishedAt;
    @SerializedName("subscriber")
    @Expose
    private String subscriber;
    @SerializedName("categoryIds")
    @Expose
    private List<CategoryId> categoryIds = null;
    @SerializedName("rank")
    @Expose
    private Integer rank;
    @SerializedName("rankTotal")
    @Expose
    private Integer rankTotal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAllvideoCount() {
        return allvideoCount;
    }

    public void setAllvideoCount(String allvideoCount) {
        this.allvideoCount = allvideoCount;
    }

    public String getAllviewCount() {
        return allviewCount;
    }

    public void setAllviewCount(String allviewCount) {
        this.allviewCount = allviewCount;
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

    public String getChannelUrl() {
        return channelUrl;
    }

    public void setChannelUrl(String channelUrl) {
        this.channelUrl = channelUrl;
    }

    public String getDatatime() {
        return datatime;
    }

    public void setDatatime(String datatime) {
        this.datatime = datatime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(String subscriber) {
        this.subscriber = subscriber;
    }

    public List<CategoryId> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<CategoryId> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getRankTotal() {
        return rankTotal;
    }

    public void setRankTotal(Integer rankTotal) {
        this.rankTotal = rankTotal;
    }

}