package com.getui.logful.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipTool {

    private static final String TAG = GzipTool.class.getSimpleName();

    private static final int BUFFER = 1024;

    /**
     * Compress input file.
     *
     * @param inFilePath Input file
     * @param outFilePath Output file
     * @return result
     */
    public static boolean compress(String inFilePath, String outFilePath) {
        byte[] buffer = new byte[BUFFER];
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        GZIPOutputStream gzipOutputStream = null;
        try {
            fileInputStream = new FileInputStream(inFilePath);
            fileOutputStream = new FileOutputStream(outFilePath);
            gzipOutputStream = new GZIPOutputStream(fileOutputStream);
            int len;
            while ((len = fileInputStream.read(buffer)) > 0) {
                gzipOutputStream.write(buffer, 0, len);
            }

            fileInputStream.close();

            gzipOutputStream.flush();
            gzipOutputStream.close();

            fileOutputStream.close();

            return true;
        } catch (IOException e) {
            LogUtil.e(TAG, "", e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "", e);
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "", e);
                }
            }
            if (gzipOutputStream != null) {
                try {
                    gzipOutputStream.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "", e);
                }
            }
        }

        return false;
    }

    /**
     * Decompress file.
     *
     * @param inFilePath Input file
     * @param outFilePath Output file
     * @return result
     */
    public static boolean decompress(String inFilePath, String outFilePath) {
        GZIPInputStream gzipInputStream = null;
        FileOutputStream fileOutputStream = null;
        byte[] buffer = new byte[1024];
        try {
            gzipInputStream = new GZIPInputStream(new FileInputStream(inFilePath));
            fileOutputStream = new FileOutputStream(outFilePath);
            int len;
            while ((len = gzipInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, len);
            }
            gzipInputStream.close();

            fileOutputStream.flush();
            fileOutputStream.close();

            return true;
        } catch (IOException e) {
            LogUtil.e(TAG, "", e);
        } finally {
            if (gzipInputStream != null) {
                try {
                    gzipInputStream.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "", e);
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "", e);
                }
            }
        }
        return false;
    }

}
