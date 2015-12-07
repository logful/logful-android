package com.getui.logful.util;

import java.io.File;

public class FileUtils {

    public static boolean deleteQuietly(String filePath) {
        if (filePath == null) {
            return false;
        }
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                try {
                    return file.delete();
                } catch (Exception ignored) {
                    // Ignore
                }
            }
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    boolean successful = true;
                    for (File item : files) {
                        try {
                            if (!item.delete()) {
                                successful = false;
                            }
                        } catch (Exception ignored) {
                            // Ignore
                        }
                    }
                    return successful;
                }
            }
        }
        return false;
    }

}
