package com.android.puccmobileplay.model.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 长春 on 2017/7/5.
 */

public class City extends DataSupport {
    private int id;
    private String cityName;
    private int provinceId;
    private int cityCode;

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {

        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityName() {

        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
