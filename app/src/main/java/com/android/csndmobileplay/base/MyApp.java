package com.android.csndmobileplay.base;

import android.app.Application;

import org.xutils.x;

/**
 * Created by 99653 on 2017/10/17.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(false);
    }
}
