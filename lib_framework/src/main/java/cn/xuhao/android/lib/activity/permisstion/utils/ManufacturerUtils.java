package cn.xuhao.android.lib.activity.permisstion.utils;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ManufacturerUtils {
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    private static final String brand = Build.BRAND.toLowerCase();
    private static final String manufacturer = Build.MANUFACTURER.toLowerCase();

    public static boolean isMotorola() {
        return TextUtils.equals(manufacturer, "motorola")
                || TextUtils.equals(manufacturer, "mot")
                || TextUtils.equals(manufacturer, "fih");
    }

    public static boolean isLG() {
        return TextUtils.equals(manufacturer, "lg");
    }


    /**
     * 检测MIUI
     *
     * @return
     */
    public static boolean isMIUI() {
        return TextUtils.equals(brand, "redmi") || TextUtils.equals(brand, "xiaomi");
    }

    public static boolean isSony() {
        return TextUtils.equals(manufacturer, "sony");
    }

    /**
     * 检测Flyme
     *
     * @return
     */
    public static boolean isFlyme() {
        return TextUtils.equals(brand, "meizu");
    }

    public static boolean isHuawei() {
        return TextUtils.equals(brand, "huawei") || TextUtils.equals(brand, "honor");
    }

    public static boolean isSamsung() {
        return TextUtils.equals(manufacturer, "samsung");
    }

    public static class BuildProperties {
        private final Properties properties;

        private BuildProperties() throws IOException {
            properties = new Properties();
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        }

        public boolean containsKey(final Object key) {
            return properties.containsKey(key);
        }

        public boolean containsValue(final Object value) {
            return properties.containsValue(value);
        }

        public Set<Map.Entry<Object, Object>> entrySet() {
            return properties.entrySet();
        }

        public String getProperty(final String name) {
            return properties.getProperty(name);
        }

        public String getProperty(final String name, final String defaultValue) {
            return properties.getProperty(name, defaultValue);
        }

        public boolean isEmpty() {
            return properties.isEmpty();
        }

        public Enumeration<Object> keys() {
            return properties.keys();
        }

        public Set<Object> keySet() {
            return properties.keySet();
        }

        public int size() {
            return properties.size();
        }

        public Collection<Object> values() {
            return properties.values();
        }

        public static BuildProperties newInstance() throws IOException {
            return new BuildProperties();
        }
    }
}
