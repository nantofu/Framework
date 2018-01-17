package cn.xuhao.android.lib.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * 字符串转义字符工具类
 * Created by xuhao on 2017/6/12.
 */

public class StringEscape {

    /**
     * 去除转义字符
     *
     * @param str 含有转义字符的字符串
     * @return 不含有转移字符的字符串
     */
    public static String unescapeJava(String str) {
        String result = "";
        if (str == null) {
            return result;
        }
        try {
            StringWriter out = new StringWriter(str.length());
            while (isHasEscape(str)) {
                parseSlash(out, str);
                str = out.toString();
                out = new StringWriter(str.length());
            }
            str = str.replaceAll("\"\\{", "{");
            str = str.replaceAll("\"\\[", "[");
            str = str.replaceAll("\\}\"", "}");
            str = str.replaceAll("\\]\"", "]");

            result = str;
        } catch (Exception var2) {
            var2.printStackTrace();
        }
        return result;
    }

    private static void parseSlash(Writer out, String str) throws IOException {
        StringBuffer unicode = new StringBuffer(4);
        boolean hadSlash = false;
        boolean inUnicode = false;

        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (inUnicode) {
                unicode.append(ch);
                if (unicode.length() == 4) {
                    try {
                        int nfe = Integer.parseInt(unicode.toString(), 16);
                        out.write((char) nfe);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException var9) {
                        throw new RuntimeException("Unable to parse unicode value: " + unicode, var9);
                    }
                }
            } else if (hadSlash) {
                hadSlash = false;
                switch (ch) {
                    case '\"':
                        out.write(34);
                        break;
                    case '\'':
                        out.write(39);
                        break;
                    case '\\':
                        out.write(92);
                        break;
                    case 'b':
                        out.write(8);
                        break;
                    case 'f':
                        out.write(12);
                        break;
                    case 'n':
                        out.write(10);
                        break;
                    case 'r':
                        out.write(13);
                        break;
                    case 't':
                        out.write(9);
                        break;
                    case 'u':
                        inUnicode = true;
                        break;
                    default:
                        out.write(ch);
                }
            } else if (ch == 92) {
                hadSlash = true;
            } else {
                out.write(ch);
            }
        }

        if (hadSlash) {
            out.write(92);
        }
    }

    private static boolean isHasEscape(String str) {
        if (str == null) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == 92) {
                return true;
            }
        }
        return false;
    }
}
