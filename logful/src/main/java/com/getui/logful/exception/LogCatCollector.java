package com.getui.logful.exception;

import com.getui.logful.LoggerConstants;
import com.getui.logful.util.Compatibility;
import com.getui.logful.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LogCatCollector {
    /**
     * Default number of latest lines kept from the logcat output.
     */
    private static final int DEFAULT_TAIL_COUNT = 100;
    private static final int DEFAULT_LOGCAT_LINES = 100;
    private static final int DEFAULT_BUFFER_SIZE_IN_BYTES = 8192;
    private static final String[] LOGCAT_ARGUMENTS = new String[]{"-t", "" + DEFAULT_LOGCAT_LINES, "-v", "time"};

    /**
     * Executes the logcat command with arguments taken from.
     *
     * @param bufferName The name of the buffer to be read: "main" (default), "radio" or "events".
     * @return A {@link String} containing the latest lines of the output.
     */
    public static String collectLogCat(String bufferName) {
        final int myPid = android.os.Process.myPid();
        String myPidStr = null;
        if (myPid > 0) {
            myPidStr = Integer.toString(myPid) + "):";
        }

        final List<String> commandLine = new ArrayList<String>();
        commandLine.add("logcat");
        if (bufferName != null) {
            commandLine.add("-b");
            commandLine.add(bufferName);
        }

        // "-t n" argument has been introduced in FroYo (API level 8). For
        // devices with lower API level, we will have to emulate its job.
        final int tailCount;
        final List<String> logcatArgumentsList = new ArrayList<String>(Arrays.asList(LOGCAT_ARGUMENTS));

        final int tailIndex = logcatArgumentsList.indexOf("-t");
        if (tailIndex > -1 && tailIndex < logcatArgumentsList.size()) {
            tailCount = Integer.parseInt(logcatArgumentsList.get(tailIndex + 1));
            if (Compatibility.getAPILevel() < Compatibility.VersionCodes.FROYO) {
                logcatArgumentsList.remove(tailIndex + 1);
                logcatArgumentsList.remove(tailIndex);
                logcatArgumentsList.add("-d");
            }
        } else {
            tailCount = -1;
        }

        final LinkedList<String> logcatBuf =
                new BoundedLinkedList<String>(tailCount > 0 ? tailCount : DEFAULT_TAIL_COUNT);
        commandLine.addAll(logcatArgumentsList);

        BufferedReader bufferedReader = null;

        try {
            final Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));
            bufferedReader =
                    new BufferedReader(new InputStreamReader(process.getInputStream(), LoggerConstants.CHARSET),
                            DEFAULT_BUFFER_SIZE_IN_BYTES);

            // Dump stderr to null
            new Thread(new Runnable() {
                public void run() {
                    try {
                        InputStream stderr = process.getErrorStream();
                        byte[] dummy = new byte[DEFAULT_BUFFER_SIZE_IN_BYTES];
                        while (stderr.read(dummy) >= 0) {
                        }
                    } catch (IOException e) {
                        LogUtil.e("LogCatCollector", "IOException", e);
                    }
                }
            }).start();

            while (true) {
                final String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                if (myPidStr == null || line.contains(myPidStr)) {
                    logcatBuf.add(line + "\n");
                }
            }

        } catch (IOException e) {
            LogUtil.e("LogCatCollector", "IOException", e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LogUtil.e("LogCatCollector", "IOException", e);
                }
            }
        }
        return logcatBuf.toString();
    }

}
