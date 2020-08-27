package com.svrpublicschool.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DatabaseVesionModel {

    @SerializedName("dbversion")
    private List<DatabaseVersion> dbversion;

    public List<DatabaseVersion> getDbversion() {
        return dbversion;
    }

    public void setDbversion(List<DatabaseVersion> dbversion) {
        this.dbversion = dbversion;
    }
}
