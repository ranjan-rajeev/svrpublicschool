package com.svrpublicschool.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity
public class ChatEntity implements Serializable {

    @PrimaryKey
    @NotNull
    public long pkId;
    public String fId;
    public long createdAt;
    public int chatType;
    public String msg;
    public String url;
    public String fileName;
    public String pages;
    public String size;
    // public String thumbUrl;
    public String sender;
    public String receiver; //will also store groupid
    public String senName;
    public int status;

    public void updateImageDetails(String url, String size) {
        this.url = url;
        this.size = size;
    }

    public void updatePdfDetails(String url, String size, String pages) {
        this.url = url;
        this.size = size;
        this.pages = pages;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSenName() {
        return senName;
    }

    public void setSenName(String senName) {
        this.senName = senName;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public long getPkId() {
        return pkId;
    }

    public void setPkId(long pkId) {
        this.pkId = pkId;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

/*    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }*/

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getfId() {
        return fId;
    }

    public void setfId(String fId) {
        this.fId = fId;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @NonNull
    @Override
    public String toString() {
        return " TYPE : " + this.chatType +
                " name : " + this.senName +
                " MSG : " + this.msg +
                " pkId : " + this.pkId +
                " fileName : " + this.fileName +
                " url : " + this.url +
                " size : " + this.size
                ;
    }
}
