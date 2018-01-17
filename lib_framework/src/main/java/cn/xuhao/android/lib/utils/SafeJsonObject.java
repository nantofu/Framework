package cn.xuhao.android.lib.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author yexiuliang
 * @page 更安全
 * @data 2016/12/6.
 */
public class SafeJsonObject extends JSONObject {

    private boolean isConvertNULL = true; // 是否将处理null导致的字符串

    public SafeJsonObject(String jsonString) throws JSONException {
        super(jsonString);
    }

    public void setConvertNULL(boolean convertNULL) {
        isConvertNULL = convertNULL;
    }

    @Override
    public String optString(String name) {
        return resetString(super.optString(name), "");
    }

    @Override
    public String optString(String name, String fallback) {
        return resetString(super.optString(name, fallback), "");
    }

    private String resetString(String origin, String defaultString) {
        if (isConvertNULL && "null".equalsIgnoreCase(origin)) {
            return defaultString;
        }
        return origin;
    }

    @Override
    public String getString(String name) throws JSONException {
        return resetString(super.getString(name), null);
    }
}
