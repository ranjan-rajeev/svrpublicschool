package com.svrpublicschool.models;

public class FacilityDetailsEntity {
    boolean DescVisible;
    String title;
    String desc;
    String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDescVisible() {
        return DescVisible;
    }

    public void setDescVisible(boolean descVisible) {
        DescVisible = descVisible;
    }

    public FacilityDetailsEntity(String title, String desc) {
        this.title = title;
        this.desc = desc;
        this.DescVisible = false;
        this.url = "https://c1.staticflickr.com/5/4851/45746110222_f877bdfa5e_o.jpg";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }
}
