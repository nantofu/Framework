package cn.xuhao.android.lib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebView;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * 手机状态信息相关工具类
 */
public class PhoneInfoUtil {

    private static final String DEFAULT_ENCODE = "UTF-8";

    private static final String SP_FILE_NAME = "phone_info";
    /**
     * 用户网络类型，细分为wifi/2g/3g/4g
     */
    public static final int NETTYPE_WIFI = 1;
    public static final int NETTYPE_2G = 2;
    public static final int NETTYPE_3G = 3;
    public static final int NETTYPE_4G = 4;
    public static final int NETTYPE_UNKNOW = 5;

    /**
     * 隐藏构造
     */
    private PhoneInfoUtil() {

    }

    /**
     * 获取设备MAC地址
     *
     * @return
     */
    @Nullable
    public static String getMacAddress() {
        String macSerial = "";
        String str = "";
        InputStreamReader ir = null;
        LineNumberReader input = null;
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            ir = new InputStreamReader(pp.getInputStream(), DEFAULT_ENCODE);
            input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }

            if (ir != null) {
                try {
                    ir.close();
                } catch (IOException e) {
                }
            }
        }

        return macSerial;
    }

    public static String getHostIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (Exception e) {    // SUPPRESS CHECKSTYLE
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取位移设备UUID
     * 首先获取 imei TelephonyManager.getDeviceId
     * 获取失败，用一个特定算法计算获得
     *
     * @param cxt
     * @return string
     */
    @NonNull
    public static String getDeviceId(@NonNull Context cxt) {
        TelephonyManager tm = (TelephonyManager) cxt.getSystemService(
                Context.TELEPHONY_SERVICE);
        String tmDeviceId = null;
        try {
            tmDeviceId = tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final int per = 10;

        if (tmDeviceId != null && !tmDeviceId.equals("Unknown")
                && !tmDeviceId.equals("000000000000000")
                && !tmDeviceId.equals("0")
                && !tmDeviceId.equals("1")
                && !tmDeviceId.equals("unknown")) {
            return tmDeviceId;
        } else {
            String devIDShort = "35"
                    + (Build.BOARD.length() % per) + (Build.BRAND.length() % per)
                    + (Build.CPU_ABI.length() % per) + (Build.DEVICE.length() % per)
                    + (Build.MANUFACTURER.length() % per)
                    + (Build.MODEL.length() % per)
                    + (Build.PRODUCT.length() % per);

            // Thanks to @Roman SL!
            // http://stackoverflow.com/a/4789483/950427
            // Only devices with API >= 9 have android.os.Build.SERIAL
            // http://developer.android.com/reference/android/os/Build.html#SERIAL
            // If a user upgrades software or roots their phone, there will be a duplicate entry
            String serial = null;
            try {
                serial = android.os.Build.class.getField("SERIAL").toString();
                // go ahead and return the serial for api => 9
                return new UUID(devIDShort.hashCode(), serial.hashCode()).toString();
            } catch (Exception ignored) {        // SUPPRESS CHECKSTYLE
                // String needs to be initialized
                serial = ""; // some value
            }

            // Thanks @Joe!
            // http://stackoverflow.com/a/2853253/950427
            // Finally, combine the values we have found by using the UUID class to create a unique identifier
            return new UUID(devIDShort.hashCode(), serial.hashCode()).toString();
        }

    }


    @NonNull
    public static String getDeviceSerialNumber() {
        try {
            return (String) Build.class.getField("SERIAL").get(null);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String getNetType(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.getTypeName();
        }
        return "";
    }

    public static String getAndroidId(@NonNull Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    /**
     * 获取设备的deviceID，一般情况下为设备的IMEI号
     */
    public static String getRawDeviceId(@NonNull Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 获取设备UA，为webview的默认UA
     */
    public static String getDefaultWebViewUA(Context context) {
        WebView webView = new WebView(context);
        String ua = webView.getSettings().getUserAgentString();
        webView.destroy();
        return ua;
    }

    public static int getOrientation(@NonNull Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return configuration.orientation;
    }

    /**
     * 判断手机是否ROOT,不是绝对准确，su存在时，也可能已经root，也有su存在但是未root的情况
     * <br>作为广告接口的一个是否root的参数，够用了；
     */
    public static boolean isDeviceRoot() {

        boolean root = false;

        try {
            root = !((!new File(Environment.getRootDirectory() + "/bin/su").exists())
                    && (!new File(Environment.getRootDirectory() + "/xbin/su").exists()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }


    public static float getDensity(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取系统时区；CMT +8
     */
    public static String getTimezone() {
        TimeZone timeZone = TimeZone.getDefault();
        return timeZone.getDisplayName(false, TimeZone.SHORT, Locale.ENGLISH);
    }

    /**
     * 获取系统语言；zh，ch。。。
     */
    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取运营商名称。例如：中国移动
     */
    public static String getOperateName(@NonNull Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperatorName();
    }

    /**
     * 获取运营商代号。例如：46000
     */
    public static String getOperateId(@NonNull Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperator();
    }

    /**
     * 获取网络类型，或是网络级别，分为wifi/2g/3g/4g
     */
    public static int getNetWorkClass(@NonNull Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            int netWorkType = networkInfo.getType();
            if (netWorkType == ConnectivityManager.TYPE_WIFI) {
                return NETTYPE_WIFI;
            } else if (netWorkType == ConnectivityManager.TYPE_MOBILE) {

                int rawNetType = telephonyManager.getNetworkType();
                switch (rawNetType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NETTYPE_2G;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NETTYPE_3G;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NETTYPE_4G;
                    default:
                        return NETTYPE_UNKNOW;
                }
            } else {
                return NETTYPE_UNKNOW;
            }
        } else {
            return NETTYPE_UNKNOW;
        }
    }


    /**
     * 获取手机IMEI号（设备串号）
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        String imei = "";
        try {
            SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
            imei = sp.getString("imei", "");
            if (TextUtils.isEmpty(imei)) {
                imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                sp.edit().putString("imei", imei).apply();
            }
        } catch (Exception e) {
            imei = "";
        }
        return imei;
    }

    /**
     * 获取手机IMSI号
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        String imsi = "";
        try {
            SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
            imsi = sp.getString("imsi", "");
            if (TextUtils.isEmpty(imsi)) {
                imsi = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
                sp.edit().putString("imsi", imsi).apply();
            }
        } catch (Exception e) {
            imsi = "";
        }
        return imsi;
    }

    public static String getMac(Context context) {
        String mac;
        try {

            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            mac = info.getMacAddress();
            if (mac == null) {
                mac = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            mac = getMacAddress();
        }
        return mac;
    }

    public static String getLanguage(Context context) {
        String language;
        try {
            Locale locale = context.getResources().getConfiguration().locale;
            language = locale.getLanguage();
        } catch (Exception e) {
            e.printStackTrace();
            language = "zh";
        }

        return language;
    }

    /**
     * 获取手机品牌
     *
     * @return
     */
    public static String getBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取系统版本号
     *
     * @return
     */
    public static String getSysVer() {
        return android.os.Build.VERSION.RELEASE;
    }

}
