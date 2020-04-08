package com.svrpublicschool.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KeyValueModel {

    @SerializedName("KeyValue")
    private List<KeyValueEntity> KeyValue;

    public List<KeyValueEntity> getKeyValue() {
        return KeyValue;
    }

    public void setKeyValue(List<KeyValueEntity> KeyValue) {
        this.KeyValue = KeyValue;
    }

    public static class KeyValueEntity {
        /**
         * key : school_desc_short
         * value : The school has its own best hostel facility for students. Parents/ Guardian desirous to admitting their children in the hostel will have to apply in the hostel application  from separately. The student should be medically fit and should not have any physical deficiency/ shortcoming which may come in the day of his/ her participation in all school activities. A well balanced healthy and nourishing diet is ensured besides the three main meals the child gets MILK in the morning and seasonal FRUITS/ SNACKS in the recess.
         */

        @SerializedName("key")
        private String key;
        @SerializedName("value")
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
