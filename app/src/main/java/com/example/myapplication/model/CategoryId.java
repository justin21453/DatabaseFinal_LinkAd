
package com.example.myapplication.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryId {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("videoCount")
    @Expose
    private Integer videoCount;
    @SerializedName("videoViews")
    @Expose
    private Integer videoViews;
    @SerializedName("totalLike")
    @Expose
    private Integer totalLike;
    @SerializedName("totalDislike")
    @Expose
    private Integer totalDislike;
    @SerializedName("avgRating")
    @Expose
    private Double avgRating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(Integer videoCount) {
        this.videoCount = videoCount;
    }

    public Integer getVideoViews() {
        return videoViews;
    }

    public void setVideoViews(Integer videoViews) {
        this.videoViews = videoViews;
    }

    public Integer getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(Integer totalLike) {
        this.totalLike = totalLike;
    }

    public Integer getTotalDislike() {
        return totalDislike;
    }

    public void setTotalDislike(Integer totalDislike) {
        this.totalDislike = totalDislike;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

}
