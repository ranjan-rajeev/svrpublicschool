package com.svrpublicschool.models;

import java.io.Serializable;
import java.util.List;

public class GroupDetailsEntity implements Serializable {
    String gpName;
    String fid;
    long createdAt;
    String gpIcon;
    List<UserEntity> loginEntities;

    public String getGpName() {
        return gpName;
    }

    public void setGpName(String gpName) {
        this.gpName = gpName;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getGpIcon() {
        return gpIcon;
    }

    public void setGpIcon(String gpIcon) {
        this.gpIcon = gpIcon;
    }

    public List<UserEntity> getLoginEntities() {
        return loginEntities;
    }

    public void setLoginEntities(List<UserEntity> loginEntities) {
        this.loginEntities = loginEntities;
    }
}
