package com.svrpublicschool.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TestModel {

    @SerializedName("Sheet1")
    private List<Sheet1Model> Sheet1;

    public List<Sheet1Model> getSheet1() {
        return Sheet1;
    }

    public void setSheet1(List<Sheet1Model> Sheet1) {
        this.Sheet1 = Sheet1;
    }

    public static class Sheet1Model {
        /**
         * name : rajeev
         * country : pakistan
         */

        @SerializedName("name")
        private String name;
        @SerializedName("country")
        private String country;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
}
