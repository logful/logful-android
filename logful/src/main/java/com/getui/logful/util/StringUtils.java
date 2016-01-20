package com.getui.logful.util;

public class StringUtils {

    public static String join(String[] array, String separator) {
        if (array == null) {
            return "";
        }
        int length = array.length;
        if (length == 0) {
            return "";
        }
        String result = "";
        for (int i = 0; i < length; i++) {
            result += array[i];
            if (i != length - 1) {
                result += separator;
            }
        }
        return result;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }
}
