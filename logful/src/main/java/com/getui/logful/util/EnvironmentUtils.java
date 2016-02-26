package com.getui.logful.util;

import java.io.File;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

public class EnvironmentUtils {

    /**
     * Calculates the free memory of the device. This is based on an inspection of the filesystem,
     * which in android devices is stored in RAM.
     *
     * @return Number of bytes available.
     */
    public static long getAvailableInternalMemorySize() {
        final File path = Environment.getDataDirectory();
        final StatFs stat = new StatFs(path.getPath());
        return getAvailableMemorySize(stat);
    }

    /**
     * Calculates the total memory of the device. This is based on an inspection of the filesystem,
     * which in android devices is stored in RAM.
     *
     * @return Total number of bytes.
     */
    public static long getTotalInternalMemorySize() {
        final File path = Environment.getDataDirectory();
        final StatFs stat = new StatFs(path.getPath());
        return getTotalMemorySize(stat);
    }

    public static long getAvailableExternalMemorySize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        return getAvailableMemorySize(stat);
    }

    public static long getTotalExternalMemorySize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        return getTotalMemorySize(stat);
    }

    @TargetApi(value = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getTotalMemorySize(final StatFs stat) {
        if (Compatibility.getAPILevel() >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final long blockSize = stat.getBlockSizeLong();
            final long totalBlocks = stat.getBlockCountLong();
            return totalBlocks * blockSize;

        } else {
            final long blockSize = stat.getBlockSize();
            final long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        }
    }

    @TargetApi(value = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getAvailableMemorySize(final StatFs stat) {
        if (Compatibility.getAPILevel() >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final long blockSize = stat.getBlockSizeLong();
            final long availableBlocks = stat.getAvailableBlocksLong();
            return availableBlocks * blockSize;
        } else {
            final long blockSize = stat.getBlockSize();
            final long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        }
    }
}
