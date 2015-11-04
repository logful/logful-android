package com.getui.logful.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageManagerWrapper {
    private final Context context;

    public PackageManagerWrapper(Context context) {
        this.context = context;
    }

    /**
     * @param permission Manifest.permission to check whether it has been granted.
     * @return true if the permission has been granted to the app, false if it hasn't been granted
     *         or the PackageManager could not be contacted.
     */
    public boolean hasPermission(String permission) {
        final PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return false;
        }

        try {
            return pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
        } catch (RuntimeException e) {
            // To catch RuntimeException("Package manager has died") that can occur on some version
            // of Android,
            // when the remote PackageManager is unavailable. I suspect this sometimes occurs when
            // the App is being reinstalled.
            return false;
        }
    }

    /**
     * @return PackageInfo for the current application or null if the PackageManager could not be
     *         contacted.
     */
    public PackageInfo getPackageInfo() {
        final PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return null;
        }

        try {
            return pm.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            LogUtil.e("PackageManagerWrapper", "NameNotFoundException", e);
            return null;
        } catch (RuntimeException e) {
            // To catch RuntimeException("Package manager has died") that can occur on some version
            // of Android,
            // when the remote PackageManager is unavailable. I suspect this sometimes occurs when
            // the App is being reinstalled.
            return null;
        }
    }
}
