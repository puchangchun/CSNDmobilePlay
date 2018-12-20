package com.android.puccmobileplay.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 长春 on 2017/9/15.
 */

public class ContentFragment extends Fragment {
    private BasePager mPager;
    public ContentFragment(BasePager basePager) {
        mPager=basePager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //旋转时，fragment会存留一段时间，给activity重新调用，以保存数据
        setRetainInstance(true);
        if (mPager != null){
            return mPager.getRootView();
        }
        return null;
    }

}
