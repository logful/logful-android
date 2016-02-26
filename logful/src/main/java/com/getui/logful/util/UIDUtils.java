package com.getui.logful.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Environment;

import com.getui.logful.LoggerFactory;

public class UIDUtils {
    private static final String TAG = "UIDUtils";

    private static String uid;
    private static final String UID_DIR = ".LogfulConfig";

    public static String uid() {
        Context context = LoggerFactory.context();
        if (context == null) {
            return "";
        }

        if (!StringUtils.isEmpty(UIDUtils.uid)) {
            return UIDUtils.uid;
        }

        // Read exist uid.
        String temp = UIDUtils.readUid(context);
        if (!StringUtils.isEmpty(temp)) {
            UIDUtils.set(temp);
            return UIDUtils.uid;
        }

        return UIDUtils.randomUid();
    }

    private static String generate(String original) {
        String temp = UUID.nameUUIDFromBytes(original.getBytes()).toString();
        UIDUtils.saveUid(temp);
        UIDUtils.set(temp);
        return UIDUtils.uid;
    }

    private static String randomUid() {
        String temp = UUID.randomUUID().toString();
        UIDUtils.saveUid(temp);
        UIDUtils.set(temp);
        return UIDUtils.uid;
    }

    private static void set(String string) {
        UIDUtils.uid = string.replace("-", "").toLowerCase();
    }

    private static void saveUid(String uid) {
        if (LogStorage.writable()) {
            File dir = new File(Environment.getExternalStorageDirectory(), UID_DIR);
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    UIDUtils.createUidFile(dir.getAbsolutePath(), uid);
                }
            } else if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                boolean successful = true;
                for (File file : files) {
                    if (!file.delete()) {
                        successful = false;
                    }
                }
                if (successful) {
                    UIDUtils.createUidFile(dir.getAbsolutePath(), uid);
                }
            } else {
                if (dir.delete()) {
                    if (dir.mkdirs()) {
                        UIDUtils.createUidFile(dir.getAbsolutePath(), uid);
                    }
                }
            }
        }
    }

    private static void createUidFile(String dirPath, String uid) {
        File file = new File(dirPath + '/' + uid);
        try {
            boolean successful = file.createNewFile();
        } catch (IOException e) {
            LogUtil.e(TAG, "Create uid file failed.");
        }
    }

    @TargetApi(value = 14)
    private static String readUid(final Context context) {
        if (context == null) {
            return "";
        }
        if (LogStorage.readable()) {
            File dir = new File(Environment.getExternalStorageDirectory(), UID_DIR);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files.length == 1) {
                    String temp = files[0].getName();
                    try {
                        if (UUID.fromString(temp).toString().equals(temp)) {
                            return temp;
                        }
                    } catch (Exception e) {
                        return "";
                    }
                }
            }
        }
        return "";
    }

}
