package com.android.puccmobileplay.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 长春 on 2017/7/6.
 */

public class Forecast {
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt_d")
        public String info;
    }
    public class Temperature{
        public String max;
        public String min;
    }
}
