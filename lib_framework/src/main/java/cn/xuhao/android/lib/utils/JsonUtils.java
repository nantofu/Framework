package cn.xuhao.android.lib.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yexiuliang
 * @page
 * @data 2016/12/6.
 */
public class JsonUtils {
    private static final Gson gson = new Gson();
    private static final JsonParser PARSER = new JsonParser();

    public static boolean isEmptyJson(String json) {
        if (json == null) {
            return true;
        }
        json = json.trim();
        return "".equals(json) || "null".equalsIgnoreCase(json) || "{}".equalsIgnoreCase(json) || "[]".equals(json);
    }

    public static <Bean> ArrayList<Bean> parseArray(String jsonArrayString, ArrayParser<Bean> arrayParser) throws JSONException {
        if (isEmptyJson(jsonArrayString)) {
            return null;
        }

        JSONArray jsonArray = new JSONArray(jsonArrayString);
        if (jsonArray.length() == 0) {
            return null;
        }

        ArrayList<Bean> commentList = new ArrayList<Bean>(jsonArray.length());
        String json;
        for (int i = 0, size = jsonArray.length(); i < size; i++) {
            json = jsonArray.optString(i);
            if (!JsonUtils.isEmptyJson(json)) {
                Bean bean = arrayParser.parse(json);
                if (bean != null) {
                    commentList.add(bean);
                }
            }
        }
        return commentList;
    }

    public static <Bean> ArrayList<Bean> parseArray(JSONArray jsonArray, ArrayItemParser<Bean> arrayParser) throws JSONException {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        ArrayList<Bean> commentList = new ArrayList<Bean>(jsonArray.length());
        JSONObject jsonObject;
        for (int i = 0, size = jsonArray.length(); i < size; i++) {
            jsonObject = jsonArray.optJSONObject(i);
            if (jsonObject != null) {
                Bean bean = arrayParser.parse(jsonObject);
                if (bean != null) {
                    commentList.add(bean);
                }
            }
        }
        return commentList;
    }

    public static <Bean> ArrayList<Bean> parseArray(String jsonArrayString, ArrayItemParser<Bean> arrayParser) throws JSONException {
        if (isEmptyJson(jsonArrayString)) {
            return null;
        }

        return parseArray(new JSONArray(jsonArrayString), arrayParser);
    }

