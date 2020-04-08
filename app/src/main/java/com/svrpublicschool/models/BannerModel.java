package com.svrpublicschool.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BannerModel {

    @SerializedName("Banner")
    private List<BannerEntity> Banner;

    public List<BannerEntity> getBanner() {
        return Banner;
    }

    public void setBanner(List<BannerEntity> Banner) {
        this.Banner = Banner;
    }

    public static class BannerEntity {
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
}
