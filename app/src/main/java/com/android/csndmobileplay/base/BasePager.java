package com.android.csndmobileplay.base;

import android.content.Context;
import android.view.View;

/**
 * Created by 长春 on 2017/9/15.
 * 页面父类，给Fragment提供视图
 */

public abstract class BasePager {
    protected final Context mContext;
    private View mRootView;
    public  boolean isInitDate=false;

    public View getRootView() {
        return mRootView;
    }

    public BasePager(Context context) {
        this.mContext = context;
        mRootView = initView();
    }

    public abstract View initView();

    public abstract void initDate();
}
