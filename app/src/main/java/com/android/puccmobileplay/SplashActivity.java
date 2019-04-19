package com.android.puccmobileplay;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.android.puccmobileplay.activity.LoginActivity;
import com.android.puccmobileplay.activity.MainActivity;
import com.android.puccmobileplay.activity.PermissionActivity;
import com.android.puccmobileplay.model.ModelController;
import com.android.puccmobileplay.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;


public class SplashActivity extends AppCompatActivity {
    private boolean isToMain = false;
    private Handler mHandler;

    private static final String TAG =SplashActivity.class.getSimpleName() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
              if(!isToMain){
                  startMainOrLogin();
                  isToMain=true;
              }
            }
        },2000);
    }

    private void startMainActivity() {
        startActivity(new Intent(SplashActivity.this,PermissionActivity.class));
    }


    private void startMainOrLogin() {
        ModelController.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (EMClient.getInstance().isLoggedInBefore()){
                    //之前登陆过，到数据库中获取用户信息
                    String hxId = EMClient.getInstance().getCurrentUser();
                    UserInfo userInfo = ModelController.getInstance().getUserAccountDao().getAccountByHxId(hxId);
                    if (userInfo == null){
                        startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                    }else {

                        ModelController.getInstance().loginSuccess(userInfo);

                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    }
                }else {
                    startActivity(new Intent(SplashActivity.this,PermissionActivity.class));
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: "+event.toString());
        if(!isToMain){
            startMainOrLogin();
            isToMain=true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
       // mHandler.removeCallbacksAndMessages(null);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
