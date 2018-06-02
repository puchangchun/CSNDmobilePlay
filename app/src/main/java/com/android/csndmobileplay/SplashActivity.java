package com.android.csndmobileplay;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.android.csndmobileplay.activity.PermissionRequest;

import org.xutils.x;

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
                  startMainActivity();
                  isToMain=true;
              }
            }
        },2000);
    }

    private void startMainActivity() {
        startActivity(new Intent(SplashActivity.this,PermissionRequest.class));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: "+event.toString());
        if(!isToMain){
            startMainActivity();
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
