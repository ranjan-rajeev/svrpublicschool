package com.svrpublicschool.models;

import com.google.gson.annotations.SerializedName;

public class BannerEntity {
    /**
     * id : 1
     * url : https://c1.staticflickr.com/5/4851/45746110222_f877bdfa5e_o.jpg
     */

    @SerializedName("id")
    private int id;
    @SerializedName("url")
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}