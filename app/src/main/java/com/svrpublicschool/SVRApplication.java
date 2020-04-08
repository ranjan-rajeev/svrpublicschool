package com.svrpublicschool;

import android.app.Application;
import android.content.Context;

public class SVRApplication extends Application {
    private static SVRApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    public static SVRApplication getInstance() {
        return mInstance;
    }
}
