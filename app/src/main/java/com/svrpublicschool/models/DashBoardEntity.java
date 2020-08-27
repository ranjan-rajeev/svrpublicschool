package com.svrpublicschool.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity
public class DashBoardEntity implements Serializable {
    @PrimaryKey
    @NotNull
    public int id;

    @SerializedName("type")
    public String type;

    @SerializedName("title")
    private String title;
    @SerializedName("url")
    private String url;
    @SerializedName("redurl")
    private String redurl;
    /**
     * userType : STUDENT
     * isActive : true
     */

    @SerializedName("userType")
    private String userType;
    @SerializedName("isActive")
    private boolean isActive;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRedurl() {
        return redurl;
    }

    public void setRedurl(String redurl) {
        this.redurl = redurl;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
