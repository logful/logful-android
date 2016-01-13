package com.getui.logful.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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

    public static String read(File filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        try {
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = reader.readLine();
            }
            return sb.toString();
        } finally {
            reader.close();
        }
    }

    public static void write(File file, String content, boolean append) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(file, append), 2048);
        out.write(content);
        out.flush();
        out.close();
    }
}
