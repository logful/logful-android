package com.getui.logful.util;

import java.lang.reflect.Field;

import android.os.Build;

public class Compatibility {

    public class VersionCodes {
        public static final int ECLAIR = 5;
        public static final int FROYO = 8;
        public static final int ICE_CREAM_SANDWICH = 14;
        public static final int JELLY_BEAN = 16;
        public static final int JELLY_BEAN_MR1 = 17;
        public static final int LOLLIPOP = 21;
    }

    public static int getAPILevel() {
        int apiLevel;
        try {
            // This field has been added in Android 1.6
            final Field SDK_INT = Build.VERSION.class.getField("SDK_INT");
            apiLevel = SDK_INT.getInt(null);
        } catch (SecurityException e) {
            apiLevel = Integer.parseInt(Build.VERSION.SDK);
        } catch (NoSuchFieldException e) {
            apiLevel = Integer.parseInt(Build.VERSION.SDK);
        } catch (IllegalArgumentException e) {
            apiLevel = Integer.parseInt(Build.VERSION.SDK);
        } catch (IllegalAccessException e) {
            apiLevel = Integer.parseInt(Build.VERSION.SDK);
        }

        return apiLevel;
    }
}
