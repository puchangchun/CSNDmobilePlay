package com.android.puccmobileplay.base;

import android.app.Application;
import android.util.DisplayMetrics;


import com.android.puccmobileplay.model.ModelController;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.uuzuche.lib_zxing.DisplayUtil;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.litepal.LitePal;
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
        ZXingLibrary.initDisplayOpinion(this);
        LitePal.initialize(this);
        EMOptions option = new EMOptions();
        option.setAutoAcceptGroupInvitation(false);
        EaseUI.getInstance().init(this,option);

        ModelController.getInstance().init(this);

        /**
         * 初始化尺寸工具类
         */
        initDisplayOpinion();

    }


    private void initDisplayOpinion() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        DisplayUtil.density = dm.density;
        DisplayUtil.densityDPI = dm.densityDpi;
        DisplayUtil.screenWidthPx = dm.widthPixels;
        DisplayUtil.screenhightPx = dm.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getApplicationContext(), dm.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(getApplicationContext(), dm.heightPixels);
    }
}
