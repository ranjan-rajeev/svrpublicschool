package com.svrpublicschool.util;

import android.util.Log;

import com.svrpublicschool.BuildConfig;


public final class Logger {

    public static void d(String tag, String msg) {
        if (BuildConfig.IS_IN_DEBUG_MODE && null != msg)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.IS_IN_DEBUG_MODE && null != msg)
            Log.e(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.IS_IN_DEBUG_MODE && null != msg)
            Log.i(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.IS_IN_DEBUG_MODE && null != msg)
            Log.v(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.IS_IN_DEBUG_MODE && null != msg)
            Log.w(tag, msg);
    }


    public static void d(String tag, String msg, Throwable tr) {
        if (BuildConfig.IS_IN_DEBUG_MODE)
            Log.d(tag, null == msg ? "" : msg, tr);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (BuildConfig.IS_IN_DEBUG_MODE)
            Log.e(tag, null == msg ? "" : msg, tr);
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (BuildConfig.IS_IN_DEBUG_MODE)
            Log.i(tag, null == msg ? "" : msg, tr);
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (BuildConfig.IS_IN_DEBUG_MODE)
            Log.v(tag, null == msg ? "" : msg, tr);
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (BuildConfig.IS_IN_DEBUG_MODE)
            Log.w(tag, null == msg ? "" : msg, tr);
    }

    public static boolean isDebugable() {
        return BuildConfig.IS_IN_DEBUG_MODE;
    }
}