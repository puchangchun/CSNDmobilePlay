package com.android.puccmobileplay.gson;

/**
 * Created by 长春 on 2017/7/6.
 */

public class AQI {
    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
