
package com.example.myapplication.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChannelMostVideo {

    @SerializedName("videoMostViewInfo")
    @Expose
    private VideoMostViewInfo videoMostViewInfo;
    @SerializedName("videoMostCommentInfo")
    @Expose
    private VideoMostCommentInfo videoMostCommentInfo;
    @SerializedName("videoMostLikeInfo")
    @Expose
    private VideoMostLikeInfo videoMostLikeInfo;

    public VideoMostViewInfo getVideoMostViewInfo() {
        return videoMostViewInfo;
    }

    public void setVideoMostViewInfo(VideoMostViewInfo videoMostViewInfo) {
        this.videoMostViewInfo = videoMostViewInfo;
    }

    public VideoMostCommentInfo getVideoMostCommentInfo() {
        return videoMostCommentInfo;
    }

    public void setVideoMostCommentInfo(VideoMostCommentInfo videoMostCommentInfo) {
        this.videoMostCommentInfo = videoMostCommentInfo;
    }

    public VideoMostLikeInfo getVideoMostLikeInfo() {
        return videoMostLikeInfo;
    }

    public void setVideoMostLikeInfo(VideoMostLikeInfo videoMostLikeInfo) {
        this.videoMostLikeInfo = videoMostLikeInfo;
    }

}
