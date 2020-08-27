package com.svrpublicschool.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;


@Entity
public class NoticeEntity {

    @PrimaryKey
    @NotNull
    String fid;
    long createdAt;
    String title;
    String desc;
    String url;

    @NotNull
    public String getFid() {
        return fid;
    }

    public void setFid(@NotNull String fid) {
        this.fid = fid;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
