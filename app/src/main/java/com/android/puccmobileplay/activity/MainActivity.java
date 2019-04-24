package com.android.puccmobileplay.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import com.android.puccmobileplay.R;
import com.android.puccmobileplay.base.AlertDialogCallBack;
import com.android.puccmobileplay.base.BasePager;
import com.android.puccmobileplay.base.ContentFragment;
import com.android.puccmobileplay.pager.NetVideoPager;
import com.android.puccmobileplay.pager.SettingsPager;
import com.android.puccmobileplay.pager.VideoPager;

import java.util.ArrayList;
import java.util.List;

import com.android.puccmobileplay.Util.Utils;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private Toolbar mToolbar;
    //托管fragment的容器
    private FrameLayout mFragmentContainer;
    private RadioGroup mRadioGroup;
    private List<BasePager> mBasePagers;
    private  BasePager mCheckedPager;

    private EaseConversationListFragment mEaseConversationList;

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
                        mCheckedPosition = 99;
                        break;
                    case R.id.rb_net_music:
                        mCheckedPosition = 2;
                        break;
                    default:
                        break;
                }

                setFragmentContainer();
            }
        });

        mEaseConversationList.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);

                // 传递参数
                intent.putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId());

                // 是否是否群聊
                if(conversation.getType() == EMConversation.EMConversationType.GroupChat) {
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                }

                startActivity(intent);
            }
        });
    }

    private void setupConversation() {

    }

    private void initDate() {
        mCheckedPosition = 0;
        //初始化pagers数据
        //0 - 3
        mBasePagers = new ArrayList<>();
        mBasePagers.add(new VideoPager(this));
        mBasePagers.add(new NetVideoPager(this));
        mBasePagers.add(new SettingsPager(this));
        mEaseConversationList = new EaseConversationListFragment();
        mEaseConversationList.hideTitleBar();


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

        if (mCheckedPosition == 99){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fl_fragment_content, mEaseConversationList);
            ft.commit();
        }else {

            if (mCheckedPager != null) {
                mCheckedPager.releaseDate();
            }
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            updateCheckedBasePager();
            //旋转时，如果fragment不存在 activity调用fragment会使用无参数构造函数 会出错
            if (mCheckedPager != null) {
                ft.replace(R.id.fl_fragment_content, new ContentFragment(mCheckedPager));
                ft.commit();
            }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_toolbar_history:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    Intent intent = new Intent(MainActivity.this,CaptureActivity.class);
                    startActivityForResult(intent,REQUEST_CODE);
                }
                return true;

                default:
                    return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    Log.d(TAG, "onActivityResult: bundle is null");
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Utils.showAlertDialog(this, "", result, new AlertDialogCallBack() {
                        @Override
                        public void startPositive() {

                        }

                        @Override
                        public void startNegative() {

                        }
                    });
                    Log.d(TAG, "onActivityResult: " + result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Log.d(TAG, "onActivityResult: " + "fail");
                }
            }

        }
    }
}
