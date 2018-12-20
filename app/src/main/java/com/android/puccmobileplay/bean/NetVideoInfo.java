package com.android.puccmobileplay.bean;

/**
 * Created by 99653 on 2017/10/17.
 */

public class NetVideoInfo {
    private String mVideoName;
    private String mCoverImgUrl;
    private String mSummary;
    private String mHeightUrl;
    private String mVideoLength;

    public String getVideoLength() {
        return mVideoLength;
    }

    public void setVideoLength(String videoLength) {
        mVideoLength = videoLength;
    }

    public String getVideoName() {
        return mVideoName;
    }

    public void setVideoName(String videoName) {
        mVideoName = videoName;
    }

    public String getCoverImgUrl() {
        return mCoverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        mCoverImgUrl = coverImgUrl;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getHeightUrl() {
        return mHeightUrl;
    }

    public void setHeightUrl(String hightUrl) {
        mHeightUrl = hightUrl;
    }
}
