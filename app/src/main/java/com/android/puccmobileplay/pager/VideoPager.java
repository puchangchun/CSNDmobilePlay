package com.android.puccmobileplay.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.puccmobileplay.BuildConfig;
import com.android.puccmobileplay.R;
import com.android.puccmobileplay.base.BasePager;
import com.android.puccmobileplay.bean.VideoInfo;
import com.android.puccmobileplay.mod.ModLab;
import com.android.puccmobileplay.activity.VideoPlayer;

import java.io.File;
import java.util.List;

import com.android.puccmobileplay.Util.MediaScanner;
import com.android.puccmobileplay.Util.Utils;
import com.bumptech.glide.Glide;


import static android.content.ContentValues.TAG;

/**
 * Created by 长春 on 2017/9/15.
 */

public class VideoPager extends BasePager {
    private static final int GET_DATA_COMPLETE = 1;
    private static final int GET_DATA_NULL = 0;
    private static final int SCAN_DATA_COMPLETE = 2;
    private static final int GET_DATA_START = 3;
    private static final int ITEM_LONG_PRESS = 4;
    private View mView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<VideoInfo> mList;
    private Handler mHandler;
    private VideoAdapter mVideoAdapter;


    public VideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.fragment_video_pager, null);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.fragment_video_swipe_refresh);
        mHandler = new VideoPagerHandle();

        return mView;
    }

    @Override
    public void initDate() {
        super.initDate();
        /**
         * 下拉刷新的监听
         */
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendMsg(SCAN_DATA_COMPLETE,2000);
            }
        });
        /**
         * 设置Recycler配置
         */
        mList = ModLab.get(mContext).getLocalVideoList();
        mVideoAdapter= new VideoAdapter(mList);

        sendMsg(GET_DATA_START);
    }



    /**
     * 扫描文件，更新系统媒体库
     * @param path 文件夹地址，或者文件地址
     */
    public void scanMedia(String path,String mimeType){
        new MediaScanner(mContext) {
            @Override
            public void getDataCompleted() {
                sendMsg(SCAN_DATA_COMPLETE);
            }
        }.scanFile(new File(path),mimeType);
    }

    /**
     * 开启线程加载手机媒体库数据
     */
    private void getDataFromLocal() {
        ModLab.get(mContext).getExecutor().execute(()->{
            Log.e(TAG, "run: "+Thread.currentThread().getName() );
            //访问内容提供者获取媒体数据
            ContentResolver contentResolver = mContext.getContentResolver();
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            String[] thumbColumns = new String[]{
                            MediaStore.Video.Thumbnails.DATA,
                            MediaStore.Video.Thumbnails.VIDEO_ID
                    };
            String[] objs = {
                    //视频在外部存储器的名字
                    MediaStore.Video.Media.DISPLAY_NAME,
                    //时长
                    MediaStore.Video.Media.DURATION,
                    //文件大小
                    MediaStore.Video.Media.SIZE,
                    //视频的绝对地址
                    MediaStore.Video.Media.DATA,
                    //艺术家
                    MediaStore.Video.Media.ARTIST,
                    //视频的唯一ID
                    MediaStore.Video.Media._ID

            };
            Cursor cursor = contentResolver.query(uri, objs, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                mList.clear();
                while (cursor.moveToNext()) {
                    VideoInfo videoInfo = new VideoInfo();
                    mList.add(videoInfo);

                    videoInfo.setVideoName(cursor.getString(0));
                    videoInfo.setVideoDuration(cursor.getLong(1));
                    videoInfo.setVideoSize(cursor.getLong(2));
                    videoInfo.setVideoPath(cursor.getString(3));
                    videoInfo.setVideoArtist(cursor.getString(4));
/*                    int id = cursor.getInt(5);
                    String selection = MediaStore.Video.Thumbnails.VIDEO_ID +"=?";
                    String[] selectionArgs = new String[]{
                            id+""
                    };
                    Cursor thumbCursor=contentResolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,thumbColumns, selection, selectionArgs, null);
                            if(thumbCursor.moveToFirst()){
                                videoInfo.setThumbPath(thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                            }
                    thumbCursor.close();*/

                }

                cursor.close();
            }
            //通知主线程数据情况
            if (mList != null && mList.size() > 0) {
                sendMsg(GET_DATA_COMPLETE);
            }else {
                sendMsg(GET_DATA_NULL);
            }

        }
        );

    }

    public void sendMsg(int i) {
        mHandler.sendEmptyMessage(i);
    }
    public void sendMsg(int i,int time) {
        mHandler.sendEmptyMessageDelayed(i,time);
    }


    private class VideoPagerHandle extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SCAN_DATA_COMPLETE) {
                //扫描结束
                sendMsg(GET_DATA_START);
            }else if (msg.what == GET_DATA_START){
                // 开启线程，获取数据
                getDataFromLocal();
                //mVideoAdapter.notifyDataSetChanged();
            }else if (msg.what == GET_DATA_COMPLETE){
                //获取数据完成
                mView.findViewById(R.id.fragment_video_pager_progress_bar)
                        .setVisibility(View.GONE);
                mRecyclerView = (RecyclerView) mView.findViewById(R.id.fragment_video_pager_recycler);
                mRecyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
                //如果Adapter存在，会先移除
                mRecyclerView.setAdapter(mVideoAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    /**
     * 该页面的RecyclerView视图
     */
    private class VideoHolder extends RecyclerView.ViewHolder implements  View.OnTouchListener, View.OnClickListener {
        ImageView mVedioPic;
        TextView mVedioName;
        TextView mVedioDuration;
        TextView mVedioSize;
        String mVedioPath;
        GestureDetector mGestureDetector;
        int mPosition;

        public VideoHolder(View itemView) {
            super(itemView);
            mVedioDuration = (TextView) itemView.findViewById(R.id.list_video_duration);
            mVedioName = (TextView) itemView.findViewById(R.id.list_video_name);
            mVedioSize = (TextView) itemView.findViewById(R.id.list_video_size);
            mVedioPic = (ImageView) itemView.findViewById(R.id.list_video_pic);
            itemView.setOnTouchListener(this);
            itemView.setOnClickListener(this);
        }

        public void bindView(VideoInfo vedioInfo, int position) {
            mVedioSize.setText(Formatter.formatFileSize(mContext,vedioInfo.getVideoSize()));
            mVedioName.setText(vedioInfo.getVideoName());
            mVedioDuration.setText(Utils.msToTime(vedioInfo.getVideoDuration()));
            mVedioPath = vedioInfo.getVideoPath();
           // Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(vedioInfo.getVideoPath(), MediaStore.Images.Thumbnails.MICRO_KIND);
            Glide.with(mContext).load(vedioInfo.getVideoPath()).centerCrop().into(mVedioPic);
            mPosition= position;
            mGestureDetector = new GestureDetector(mContext,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public void onLongPress(MotionEvent e) {
                    Log.i(TAG, "onLongPress: ");
                    //1 调用播放视频工具
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    //sdk 24, 不能用file 要用content

                    Uri uri = FileProvider.getUriForFile(mContext,
                            BuildConfig.APPLICATION_ID + ".provider",
                            new File(mVedioPath));
                    intent.setDataAndType(uri,"video/*");
                    mContext.startActivity(intent);
                }

            });
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.i(TAG, "onTouch: ");
            mGestureDetector.onTouchEvent(event);
            return false;
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: ");
            mContext.startActivity(VideoPlayer.newIntent(mContext,mPosition));
        }
    }

    private class VideoAdapter extends RecyclerView.Adapter<VideoHolder> {
        private List<VideoInfo> mInfoList;

        public VideoAdapter(List<VideoInfo> infoList) {
            mInfoList = infoList;
        }

        @Override
        public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_video_pager, parent, false);
            return new VideoHolder(v);
        }

        @Override
        public void onBindViewHolder(VideoHolder holder, int position) {
            holder.bindView(mList.get(position),position);

        }

        @Override
        public int getItemCount() {
            return mInfoList.size();
        }
    }
}
