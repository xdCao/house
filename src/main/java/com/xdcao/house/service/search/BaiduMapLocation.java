package com.xdcao.house.service.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * @Author: buku.ch
 * @Date: 2019-04-17 09:53
 */


public class BaiduMapLocation {

    @SerializedName("lon")
    private double longtitude;

    @SerializedName("lat")
    private double latitude;

    public BaiduMapLocation() {
    }

    public BaiduMapLocation(double longtitude, double latitude) {
        this.longtitude = longtitude;
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
