
package com.example.myapplication.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChannelVideo {

    @SerializedName("videoInMonthInfo")
    @Expose
    private List<VideoInMonthInfo> videoInMonthInfo = null;
    @SerializedName("videoMostViewInfo")
    @Expose
    private VideoMostViewInfo videoMostViewInfo;

    public List<VideoInMonthInfo> getVideoInMonthInfo() {
        return videoInMonthInfo;
    }

    public void setVideoInMonthInfo(List<VideoInMonthInfo> videoInMonthInfo) {
        this.videoInMonthInfo = videoInMonthInfo;
    }

    public VideoMostViewInfo getVideoMostViewInfo() {
        return videoMostViewInfo;
    }

    public void setVideoMostViewInfo(VideoMostViewInfo videoMostViewInfo) {
        this.videoMostViewInfo = videoMostViewInfo;
    }

}
