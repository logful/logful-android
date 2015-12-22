package com.getui.logful.util;

import android.util.Base64;

import java.util.HashMap;
import java.util.Map;

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

    public static int charCount(String string, char separator) {
        int counter = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == separator) {
                counter++;
            }
        }
        return counter;
    }

    public static String mapToPropertiesString(HashMap<String, String> propertiesMap) {
        StringBuilder builder = new StringBuilder();
        int size = propertiesMap.size();
        int index = 0;
        for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (index == size - 1) {
                builder.append(String.format("%s=%s", key, value));
            } else {
                builder.append(String.format("%s=%s\n", key, value));
            }
            index++;
        }
        return builder.toString();
    }

    public static String base64Encode(String input) {
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

}
