package com.android.puccmobileplay.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 99653 on 2017/10/19.
 */

public class CacheUtils {
    public static void putString(Context context, String key, String value) {
        SharedPreferences s = context.getSharedPreferences(CacheUtils.class.getName(), Context.MODE_PRIVATE);
        s.edit().putString(key, value).commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences s = context.getSharedPreferences(CacheUtils.class.getName(), Context.MODE_PRIVATE);
        return s.getString(key,"");
    }
}
