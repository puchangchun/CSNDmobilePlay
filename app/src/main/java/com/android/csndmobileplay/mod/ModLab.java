package com.android.csndmobileplay.mod;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.android.csndmobileplay.Util.Utils;
import com.android.csndmobileplay.bean.NetVideoInfo;
import com.android.csndmobileplay.bean.SoundInfo;
import com.android.csndmobileplay.bean.VideoInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 单例-双重效验
 * Created by 长春 on 2017/9/26.
 */

public class ModLab {
    private static final String SOUNDS_FOLDER = "sample_sounds";
    private static final String TAG = "ModLab_Test";
    /**
     * 最大音频并发播放数
     */
    private final int maxSteams = 5;

    private  List<VideoInfo> mLocalVideoList;
    private  List<NetVideoInfo> mNetVideoList;
    private  List<SoundInfo> mSoundList;
    private AssetManager mAssetManager;
    private SoundPool mSoundPool;
    private Boolean isLoadSounds = false;

    private static ModLab sModLab;

    public static ModLab get(Context context) {
        if (sModLab == null) {
            synchronized (ModLab.class) {
                if (sModLab == null) {
                    sModLab = new ModLab(context);
                }
            }
        }
        return sModLab;
    }

    private ModLab(Context context) {
        mLocalVideoList = new ArrayList<>();
        mNetVideoList = new ArrayList<>();
        mSoundList = new ArrayList<>();
        mAssetManager = context.getAssets();


    }

    public List<VideoInfo> getLocalVideoList() {
        return mLocalVideoList;
    }

    public List<NetVideoInfo> getNetVideoList() {
        return mNetVideoList;
    }

    public List<SoundInfo> getSoundList(){
        if (!isLoadSounds){
            loadSound();
        }
        return mSoundList;
    }

    /**
     *
     * @param videoInfo
     */
    public void addNetVideoInfo(NetVideoInfo videoInfo) {
        mNetVideoList.add(videoInfo);
    }

    /**
     *
     * @param position
     */
    public void removeNetVideoInfo(int position) {
        mNetVideoList.remove(position);
    }

    /**
     *
     * @param position
     * @return
     */
    public NetVideoInfo getNetVideoInfo(int position) {
        return mNetVideoList.get(position);
    }

    /**
     * 获得assets的资源清单
     */
    private void loadSound(){

        Log.e(TAG, "loadSound: " );
        String[] soundNames = null;
        try {
             soundNames = mAssetManager.list(SOUNDS_FOLDER);
            Log.d(TAG, "loadSound: "+soundNames.length+" sounds");
            if (soundNames != null ){
                for (String name : soundNames){
                    SoundInfo soundInfo = new SoundInfo(SOUNDS_FOLDER + "/" + name);
                    mSoundList.add(soundInfo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        isLoadSounds = true;
    }

    /**
     * 加载Asset音频文件到SoundPool
     * @param soundInfo
     * @throws IOException
     */
    private void load(SoundInfo soundInfo) throws IOException {
        AssetFileDescriptor assetFileDescriptor = mAssetManager.openFd(soundInfo.getAssetPath());
        int loadId = mSoundPool.load(assetFileDescriptor, 1);
        soundInfo.setSoundId(loadId);
    }

    /**
     * 播放音频
     * @param sound
     */
    public void playSound(SoundInfo sound){
        int loadId = sound.getSoundId();
        mSoundPool.play(loadId,1.0f,1.0f,1,0,1.0f);
    }

    /**
     * 释放音频内存资源
     */
    public void releaseSounds(){
        Log.e(TAG, "releaseSounds: " );
        mSoundPool.release();
    }

    /**
     * 重新加载SoundPool资源
     */
    public void reLoadSoundPool(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(maxSteams)
                    .build();
        } else {
            mSoundPool = new SoundPool(maxSteams, AudioManager.STREAM_MUSIC, 0);
        }

        Log.e(TAG, "reLoadSoundPool: ");
        try {
            for (SoundInfo s : mSoundList) {
                load(s);
            }
        } catch (IOException e) {
                e.printStackTrace();
        }

    }

}
