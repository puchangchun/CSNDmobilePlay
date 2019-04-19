package com.android.puccmobileplay.Util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 长春 on 2017/7/5.
 */

public class HttpUtil {
    /**
     * 访问服务器
     * @param url
     * @param callback
     */
    public static void sendOkHttpRequest(String url,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        //开启线程，在回调接口里处理返回的数据
        client.newCall(request).enqueue(callback);
    }
}
