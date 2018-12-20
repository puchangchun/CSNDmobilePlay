package com.android.puccmobileplay.Util;

import java.io.File;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

/**
 * 扫描文件的工具
 *
 */

public abstract class MediaScanner {

    private static final String TAG = MediaScanner.class.getSimpleName();
    private int scanTimes = 0;
    private MediaScannerConnection mConn = null;
    private SannerClient mClient = null;
    private File mFile = null;
    private String mMimeType = null;

    public MediaScanner(Context context) {
        if (mClient == null) {
            mClient = new SannerClient();
        }
        if (mConn == null) {
            mConn = new MediaScannerConnection(context, mClient);
        }
    }

    class SannerClient implements MediaScannerConnection.MediaScannerConnectionClient {

        @Override
        public void onMediaScannerConnected() {
            //会在主线程中开启服务，
            Log.e(TAG, "onMediaScannerConnected: +++++++++++");

            if (mFile == null) {
                return;
            }
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    scan(mFile, mMimeType);
                }
            }.start();

        }

        @Override
        public void onScanCompleted(String s, Uri uri) {
            //会开启线程来回调这个函数
            Log.e(TAG, "onScanCompleted: "+666666666);
/*                if (scanTimes == 0){
                    Log.e(TAG, "onScanCompleted: ");
                    getDataCompleted();
                    mConn.disconnect();
                }*/
        }



        private void scan(File file, String type) {


            Log.e(TAG, "scan " + file.getAbsolutePath());
            Log.e(TAG, "scan: "+Thread.currentThread().getName() );
            if (file.isFile()) {
                //调用服务的方法扫描
                mConn.scanFile(file.getAbsolutePath(), null);
                return;
            }
            ////listFiles是获取该目录下所有文件和目录的绝对路径
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                getDataCompleted();
                return;
            }
            scanTimes += files.length;
            for (File f : files) {
                scan(f, type);
                scanTimes--;
            }
            //直接在这做处理，把服务制空
            if (scanTimes == 0){
                Log.e(TAG, "onScanCompleted: ");
                getDataCompleted();
                mConn.disconnect();
            }
        }
    }
    abstract public void getDataCompleted();

    public void scanFile(File file, String mimeType) {
        mFile = file;
        mMimeType = mimeType;
        //调用onMediaScannerConnected
        mConn.connect();
    }

}