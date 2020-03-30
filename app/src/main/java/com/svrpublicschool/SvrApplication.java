package com.svrpublicschool;

import android.app.Application;
import android.content.Context;

public class SvrApplication extends Application {
    private static SvrApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    public static SvrApplication getInstance() {
        return mInstance;
    }
}
