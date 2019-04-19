package com.android.puccmobileplay.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.puccmobileplay.R;
import com.android.puccmobileplay.bean.VideoInfo;
import com.android.puccmobileplay.mod.ModLab;
import com.android.puccmobileplay.view.VolumeView;
import com.android.puccmobileplay.view.MyVideoView;

import java.util.Calendar;
import java.util.List;

import com.android.puccmobileplay.Util.Utils;

import io.vov.vitamio.widget.VideoView;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.media.MediaPlayer.MEDIA_ERROR_IO;
import static io.vov.vitamio.MediaPlayer.MEDIA_ERROR_MALFORMED;
import static io.vov.vitamio.MediaPlayer.MEDIA_ERROR_TIMED_OUT;

public class VideoPlayer extends AppCompatActivity implements View.OnClickListener {
    private static final int UPDATE_PROGRESS = 0;
    private static final int UPDATE_TIME = 1;
    private static final String LIST_VIDEO_INFO = "video_list";
    private static final String PLAY_VIDEO_POSITION = "play_video_position";
    private static final int UPDATE_VIDEO_CONTROLLER = 2;
    private static final int UPDATE_VOLUME_CONTROLLER = 3;
    private static final int UPDATE_NET_SPEED = 4;
    private static final String TAG = VideoPlayer.class.getSimpleName();
    private MyVideoView mSystemVideoView;
    private TextView mVideoName;
    private ImageView mCurrentPowe;
    private TextView mVideoDuration;
    private TextView mVideoCurrentTime;
    private SeekBar mVideoSeekBar;
    private TextView mVideoTotalTime;
    private ImageButton mBtnExit;
    private Button mBtnVideoPre;
    private Button mBtnVideoControl;
    private Button mBtnVideoNext;
    private ImageButton mBtnSwitchScreen;
    private boolean isTouchSeekBar = false;
    private Calendar mCalendar;
    private List<VideoInfo> mVideoList;
    private int mPlayVideoPosition;
    private Uri mPlayVideoUri;
    private BatteryReceiver mBatteryReceiver;
    private GestureDetector mGestureDetector;
    private LinearLayout mControllerBottom;
    private RelativeLayout mBufferingView;
    private VolumeView mControllerVolume;
    private LinearLayout mControllerInfo;
    private LinearLayout mVideoBuffering;
    private boolean isShowController;
    private boolean isLandscape;
    private boolean isUseInfoBufferingListener;
    private AudioManager audioManager;
    private RelativeLayout mController;
    private int screenWidth;
    private int screenHeight;
    private GestureDetector mGestureController;
    private int mSystemMaxVolume;
    private RelativeLayout mTopViewGroup;
    private int mSystemCurrentVolume;
    private int newVolume;
    private boolean isShowVolume;
    private float mDownScreenX;
    private float mDownScreenY;
    private boolean isNetUri;
    private boolean isVitamioVideo;
    private String mNetSpeedTxt;
    private TextView mBufferingViewSpeed;
    private VideoView mVitamioVideoView;
    private Handler mHandler;


    public static Intent newIntent(Context context, int position) {
        Intent intent = new Intent(context, VideoPlayer.class);
/*        Bundle bundle = new Bundle();
        bundle.putSerializable(LIST_VIDEO_INFO,list);
        intent.putExtras(bundle);
        intent.putExtra(LIST_VIDEO_INFO,list);*/
        intent.putExtra(PLAY_VIDEO_POSITION, position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE)
            isLandscape = true;
        getData();
        mHandler = new VideoPlayerHandler();
        initViewAndValue();
        initListener();
        initData();

    }

    private void getData() {
        if (getIntent().getData() != null) {
            mPlayVideoUri = getIntent().getData();
            isNetUri = Utils.isNetUri(mPlayVideoUri.toString());
        } else {
            mPlayVideoPosition = getIntent().getIntExtra(PLAY_VIDEO_POSITION, 0);
            mVideoList = ModLab.get(this).getLocalVideoList();
            mPlayVideoUri = Uri.parse(mVideoList.get(mPlayVideoPosition).getVideoPath());
        }
    }



