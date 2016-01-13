package com.getui.logful.util;

import android.util.Log;

import com.getui.logful.LoggerFactory;

public class LogUtil {

    public static void d(String tag, String msg) {
        if (LoggerFactory.isDebug()) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (LoggerFactory.isDebug()) {
            Log.d(tag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (LoggerFactory.isDebug()) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (LoggerFactory.isDebug()) {
            Log.e(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (LoggerFactory.isDebug()) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (LoggerFactory.isDebug()) {
            Log.i(tag, msg, tr);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (LoggerFactory.isDebug()) {
            Log.v(tag, msg, tr);
        }
    }

    public static void v(String tag, String msg) {
        if (LoggerFactory.isDebug()) {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, Throwable tr) {
        if (LoggerFactory.isDebug()) {
            Log.w(tag, tr);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (LoggerFactory.isDebug()) {
            Log.w(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (LoggerFactory.isDebug()) {
            Log.w(tag, msg);
        }
    }

    public static void wtf(String tag, Throwable tr) {
        if (LoggerFactory.isDebug()) {
            Log.wtf(tag, tr);
        }
    }

    public static void wtf(String tag, String msg, Throwable tr) {
        if (LoggerFactory.isDebug()) {
            Log.wtf(tag, msg, tr);
        }
    }

    public static void wtf(String tag, String msg) {
        if (LoggerFactory.isDebug()) {
            Log.wtf(tag, msg);
        }
    }
}
