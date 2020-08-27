package com.svrpublicschool.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DashBoardResponse {

    @SerializedName("dashboard")
    private List<DashBoardEntity> dashboard;

    public List<DashBoardEntity> getDashboard() {
        return dashboard;
    }

    public void setDashboard(List<DashBoardEntity> dashboard) {
        this.dashboard = dashboard;
    }
}
