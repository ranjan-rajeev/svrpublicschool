package com.svrpublicschool.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FacultyResponse {

    @SerializedName("faculty")
    private List<FacultyDetailsEntity> faculty;

    public List<FacultyDetailsEntity> getFaculty() {
        return faculty;
    }

    public void setFaculty(List<FacultyDetailsEntity> faculty) {
        this.faculty = faculty;
    }
}
