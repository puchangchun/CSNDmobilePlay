package com.android.csndmobileplay.bean;

import java.io.Serializable;

/**
 * Created by 长春 on 2017/9/16.
 */

public class VideoInfo implements Serializable{
    private static final long serialVersionUID= 1L;
    private long mVideoSize;
    private long mVideoDuration;
    private String mVideoName;
    private String mVideoArtist;
    private String mVideoPath;


    public VideoInfo() {
    }

    public VideoInfo(int vedioSize, int vedioDuration, String vedioName) {
        mVideoSize = vedioSize;
        mVideoDuration = vedioDuration;
        mVideoName = vedioName;
    }

    public String getVideoArtist() {
        return mVideoArtist;
    }

    public void setVideoArtist(String videoArtist) {
        mVideoArtist = videoArtist;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    public void setVideoPath(String videoPath) {
        mVideoPath = videoPath;
    }

    public long getVideoSize() {
        return mVideoSize;
    }

    public void setVideoSize(long videoSize) {
        mVideoSize = videoSize;
    }

    public long getVideoDuration() {
        return mVideoDuration;
    }

    public void setVideoDuration(long videoDuration) {
        mVideoDuration = videoDuration;
    }

    public String getVideoName() {
        return mVideoName;
    }

    public void setVideoName(String videoName) {
        mVideoName = videoName;
    }
}
