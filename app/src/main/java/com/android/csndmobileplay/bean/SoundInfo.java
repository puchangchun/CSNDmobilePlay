package com.android.csndmobileplay.bean;

public class SoundInfo {
    private String mAssetPath;
    private String mName;
    private int mSoundId;

    public SoundInfo(String assetPath) {
        mAssetPath = assetPath;
        String[] strings = mAssetPath.split("/");
        mName = strings[strings.length-1].replace(".wav","");
    }

    public int getSoundId() {
        return mSoundId;
    }

    public void setSoundId(int soundId) {
        mSoundId = soundId;
    }

    public String getAssetPath() {
        return mAssetPath;
    }

    public void setAssetPath(String assetPath) {
        mAssetPath = assetPath;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