    public static <Bean> Bean parse(String jsonString, Class<Bean> beanClass, Parser<Bean> parser) throws JSONException {
        if (isEmptyJson(jsonString)) {
            return null;
        }

        Bean bean;
        try {
            bean = beanClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        JSONObject jsonObject = new SafeJsonObject(jsonString);
        parser.parse(bean, jsonObject);

        return bean;
    }

    public static <Bean> Bean parse(JSONObject jsonObject, Class<Bean> beanClass, Parser<Bean> parser) throws JSONException {
        if (jsonObject == null) {
            return null;
        }

        Bean bean;
        try {
            bean = beanClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        parser.parse(bean, jsonObject);

        return bean;
    }

    @SuppressWarnings("unused")
    public static int[] parseIntArray(String jsonString) throws JSONException {
        if (isEmptyJson(jsonString)) {
            return null;
        }

        JSONArray jsonArray = new JSONArray(jsonString);
        if (jsonArray.length() == 0) {
            return null;
        }

        int[] dataInts = new int[jsonArray.length()];
        for (int i = 0, size = jsonArray.length(); i < size; i++) {
            dataInts[i] = jsonArray.getInt(i);
        }

        return dataInts;
    }

    @SuppressWarnings("unused")
    public static String[] parseStringArray(String jsonString) throws JSONException {
        if (isEmptyJson(jsonString)) {
            return null;
        }

        JSONArray jsonArray = new JSONArray(jsonString);
        if (jsonArray.length() == 0) {
            return null;
        }

        String[] dataStrings = new String[jsonArray.length()];
        for (int i = 0, size = jsonArray.length(); i < size; i++) {
            dataStrings[i] = jsonArray.getString(i);
        }

        return dataStrings;
    }

    @SuppressWarnings("unused")
    public static ArrayList<String> parseStringList(String jsonString) throws JSONException {
        if (isEmptyJson(jsonString)) {
            return null;
        }

        JSONArray jsonArray = new JSONArray(jsonString);
        if (jsonArray.length() == 0) {
            return null;
        }

        ArrayList<String> dataList = new ArrayList<String>(jsonArray.length());
        for (int i = 0, size = jsonArray.length(); i < size; i++) {
            dataList.add(jsonArray.getString(i));
        }

        return dataList;
    }

    public static String toJson(Object src) {
        return gson.toJson(src);
    }



    public static String toJson(Object src, Type c) {
        return gson.toJson(src, c);
    }

    /**
     * Json字串转换成JsonObject
     * @param str
     * @return
     */
    public static JsonObject stringToJson(String str) {
        try {
            return PARSER.parse(str).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * object转换成JsonObject
     * @param obj
     * @return
     */
    public static JsonObject objectToJson(Object obj) {
        try {
            return stringToJson(toJson(obj));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * object转换成JsonObject
     * @param obj
     * @param type 类型，如果obj是普通对象，则直接传obj.getClass()（或者XXX.class）即可；
     *             如果是泛型，一般需要配合TypeToken，
     *             比如ArrayList&lt;String&gt;，则需要传new TypeToken&lt;ArrayList&lt;String&gt;&gt; { }.getType()
     * @return
     */
    public static JsonObject objectToJson(Object obj, @NonNull Type type) {
        try {
            return stringToJson(toJson(obj, type));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * JsonArray字串转换成JsonArray
     * @param str
     * @return
     */
    public static JsonArray stringToJsonArray(String str) {
        try {
            return PARSER.parse(str).getAsJsonArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> c) {
        try {
            return gson.fromJson(json, c);
        } catch (JsonSyntaxException ex) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static <T> T fromJson(JsonElement src, Class<T> c) {
        try {
            return gson.fromJson(src, c);
        } catch (JsonSyntaxException ex) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static List fromJson(String src, Type c) {
        try {
            return gson.fromJson(src, c);
        } catch (JsonSyntaxException ex) {
            return null;
        }
    }

    public static JSONArray toJsonArray(int[] ints) {
        if (ints == null) {
            return null;
        }

        JSONArray jsonArray = new JSONArray();
        for (int item : ints) {
            jsonArray.put(item);
        }
        return jsonArray;
    }

    public interface ArrayParser<T> {
        T parse(String json) throws JSONException;
    }

    public interface ArrayItemParser<T> {
        T parse(JSONObject jsonObject) throws JSONException;
    }

    public interface Parser<T> {
        void parse(T t, JSONObject jsonObject) throws JSONException;
    }

    /**
     * Json字串转换成普通类对象
     * @param jsonStr
     * @param objCls
     * @param <T>
     * @return
     */
    public static <T> T jsonStringToObject(String jsonStr, @NonNull Class<T> objCls) {
        try {
            return gson.fromJson(jsonStr, objCls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Json转换成普通类对象
     * @param json
     * @param objCls
     * @param <T>
     * @return
     */
    public static <T> T jsonToObject(JsonElement json, @NonNull Class<T> objCls) {
        try {
            return gson.fromJson(json, objCls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Json转换成泛型类对象（如泛型List、Array等）
     * @param json JsonElement
     * @param type 泛型类对象对应的type
     * @return 泛型类对象（如泛型List、Array等）
     */
    public static <T> T jsonToObject(JsonElement json, Type type) {
        try {
            return gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * JsonArray字串转换成List对象
     * @param jsonArrayStr
     * @param elementCls
     * @param <T>
     * @return
     */
    @NonNull
    public static <T> List<T> jsonArrayStringToList(String jsonArrayStr, @NonNull Class<T> elementCls) {
        List<T> result = new ArrayList<T>();
        JsonArray array = stringToJsonArray(jsonArrayStr);
        if (array == null) {
            return result;
        }

        try {
            for (JsonElement element : array) {
                result.add(gson.fromJson(element, elementCls));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * JsonArray字串转换成List对象
     * @param jsonArrayStr
     * @return
     */
    @NonNull
    public static List<String> jsonArrayStringToList(String jsonArrayStr) {
        List<String> result = new ArrayList<String>();
        JsonArray array = stringToJsonArray(jsonArrayStr);
        if (array == null) {
            return result;
        }

        try {
            for (JsonElement element : array) {
                result.add(gson.fromJson(element, String.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * JsonArray字串转换成List对象
     * @param jsonArrayStr
     * @param type
     * @param <T>
     * @return
     */
    @NonNull
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> List<T> jsonArrayStringToList(String jsonArrayStr, Type type) {
        List result = new ArrayList();
        JsonArray array = stringToJsonArray(jsonArrayStr);
        if (array == null) {
            return result;
        }

        try {
            for (JsonElement element : array) {
                result.add(gson.fromJson(element, type));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 在接口返回数据时，字段类型不匹配时，Gson解析报错的信息是比较有规律的，用正则匹配找出类型不匹配的字段，可以及时发现问题
     */
    public static String jsonErrorHandle(String jsonStr, Exception e) {
        String a = e.getMessage();
        String regex = "java.lang.IllegalStateException: Expected (a )*(\\w+) but was (\\w+)(?: at line (\\d+) column (\\d+))*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(a);
        JsonTypeError jsonTypeError = new JsonTypeError();
        jsonTypeError.rawString = a;

        if (matcher.find()) {
            String[] errorKeyWords = new String[matcher.groupCount()];
            for (int i = 0; i < matcher.groupCount(); i++) {
                errorKeyWords[i] = matcher.group(i + 1);
            }

            jsonTypeError.expectedType = errorKeyWords[1];
            jsonTypeError.actualType = errorKeyWords[2];
            if (matcher.groupCount() > 4) { // SUPPRESS CHECKSTYLE
                String posString = errorKeyWords[4]; // SUPPRESS CHECKSTYLE
                try {
                    int position = Integer.parseInt(posString);
                    jsonTypeError.position = position;
                    String subError = jsonStr.substring(0, position);
                    String keyRegex = "\"(\\w+)\":";
                    Pattern keyPattern = Pattern.compile(keyRegex);
                    Matcher keyMatcher = keyPattern.matcher(subError);
                    while (keyMatcher.find()) {
                        jsonTypeError.keyName = keyMatcher.group(keyMatcher.groupCount());
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }

        if (TextUtils.isEmpty(jsonTypeError.expectedType) || TextUtils.isEmpty(jsonTypeError.actualType)) {
            return jsonTypeError.rawString;
        } else {
            return String.format("字段 %1$s 预期类型为 %2$s 实际返回类型是 %3$s", !TextUtils.isEmpty(jsonTypeError.keyName) ? jsonTypeError.keyName : "unknown",
                    jsonTypeError.expectedType, jsonTypeError.actualType);
        }

    }
    public static class JsonTypeError {
        public int position;
        public String expectedType;
        public String actualType;
        public String keyName;
        public String rawString = "";

    }


}
