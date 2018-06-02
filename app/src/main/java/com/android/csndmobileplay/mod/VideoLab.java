package com.android.csndmobileplay.mod;

import android.content.Context;

import com.android.csndmobileplay.bean.NetVideoInfo;
import com.android.csndmobileplay.bean.VideoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 单例懒汉模式，多线程不安全
 * Created by 长春 on 2017/9/26.
 */

public class VideoLab {
    private static List<VideoInfo> sLocalVideoList;
    private static List<NetVideoInfo> sNetVideoList;
    private static VideoLab sVideoLab;

    public static VideoLab get(Context context) {
        if (sVideoLab == null) {
            sVideoLab = new VideoLab(context);
        }
        return sVideoLab;
    }

    public VideoLab(Context context) {
        sLocalVideoList = new ArrayList<>();
        sNetVideoList = new ArrayList<>();
    }

    public List<VideoInfo> getLocalVideoList() {
        return sLocalVideoList;
    }

    public List<NetVideoInfo> getNetVideoList() {
        return sNetVideoList;
    }

    public void addNetVideoInfo(NetVideoInfo videoInfo) {
        sNetVideoList.add(videoInfo);
    }

    public void removeNetVideoInfo(int position) {
        sNetVideoList.remove(position);
    }

    public NetVideoInfo getNetVideoInfo(int position) {
        return sNetVideoList.get(position);
    }

}
