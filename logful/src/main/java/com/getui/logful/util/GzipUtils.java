package com.getui.logful.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GzipUtils {

    private static final String TAG = "GzipUtils";

    private static final int BUFFER = 1024;

    /**
     * Compress input file.
     *
     * @param inFilePath  Input file
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
            return false;
        } finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(fileOutputStream);
            IOUtils.closeQuietly(gzipOutputStream);
        }
    }

    public static byte[] compress(String filePath) {
        FileInputStream fis = null;
        GZIPOutputStream gzip = null;
        ByteArrayOutputStream output = null;
        try {
            byte[] buffer = new byte[BUFFER];
            fis = new FileInputStream(filePath);
            output = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(output);
            int len;
            while ((len = fis.read(buffer)) > 0) {
                gzip.write(buffer, 0, len);
            }
            gzip.close();
            return output.toByteArray();
        } catch (Exception e) {
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(gzip);
            IOUtils.closeQuietly(output);
        }
    }

}
