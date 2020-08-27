package com.svrpublicschool.models;

import com.google.gson.annotations.SerializedName;

public class YoutubeMetaDataEntity {

    /**
     * author_url : https://www.youtube.com/channel/UCeBNXDuC5w3m382n4n_Jyow
     * thumbnail_width : 480
     * title : Number system || Maths || Class 6
     * author_name : Study Smart
     * width : 459
     * html : <iframe width="459" height="344" src="https://www.youtube.com/embed/oH2KeZN85Gs?feature=oembed" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
     * provider_name : YouTube
     * version : 1.0
     * type : video
     * thumbnail_height : 360
     * provider_url : https://www.youtube.com/
     * height : 344
     * thumbnail_url : https://i.ytimg.com/vi/oH2KeZN85Gs/hqdefault.jpg
     */

    @SerializedName("author_url")
    private String authorUrl;
    @SerializedName("thumbnail_width")
    private int thumbnailWidth;
    @SerializedName("title")
    private String title;
    @SerializedName("author_name")
    private String authorName;
    @SerializedName("width")
    private int width;
    @SerializedName("html")
    private String html;
    @SerializedName("provider_name")
    private String providerName;
    @SerializedName("version")
    private String version;
    @SerializedName("type")
    private String type;
    @SerializedName("thumbnail_height")
    private int thumbnailHeight;
    @SerializedName("provider_url")
    private String providerUrl;
    @SerializedName("height")
    private int height;
    @SerializedName("thumbnail_url")
    private String thumbnailUrl;

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailWidth(int thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setThumbnailHeight(int thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public String getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
