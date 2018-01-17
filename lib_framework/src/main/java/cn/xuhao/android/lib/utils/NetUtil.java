package cn.xuhao.android.lib.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * 检查网络是否可用
 */
public class NetUtil {

    /**
     * debug
     */
    private static final boolean DEBUG = false;

    private NetUtil() {

    }

    /**
     * 网络是否可用。
     *
     * @param context Context
     * @return 连接并可用返回 true
     */
    public static boolean isNetworkConnected(@NonNull Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo != null && networkInfo.isAvailable();
    }

    /**
     * 获取活动的连接。
     *
     * @param context Context
     * @return 当前连接
     */
    private static NetworkInfo getActiveNetworkInfo(@NonNull Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return null;
        }
        return connectivity.getActiveNetworkInfo();
    }


    /**
     * 获取当前的网络状态
     *
     * @param context
     * @return
     */
    public static boolean isWifi(@NonNull Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null) {    //没有网络,属于WIFI情况下。
            return true;
        }

        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            return false;    //2G,3G
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            return true;
        }

        return false;
    }

    /**
     * 获取当前网络状态，3G / wifi
     *
     * @param context
     * @return
     */
    @NonNull
    public static String getNetStatus(@NonNull Context context) {
        String netState = "";
        ConnectivityManager conMan = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // mobile 3G Data Network
        NetworkInfo mobileNetworkInfo = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetworkInfo != null) {
            State mobile = mobileNetworkInfo.getState();
            if (mobile != null && mobile.toString().equals(State.CONNECTED.toString())) { // 显示3G网络连接状态
                netState = "3G";
            }
        }

        // wifi
        NetworkInfo wifiNetworkInfo = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo != null) {
            State wifi = wifiNetworkInfo.getState();
            if (wifi != null && wifi.toString().equals(State.CONNECTED.toString())) {
                netState = "WIFI";
            }
        }

        return netState;
    }


    /**
     * APN（接入点）查询的URI.
     */
    public static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");


    /**
     * 检查当前网络类型。
     *
     * @param context context
     * @return wifi, cmnet, uninet, ctnet, cmwap ……
     */
    @Nullable
    public static String getNetType(@NonNull Context context) {
        String netType = null;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {

            if (DEBUG) {
                L.d("network type : " + activeNetInfo.getTypeName().toLowerCase());
            }
            netType = activeNetInfo.getTypeName() + "-" + activeNetInfo.getSubtypeName();
        }
        return netType;
    }


}
