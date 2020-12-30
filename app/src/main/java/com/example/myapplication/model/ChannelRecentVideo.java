
package com.example.myapplication.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChannelRecentVideo {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("categoryId")
    @Expose
    private String categoryId;
    @SerializedName("channelId")
    @Expose
    private String channelId;
    @SerializedName("channelTitle")
    @Expose
    private String channelTitle;
    @SerializedName("commentCount")
    @Expose
    private String commentCount;
    @SerializedName("datatime")
    @Expose
    private String datatime;
    @SerializedName("dislikeCount")
    @Expose
    private String dislikeCount;
    @SerializedName("likeCount")
    @Expose
    private String likeCount;
    @SerializedName("publishedAt")
    @Expose
    private String publishedAt;
    @SerializedName("tag")
    @Expose
    private List<String> tag = null;
    @SerializedName("thumbnails url high")
    @Expose
    private String thumbnailsUrlHigh;
    @SerializedName("thumbnails url small")
    @Expose
    private String thumbnailsUrlSmall;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("videoId")
    @Expose
    private String videoId;
    @SerializedName("video_url")
    @Expose
    private String videoUrl;
    @SerializedName("viewCount")
    @Expose
    private String viewCount;
    @SerializedName("publishedAtDate")
    @Expose
    private String publishedAtDate;
    @SerializedName("curDate")
    @Expose
    private String curDate;
    @SerializedName("isBeforeAMonth")
    @Expose
    private Integer isBeforeAMonth;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getDatatime() {
        return datatime;
    }

    public void setDatatime(String datatime) {
        this.datatime = datatime;
    }

    public String getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(String dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getPublishedAtDate() {
        return publishedAtDate;
    }

    public void setPublishedAtDate(String publishedAtDate) {
        this.publishedAtDate = publishedAtDate;
    }

    public String getCurDate() {
        return curDate;
    }

    public void setCurDate(String curDate) {
        this.curDate = curDate;
    }

    public Integer getIsBeforeAMonth() {
        return isBeforeAMonth;
    }

    public void setIsBeforeAMonth(Integer isBeforeAMonth) {
        this.isBeforeAMonth = isBeforeAMonth;
    }

}
