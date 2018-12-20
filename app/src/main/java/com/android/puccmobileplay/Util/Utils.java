package com.android.puccmobileplay.Util;

import android.content.Context;
import android.net.TrafficStats;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by 长春 on 2017/9/20.
 */

public class Utils {
    private static long lastTotalRxBytes = 0;
    private static long lastTimeStamp = 0;

    /**
     * 毫秒转HH MM SS
     * @param ms
     * @return
     */
    public static String msToTime(long ms) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(ms);
        return hms;
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param context
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
    public static boolean isNetUri(String s){
         String ss= s.toLowerCase();
         if (ss.startsWith("http") ||ss.startsWith("rtsp") ||ss.startsWith("mms")  ){
             return true;
         }
         return false;
     }

    static public String getNetSpeed(Context context) {
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid)==TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB
        long nowTimeStamp = System.currentTimeMillis();
        long speed = 0;//毫秒转换
        try {
            speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lastTimeStamp = nowTimeStamp;
            lastTotalRxBytes = nowTotalRxBytes;
            return String.valueOf(speed)+"kb/s";
        }

    }
}
