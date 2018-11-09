package com.svrpublicschool.models;

public class FacultyDetailsEntity {
    int id;
    String name;
    String desg;
    String profilePhoto;
    String fbUrl;

    public FacultyDetailsEntity(int id, String name, String desg, String profilePhoto) {
        this.id = id;
        this.name = name;
        this.desg = desg;
        this.profilePhoto = profilePhoto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesg() {
        return desg;
    }

    public void setDesg(String desg) {
        this.desg = desg;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getFbUrl() {
        return fbUrl;
    }

    public void setFbUrl(String fbUrl) {
        this.fbUrl = fbUrl;
    }
}
