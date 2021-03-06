package cn.xuhao.android.lib.utils;

import android.util.Log;


/**
 * 打印日志帮助类
 * Created by xuhao on 2017/4/3.
 */
public class L {

    private static class Debug{
        public static boolean isDebug = true;
    }
    public static final String DEFAULT_TAG = "SQYC_TAG";

    public static void i(String tag, String msg) {
        if (Debug.isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void i(String msg) {
        i(DEFAULT_TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (Debug.isDebug) {
            Log.v(tag, msg);
        }
    }

    public static void v(String msg) {
        v(DEFAULT_TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (Debug.isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        d(DEFAULT_TAG, msg);
    }

    public static void w(String tag, String msg) {
        if (Debug.isDebug) {
            Log.w(tag, msg);
        }
    }

    public static void w(String msg) {
        w(DEFAULT_TAG, msg);
    }

    public static void e(String tag, String msg) {
        if (Debug.isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        if (Debug.isDebug) {
            Log.e(tag, msg, e);
        }
    }

    public static void e(String msg) {
        e(DEFAULT_TAG, msg);
    }
}