    private void initData() {
        if (mPlayVideoUri != null) mSystemVideoView.setVideoURI(mPlayVideoUri);

        //开始系统时间更新
        mHandler.sendEmptyMessage(UPDATE_TIME);
        //开始系统网速更新,设置缓冲页面
        if (isNetUri) {
            mHandler.sendEmptyMessage(UPDATE_NET_SPEED);
        } else {
            mBufferingView.setVisibility(View.GONE);
            mVideoBuffering.setVisibility(View.GONE);
        }
        //注册电量广播接收器
        mBatteryReceiver = new BatteryReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryReceiver, filter);
        if (mVideoList == null) {
            mBtnVideoNext.setVisibility(View.GONE);
        }
        if (mVideoList != null && mPlayVideoPosition + 2 > mVideoList.size()) {
            mBtnVideoNext.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        mBtnExit.setOnClickListener(this);
        mBtnVideoControl.setOnClickListener(this);
        mBtnVideoNext.setOnClickListener(this);
        mBtnSwitchScreen.setOnClickListener(this);
        initSystemVideoListener();
    }

    private void initSystemVideoListener() {
        if (isUseInfoBufferingListener) {
            mSystemVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            mVideoBuffering.setVisibility(View.VISIBLE);
                            mHandler.sendEmptyMessage(UPDATE_NET_SPEED);
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            mVideoBuffering.setVisibility(View.GONE);
                            mHandler.removeMessages(UPDATE_NET_SPEED);
                            break;
                    }
                    return true;
                }
            });
        }
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                //双击事件
                if (mSystemVideoView.isPlaying()) {
                    mSystemVideoView.pause();
                    mBtnVideoControl.setBackgroundResource(R.drawable.btn_video_start);
                } else {
                    mSystemVideoView.start();
                    mBtnVideoControl.setBackgroundResource(R.drawable.btn_video_pause);
                }
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //单击事件
                mHandler.sendEmptyMessage(UPDATE_VIDEO_CONTROLLER);
                return super.onSingleTapConfirmed(e);
            }
        });


        //视频准备完毕
        mSystemVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                //在视频加载好后，设置SeekBar的监听
                mVideoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (isTouchSeekBar) {
                            if (isVitamioVideo) {
                                mVitamioVideoView.seekTo(progress);
                            } else {
                                mSystemVideoView.seekTo(progress);
                            }
                        }
                        mVideoCurrentTime.setText(String.valueOf(Utils.msToTime(progress)));

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //滑动时处理控制窗口的显示
                        mSystemVideoView.pause();
                        mHandler.removeMessages(UPDATE_VIDEO_CONTROLLER);
                        mHandler.removeMessages(UPDATE_PROGRESS);
                        isTouchSeekBar = true;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mSystemVideoView.start();
                        mHandler.sendEmptyMessage(UPDATE_PROGRESS);
                        mHandler.sendEmptyMessageDelayed(UPDATE_VIDEO_CONTROLLER, 5000);
                        isTouchSeekBar = false;
                    }
                });
                mVideoTotalTime.setText(String.valueOf(Utils.msToTime(mSystemVideoView.getDuration())));
                mSystemVideoView.seekTo(mVideoSeekBar.getProgress());
                mVideoSeekBar.setMax(mSystemVideoView.getDuration());
                mSystemVideoView.start();
                mHandler.removeMessages(UPDATE_PROGRESS);
                mHandler.sendEmptyMessage(UPDATE_PROGRESS);
                mBufferingView.setVisibility(View.GONE);

            }
        });
        //视频播放完成
        mSystemVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (!isNetUri) {
                    toNextVideo();
                }
            }
        });
        //视频初始化失败
        mSystemVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Toast.makeText(getApplication(), "切换软件解码器", Toast.LENGTH_SHORT).show();
                startVitamioVideo();
                return true;
            }
        });
    }

    private void startVitamioVideo() {
        isVitamioVideo = true;
        if (mSystemVideoView != null) {
            mSystemVideoView.stopPlayback();
        }
        mHandler.removeMessages(UPDATE_PROGRESS);
        mSystemVideoView.setVisibility(View.GONE);
        mVitamioVideoView.setVisibility(View.VISIBLE);
        initVitamioListener();
        if (mPlayVideoUri != null) {
            mVitamioVideoView.setVideoURI(mPlayVideoUri);
        }
    }

    private void startSystemVideo() {
        isVitamioVideo = false;
        if (mVitamioVideoView != null) {
            mVitamioVideoView.stopPlayback();
        }
        initSystemVideoListener();
        mHandler.removeMessages(UPDATE_PROGRESS);
        mVitamioVideoView.setVisibility(View.GONE);
        mSystemVideoView.setVisibility(View.VISIBLE);
        if (mPlayVideoUri != null) {
            mSystemVideoView.setVideoURI(mPlayVideoUri);
        }
    }

    private void initVitamioListener() {
        /**
         * 注册视频卡顿的系统监听
         */
        if (isUseInfoBufferingListener) {
            mVitamioVideoView.setOnInfoListener(new io.vov.vitamio.MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(io.vov.vitamio.MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        case io.vov.vitamio.MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            mVideoBuffering.setVisibility(View.VISIBLE);
                            mHandler.sendEmptyMessage(UPDATE_NET_SPEED);
                            break;
                        case io.vov.vitamio.MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            mVideoBuffering.setVisibility(View.GONE);
                            mHandler.removeMessages(UPDATE_NET_SPEED);
                            break;
                    }
                    return true;
                }
            });
        }
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                //双击事件
                if (mVitamioVideoView.isPlaying()) {
                    mVitamioVideoView.pause();
                    mBtnVideoControl.setBackgroundResource(R.drawable.btn_video_start);
                } else {
                    mVitamioVideoView.start();
                    mBtnVideoControl.setBackgroundResource(R.drawable.btn_video_pause);
                }
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //单击事件
                mHandler.sendEmptyMessage(UPDATE_VIDEO_CONTROLLER);
                return super.onSingleTapConfirmed(e);
            }
        });


        /**
         * 视频解析完成，准备播放的监听
         */
        mVitamioVideoView.setOnPreparedListener(new io.vov.vitamio.MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(io.vov.vitamio.MediaPlayer mediaPlayer) {
                mVideoTotalTime.setText(String.valueOf(Utils.msToTime(mVitamioVideoView.getDuration())));
                mVitamioVideoView.seekTo(mVideoSeekBar.getProgress());
                mVideoSeekBar.setMax((int) mVitamioVideoView.getDuration());
                mVitamioVideoView.start();
                mHandler.removeMessages(UPDATE_PROGRESS);
                mHandler.sendEmptyMessage(UPDATE_PROGRESS);
                mBufferingView.setVisibility(View.GONE);
            }
        });
        /**
         * 视频播放完成的监听
         */
        mVitamioVideoView.setOnCompletionListener(new io.vov.vitamio.MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(io.vov.vitamio.MediaPlayer mediaPlayer) {
                if (!isNetUri) {
                    toNextVideo();
                }
            }
        });
        /**
         * 视频初始化失败的监听
         */
        mVitamioVideoView.setOnErrorListener(new io.vov.vitamio.MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(io.vov.vitamio.MediaPlayer mediaPlayer, int i, int i1) {
                switch (i) {
                    case MEDIA_ERROR_IO:
                        showErrorDialog("File or network related operation errors");
                        break;
                    case MEDIA_ERROR_MALFORMED:
                        showErrorDialog("itstream is not conforming to the related coding standard or file spec");
                        break;
                    case MEDIA_ERROR_TIMED_OUT:
                        showErrorDialog(" Some operation takes too long to complete, usually more than 3-5 seconds.");
                        break;
                    default:
                        showErrorDialog("播放出现错误");
                        break;
                }
                return true;
            }
        });


    }

    private void initViewAndValue() {
        mBufferingViewSpeed = (TextView) findViewById(R.id.activity_video_buffering_speed);
        mBufferingView = (RelativeLayout) findViewById(R.id.activity_video_buffering_view);
        mController = (RelativeLayout) findViewById(R.id.my_video_controller);
        mVideoName = (TextView) findViewById(R.id.my_video_txv_name);
        mCurrentPowe = (ImageView) findViewById(R.id.my_video_img_power);
        mVideoDuration = (TextView) findViewById(R.id.my_video_txv_duration);
        mTopViewGroup = (RelativeLayout) findViewById(R.id.video_player_top_relative);
        mVideoCurrentTime = (TextView) findViewById(R.id.my_video_txv_current_time);
        mVideoSeekBar = (SeekBar) findViewById(R.id.my_video_seek_bar_video);
        mVideoTotalTime = (TextView) findViewById(R.id.my_video_txv_total_time);
        mBtnExit = (ImageButton) findViewById(R.id.btn_exit);
        mBtnVideoControl = (Button) findViewById(R.id.btn_video_start_pause);
        mBtnVideoNext = (Button) findViewById(R.id.btn_video_next);
        mBtnSwitchScreen = (ImageButton) findViewById(R.id.btn_video_switch_screen);
        mSystemVideoView = (MyVideoView) findViewById(R.id.activity_video_system_video_view);
        mVitamioVideoView = (VideoView) findViewById(R.id.activity_video_vitamio_video_view);
        mControllerBottom = (LinearLayout) findViewById(R.id.my_video_controller_bottom);
        mControllerInfo = (LinearLayout) findViewById(R.id.my_video_controller_info);
        mControllerVolume = (VolumeView) findViewById(R.id.my_video_controller_volume);
        mVideoBuffering = (LinearLayout) findViewById(R.id.my_video_controller_info_buffering);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);


        /**
         * 获取屏幕宽和高
         */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        mSystemMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mSystemCurrentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mControllerVolume.setMaxVolume(mSystemMaxVolume);
        mControllerVolume.setCurrentVolume(mSystemCurrentVolume);
    }

    private void toNextVideo() {
        if (mPlayVideoPosition + 2 > mVideoList.size()) {
            mBtnVideoNext.setVisibility(View.GONE);
            return;
        }
        String uri = mVideoList.get(++mPlayVideoPosition).getVideoPath();
        if (isVitamioVideo) {
            mSystemVideoView.setVideoURI(Uri.parse(uri));
        } else {
            mVitamioVideoView.setVideoPath(uri);
        }
        /**
         * 如果是网络视频，显示加载页面
         */
        isNetUri = Utils.isNetUri(uri);
        if (isNetUri) {
            mBufferingView.setVisibility(View.VISIBLE);
        }
    }

    private void showErrorDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("错误提示")
                .setMessage(error)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mVitamioVideoView != null)
                            mVitamioVideoView.stopPlayback();
                        finish();
                    }
                })
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: " + event.toString());
        mGestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mDownScreenX = event.getX();
            mDownScreenY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (mDownScreenX > (screenWidth / 2) && Math.abs(mDownScreenY - event.getY()) > 20) {
                mHandler.removeMessages(UPDATE_VOLUME_CONTROLLER);
                if (!isShowVolume) {
                    mHandler.sendEmptyMessage(UPDATE_VOLUME_CONTROLLER);
                }
                float distanceY = mDownScreenY - event.getY();
                if (distanceY > 0) {
                    //减小
                    newVolume = (int) (mSystemCurrentVolume + distanceY / screenHeight * mSystemMaxVolume);
                    if (newVolume < 0) newVolume = 0;
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
                    mControllerVolume.setCurrentVolume(newVolume);
                } else if (distanceY < 0) {
                    newVolume = (int) (mSystemCurrentVolume + distanceY / screenHeight * mSystemMaxVolume);
                    if (newVolume > mSystemMaxVolume) newVolume = mSystemMaxVolume;
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
                    mControllerVolume.setCurrentVolume(newVolume);
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isShowVolume) {
                mSystemCurrentVolume = newVolume;
                mHandler.removeMessages(UPDATE_VOLUME_CONTROLLER);
                mHandler.sendEmptyMessageDelayed(UPDATE_VOLUME_CONTROLLER, 2000);
            }
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: " + event.toString());
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (mSystemCurrentVolume >= 1) {
                --mSystemCurrentVolume;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mSystemCurrentVolume, 0);
                mControllerVolume.setCurrentVolume(mSystemCurrentVolume);
            }
            mHandler.removeMessages(UPDATE_VOLUME_CONTROLLER);
            if (!isShowVolume) {
                mHandler.sendEmptyMessage(UPDATE_VOLUME_CONTROLLER);
            }
            mHandler.sendEmptyMessageDelayed(UPDATE_VOLUME_CONTROLLER, 2000);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (mSystemCurrentVolume + 1 <= mSystemMaxVolume) {
                ++mSystemCurrentVolume;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mSystemCurrentVolume, 0);
                mControllerVolume.setCurrentVolume(mSystemCurrentVolume);
            }
            mHandler.removeMessages(UPDATE_VOLUME_CONTROLLER);
            if (!isShowVolume) {
                mHandler.sendEmptyMessage(UPDATE_VOLUME_CONTROLLER);
            }
            mHandler.sendEmptyMessageDelayed(UPDATE_VOLUME_CONTROLLER, 2000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        /**
         * 每次点击都会重新发送一次 更新控制栏消息
         */
        mHandler.removeMessages(UPDATE_VIDEO_CONTROLLER);

        if (v == mBtnExit) {
            finish();
        } else if (v == mBtnVideoControl) {
            if (mSystemVideoView.isPlaying()) {
                mSystemVideoView.pause();
                mBtnVideoControl.setBackgroundResource(R.drawable.btn_video_start);
            } else {
                mSystemVideoView.start();
                mBtnVideoControl.setBackgroundResource(R.drawable.btn_video_pause);
            }
        } else if (v == mBtnVideoNext) {
            toNextVideo();
        } else if (v == mBtnSwitchScreen) {
            if (isLandscape) {
                isLandscape = true;
                /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
                setRequestedOrientation(SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            } else {
                isLandscape = false;
                setRequestedOrientation(SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        }
        mHandler.sendEmptyMessageDelayed(UPDATE_VIDEO_CONTROLLER, 5000);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        /**
         * 旋转屏幕时从新获取
         */
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE)
            isLandscape = true;
        if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT)
            isLandscape = false;
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(mBatteryReceiver);
        mBatteryReceiver = null;
        super.onDestroy();
    }


    private class VideoPlayerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_PROGRESS) {
                /**
                 *循环发送消息，更新视频进度条或者缓存进度条
                 */
                int oldProgress = mVideoSeekBar.getProgress();
                int newProgress;
                if (isVitamioVideo) {
                    newProgress = (int) mVitamioVideoView.getCurrentPosition();
                } else {
                    newProgress = mSystemVideoView.getCurrentPosition();
                }
                int gapProgress = newProgress - oldProgress;//用来进行卡顿判断
                mVideoSeekBar.setProgress(newProgress);

                /**
                 * 如果是网络视频
                 * 设置缓冲进度
                 */
                if (isNetUri) {
                    /**
                     * getBufferPercentage()
                     * 读取视频缓冲进度
                     * 设置第二进度条
                     */
                    int bufferPercent = mSystemVideoView.getBufferPercentage();//0~100
                    int secondProgress = bufferPercent * mVideoSeekBar.getMax() / 100;
                    mVideoSeekBar.setSecondaryProgress(secondProgress);
                    /**
                     * 当视频缓冲不足，卡顿时
                     * isUseInfoBufferingListener判断是否使用系统自己的监听接口（不稳定）
                     */
                    if (!isUseInfoBufferingListener) {
                        if (gapProgress < 500 && gapProgress >= 0 && mSystemVideoView.isPlaying()) {
                            Log.i(TAG, "handleMessage: 视频卡顿");
                            if (mVideoBuffering.getVisibility() != View.VISIBLE) {
                                /**
                                 * 显示缓冲条，发生网速更新消息
                                 */
                                mVideoBuffering.setVisibility(View.VISIBLE);
                                mHandler.sendEmptyMessage(UPDATE_NET_SPEED);
                            }
                        } else {
                            if (mVideoBuffering.getVisibility() != View.GONE) {
                                /**
                                 * 清理网速更新消息，移除缓冲条
                                 */
                                mHandler.removeMessages(UPDATE_NET_SPEED);
                                mVideoBuffering.setVisibility(View.GONE);
                            }
                        }
                    }
                } else {
                    mVideoBuffering.setVisibility(View.GONE);
                    mVideoSeekBar.setSecondaryProgress(0);
                }
                /**
                 * 发送循环消息
                 */
                removeMessages(UPDATE_PROGRESS);
                sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
            } else if (msg.what == UPDATE_TIME) {
                /**
                 * 更新上部控制栏的时间
                 */
                mCalendar = Calendar.getInstance();
                int minute = mCalendar.get(Calendar.MINUTE);
                if (minute < 10)
                    mVideoDuration.setText(mCalendar.get(Calendar.HOUR_OF_DAY) + ":0" + minute);
                else
                    mVideoDuration.setText(mCalendar.get(Calendar.HOUR_OF_DAY) + ":" + mCalendar.get(Calendar.MINUTE));
                /**
                 * 发送消息，时间跟随系统跟新
                 */
                removeMessages(UPDATE_TIME);
                sendEmptyMessageDelayed(UPDATE_TIME, (60 - mCalendar.get(Calendar.SECOND) * 1000));
            } else if (msg.what == UPDATE_VIDEO_CONTROLLER) {
                /**
                 * 主控制栏的 显示/隐藏 处理
                 */
                if (!isShowController) {
                    removeMessages(UPDATE_VIDEO_CONTROLLER);
                    mControllerInfo.setVisibility(View.VISIBLE);
                    mControllerBottom.setVisibility(View.VISIBLE);
                    isShowController = !isShowController;
                    sendEmptyMessageDelayed(UPDATE_VIDEO_CONTROLLER, 5000);
                } else {
                    removeMessages(UPDATE_VIDEO_CONTROLLER);
                    mControllerInfo.setVisibility(View.INVISIBLE);
                    mControllerBottom.setVisibility(View.GONE);
                    isShowController = !isShowController;
                }

            } else if (msg.what == UPDATE_VOLUME_CONTROLLER) {
                /**
                 * 声音图标的 显示/隐藏 处理
                 */
                if (!isShowVolume) {
                    //显示
                    mControllerVolume.setVisibility(View.VISIBLE);
                    isShowVolume = true;
                } else {
                    //隐藏
                    mControllerVolume.setVisibility(View.GONE);
                    isShowVolume = false;
                }
            } else if (msg.what == UPDATE_NET_SPEED) {
                /**
                 * 系统网速的更新
                 */
                Log.i(TAG, "handleMessage: 更新网速");
                mNetSpeedTxt = Utils.getNetSpeed(getApplicationContext());
                mBufferingViewSpeed.setText(mNetSpeedTxt);
                TextView t = (TextView) mVideoBuffering.getChildAt(1);
                t.setText(mNetSpeedTxt);
                removeMessages(UPDATE_NET_SPEED);
                sendEmptyMessageDelayed(UPDATE_NET_SPEED, 2000);
            }
        }
    }

    /**
     * 电量广播接收器
     */
    private class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            if (level <= 0) {
                mCurrentPowe.setImageResource(R.drawable.ic_battery_0);
            } else if (level <= 10) {
                mCurrentPowe.setImageResource(R.drawable.ic_battery_10);
            } else if (level <= 20) {
                mCurrentPowe.setImageResource(R.drawable.ic_battery_20);
            } else if (level <= 40) {
                mCurrentPowe.setImageResource(R.drawable.ic_battery_40);
            } else if (level <= 60) {
                mCurrentPowe.setImageResource(R.drawable.ic_battery_60);
            } else if (level <= 80) {
                mCurrentPowe.setImageResource(R.drawable.ic_battery_80);
            } else if (level <= 100) {
                mCurrentPowe.setImageResource(R.drawable.ic_battery_100);
            } else {
                mCurrentPowe.setImageResource(R.drawable.ic_battery_100);
            }
        }
    }
}
