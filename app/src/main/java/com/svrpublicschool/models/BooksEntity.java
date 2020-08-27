package com.svrpublicschool.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;
import com.google.gson.annotations.SerializedName;



import java.io.Serializable;

@Entity
public class BooksEntity implements Serializable {

    /**
     * id : 1
     * class : 10
     * subject : PHYSICS
     * chapter : CH1
     * url : https://drive.google.com/uc?export=download&id=1QSqoaCmrYYdDurytJ-bB-gqA8S8Mk0qg
     */

    @PrimaryKey
    @NotNull
    @SerializedName("id")
    public int id;
    @SerializedName("class")
    public int classX;
    @SerializedName("subject")
    public String subject;
    @SerializedName("chapter")
    public String chapter;
    @SerializedName("url")
    public String url;

    public boolean isDownloaded = false;

    @SerializedName("isCategory")
    private boolean isCategory;
    @SerializedName("hasSubCategory")
    private boolean hasSubCategory;


    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClassX() {
        return classX;
    }

    public void setClassX(int classX) {
        this.classX = classX;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isIsCategory() {
        return isCategory;
    }

    public void setIsCategory(boolean isCategory) {
        this.isCategory = isCategory;
    }

    public boolean isHasSubCategory() {
        return hasSubCategory;
    }

    public void setHasSubCategory(boolean hasSubCategory) {
        this.hasSubCategory = hasSubCategory;
    }
}
