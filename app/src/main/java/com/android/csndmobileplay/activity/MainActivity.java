package com.android.csndmobileplay.activity;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.csndmobileplay.R;
import com.android.csndmobileplay.base.BasePager;
import com.android.csndmobileplay.base.ContentFragment;
import com.android.csndmobileplay.pager.BeatBoxPager;
import com.android.csndmobileplay.pager.NetAudioPager;
import com.android.csndmobileplay.pager.NetVideoPager;
import com.android.csndmobileplay.pager.VideoPager;

import java.util.ArrayList;
import java.util.List;

import com.android.csndmobileplay.Util.Utils;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    //托管fragment的容器
    private FrameLayout mFragmentContainer;
    private RadioGroup mRadioGroup;
    private List<BasePager> mBasePagers;
    private  BasePager mCheckedPager;
    private int mCheckedPosition;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewAndValue();
        initDate();
        initListener();
    }


    private void initListener() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch ( i ){
                    case R.id.rb_video:
                        mCheckedPosition = 0 ;
                        break;
                    case R.id.rb_music:
                        mCheckedPosition = 1;
                        break;
                    case R.id.rb_net_video:
                        mCheckedPosition = 2;
                        break;
                    case R.id.rb_net_music:
                        mCheckedPosition = 3;
                        break;
                    default:
                        break;
                }

                setFragmentContainer();
            }
        });
    }
    private void initDate() {
        mCheckedPosition = 0;
        //初始化pagers数据
        //0 - 3
        mBasePagers = new ArrayList<>();
        mBasePagers.add(new VideoPager(this));
        mBasePagers.add(new BeatBoxPager(this));
        mBasePagers.add(new NetVideoPager(this));
        mBasePagers.add(new NetAudioPager(this));
        setFragmentContainer();

    }
    private void initViewAndValue() {
        mToolbar = (Toolbar) findViewById(R.id.main_activity_search_toolbar);
        mRadioGroup =(RadioGroup)findViewById(R.id.main_act_rg);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        setBottomDrawableSize();

    }

    /**
     * 刷新Fragment
     * 将mCheckedPager交给Fragment
     * 打包提交给fragmentManager
     */
    private void setFragmentContainer() {

        if(mCheckedPager != null){
            mCheckedPager.releaseDate();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        updateCheckedBasePager();
        //旋转时，如果fragment不存在 activity调用fragment会使用无参数构造函数 会出错
        if (mCheckedPager != null){
            ft.replace(R.id.fl_fragment_content,new ContentFragment(mCheckedPager));
            ft.commit();
        }
    }

    /**
     * 刷新选中的页面
     * @return
     */
    private void updateCheckedBasePager() {
        mCheckedPager = mBasePagers.get(mCheckedPosition);
        if (!mCheckedPager.isInitDate) {
            mCheckedPager.initDate();
        }

    }

    /**
     * 设置底部导航图标的drawable大小
     */
    private void setBottomDrawableSize() {
        RadioButton[] rbs = new RadioButton[4];
        rbs[0] = (RadioButton) findViewById(R.id.rb_video);
        rbs[1] = (RadioButton) findViewById(R.id.rb_music);
        rbs[2] = (RadioButton) findViewById(R.id.rb_net_music);
        rbs[3] = (RadioButton) findViewById(R.id.rb_net_video);
        int height=0,width=0;
        Drawable[] drs;
        for (RadioButton rb : rbs){
                //返回控件中的左 上 右 下的drawable
                drs = rb.getCompoundDrawables();
            int l = Utils.dip2px(getApplicationContext(),25);
                Rect r = new Rect(0, 0,l,l);
                drs[1].setBounds(r);
                rb.setCompoundDrawables(null,drs[1],null,null);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
