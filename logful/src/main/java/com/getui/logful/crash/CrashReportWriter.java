package com.getui.logful.crash;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.JsonWriter;

import com.getui.logful.LoggerConstants;
import com.getui.logful.LoggerFactory;
import com.getui.logful.db.DatabaseManager;
import com.getui.logful.entity.CrashReportFileMeta;
import com.getui.logful.util.Compatibility;
import com.getui.logful.util.EnvironmentUtils;
import com.getui.logful.util.LogStorage;
import com.getui.logful.util.LogUtil;
import com.getui.logful.util.PackageManagerWrapper;
import com.getui.logful.util.SystemInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.UUID;

public class CrashReportWriter {

    public static void write(Thread crashThread, Throwable throwable) throws Exception {
        Context context = LoggerFactory.context();
        if (context == null) {
            return;
        }

        int location;
        String crashReportDir;
        if (LogStorage.writable()) {
            location = LoggerConstants.LOCATION_EXTERNAL;
            crashReportDir = LogStorage.externalCrashReportDir();
        } else {
            location = LoggerConstants.LOCATION_INTERNAL;
            crashReportDir = LogStorage.internalCrashReportDir();
        }

        if (crashReportDir == null) {
            return;
        }

        String reportUUID = UUID.randomUUID().toString().toUpperCase();
        File file = new File(crashReportDir, reportUUID);
        if (!file.createNewFile()) {
            return;
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        JsonWriter writer = new JsonWriter(bufferedWriter);

        writer.beginObject();

        // Write report part.
        writer.name(CrashReportField.Report.REPORT).beginObject();
        writer.name(CrashReportField.Common.UUID).value(reportUUID);
        writer.name(CrashReportField.Report.TIMESTAMP).value(System.currentTimeMillis());

        writer.endObject();

        // Write crash part.
        writer.name(CrashReportField.Report.CRASH).beginObject();

        // Write all threads stack trace.
        writer.name(CrashReportField.Report.THREADS).beginArray();
        CrashReportWriter.writeAllThreadsStackTrace(writer, crashThread, throwable);
        writer.endArray();

        writer.name(CrashReportField.Report.ERROR).beginObject();
        writer.name(CrashReportField.Crash.REASON).value(throwable.getLocalizedMessage());
        writer.name(CrashReportField.Crash.TYPE).value(CrashConstants.EXCEPTION);

        // Write exception detail.
        writer.name(CrashReportField.Report.EXCEPTION).beginObject();
        writer.name(CrashReportField.Common.NAME).value(throwable.getClass().getName());
        StackTraceElement[] traces = throwable.getStackTrace();
        if (traces.length > 0) {
            CrashReportWriter.writeStackTrace(writer, traces[0]);
        }
        writer.endObject();

        writer.endObject();

        writer.endObject();

        // Write system part.
        writer.name(CrashReportField.Report.SYSTEM).beginObject();
        writer.name(CrashReportField.System.APP_START_TIME).value(CrashReporter.getStartTimeMillis());
        writer.name(CrashReportField.System.ROOTED).value(isDeviceRooted());

        // Write process info.
        CrashReportWriter.writeProcessInfo(writer);

        // Write package info.
        CrashReportWriter.writePackageInfo(writer);

        // Write device info.
        CrashReportWriter.writeDeviceInfo(writer);

        // Write memory part.
        CrashReportWriter.writeMemoryInfo(writer);

        // Write disk info.
        CrashReportWriter.writeDiskInfo(writer);

        writer.endObject();

        CrashReportWriter.writeLogcatRecord(writer);

        writer.endObject();

        writer.close();

        // Save crash report file meta.
        CrashReportFileMeta meta = new CrashReportFileMeta();
        meta.setLocation(location);
        meta.setFilename(reportUUID);
        DatabaseManager.saveCrashFileMeta(meta);

        LogUtil.d("==============", "++++++++++++++++++++++");
    }

    /**
     * Write all threads stack trace.
     *
     * @param writer      {@link JsonWriter} Writer
     * @param crashThread {@link Thread} Crash thread
     * @param throwable   {@link Throwable} Throwable
     * @throws Exception
     */
    private static void writeAllThreadsStackTrace(JsonWriter writer, Thread crashThread, Throwable throwable) throws Exception {
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parentGroup;
        while ((parentGroup = rootGroup.getParent()) != null) {
            rootGroup = parentGroup;
        }

        Thread[] threads = new Thread[rootGroup.activeCount() + 1];
        while (rootGroup.enumerate(threads, true) == threads.length) {
            threads = new Thread[threads.length * 2];
        }

        for (Thread thread : threads) {
            if (thread != null) {
                // Write thread detail.
                writer.beginObject();
                writer.name(CrashReportField.Common.ID).value(thread.getId());
                writer.name(CrashReportField.Common.NAME).value(thread.getName());
                writer.name(CrashReportField.Thread.PRIORITY).value(thread.getPriority());
                writer.name(CrashReportField.Thread.GROUP_NAME).value(thread.getThreadGroup().getName());

                // Check is crash thread.
                writer.name(CrashReportField.Thread.CRASHED).value(thread == crashThread);

                writer.name(CrashReportField.Thread.STACK_TRACE).beginArray();
                StackTraceElement[] elements = thread == crashThread ? throwable.getStackTrace() : thread.getStackTrace();
                for (StackTraceElement element : elements) {
                    // Write stack trace.
                    writer.beginObject();
                    CrashReportWriter.writeStackTrace(writer, element);
                    writer.endObject();
                }

                writer.endArray();
                writer.endObject();
            }
        }
    }

    private static void writeStackTrace(JsonWriter writer, StackTraceElement element) throws Exception {
        writer.name(CrashReportField.StackTrace.CLASS_NAME).value(element.getClassName());
        writer.name(CrashReportField.StackTrace.METHOD_NAME).value(element.getMethodName());
        writer.name(CrashReportField.StackTrace.FILE_NAME).value(element.getFileName());
        writer.name(CrashReportField.StackTrace.LINE_NUMBER).value(element.getLineNumber());
    }

    /**
     * Write package info.
     *
     * @param writer {@link JsonWriter} Writer
     * @throws Exception
     */
    private static void writePackageInfo(JsonWriter writer) throws Exception {
        writer.name(CrashReportField.System.PACKAGE_NAME).value(SystemInfo.appId());
        writer.name(CrashReportField.System.VERSION).value(SystemInfo.version());
        writer.name(CrashReportField.System.VERSION_NAME).value(SystemInfo.versionString());
    }

    /**
     * Write device info.
     *
     * @param writer {@link JsonWriter} Writer
     * @throws Exception
     */
    @TargetApi(value = Build.VERSION_CODES.LOLLIPOP)
    private static void writeDeviceInfo(JsonWriter writer) throws Exception {
        writer.name(CrashReportField.System.MODEL).value(Build.MODEL);
        writer.name(CrashReportField.System.OS_VERSION).value(Build.VERSION.RELEASE);
        writer.name(CrashReportField.System.SDK_VERSION).value(Build.VERSION.SDK_INT);

        // Write support ABIs.
        writer.name(CrashReportField.System.SUPPORTED_ABI).beginArray();
        if (Compatibility.getAPILevel() >= Build.VERSION_CODES.LOLLIPOP) {
            for (String ABI : Build.SUPPORTED_ABIS) {
                writer.value(ABI);
            }
        } else {
            writer.value(Build.CPU_ABI);
            writer.value(Build.CPU_ABI2);
        }

        writer.endArray();
    }

    /**
     * Write memory info.
     *
     * @param writer {@link JsonWriter} Writer
     * @throws Exception
     */
    private static void writeMemoryInfo(JsonWriter writer) throws Exception {
        BufferedReader reader = null;
        try {
            String meminfo = "/proc/meminfo";
            String line;
            reader = new BufferedReader(new FileReader(meminfo));

            long total = 0;
            long available = 0;
            long count = 0;
            writer.name(CrashReportField.System.MEMORY).beginObject();
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (count > 5) {
                    break;
                }
                if (line.toLowerCase().contains("memtotal")) {
                    String[] parts = line.split("\\s+");
                    total = Long.parseLong(parts[1]);
                    count++;
                }
                if (line.toLowerCase().contains("memfree")
                        || line.toLowerCase().contains("buffers")
                        || line.toLowerCase().contains("cached")) {
                    String[] parts = line.split("\\s+");
                    available += Long.parseLong(parts[1]);
                    count++;
                }
            }
            writer.name(CrashReportField.Common.TOTAL).value(total);
            writer.name(CrashReportField.Common.AVAILABLE).value(available);
            writer.endObject();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Write disk info.
     *
     * @param writer {@link JsonWriter} Writer
     * @throws Exception
     */
    private static void writeDiskInfo(JsonWriter writer) throws Exception {
        writer.name(CrashReportField.System.DISK).beginObject();

        // Internal storage info.
        writer.name(CrashReportField.System.INTERNAL).beginObject();
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        writer.name(CrashReportField.Common.TOTAL).value(EnvironmentUtils.getTotalMemorySize(stat));
        writer.name(CrashReportField.Common.AVAILABLE).value(EnvironmentUtils.getAvailableMemorySize(stat));
        writer.endObject();

        // External storage info.
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            writer.name(CrashReportField.System.EXTERNAL).beginObject();
            stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            writer.name(CrashReportField.Common.TOTAL).value(EnvironmentUtils.getTotalMemorySize(stat));
            writer.name(CrashReportField.Common.AVAILABLE).value(EnvironmentUtils.getAvailableMemorySize(stat));
            writer.endObject();
        }

        writer.endObject();
    }

    /**
     * Write crash process info.
     *
     * @param writer {@link JsonWriter} Writer
     * @throws Exception
     */
    private static void writeProcessInfo(JsonWriter writer) throws Exception {
        int pid = android.os.Process.myPid();
        writer.name(CrashReportField.System.PROCESS_ID).value(pid);
        BufferedReader cmdlineReader = null;
        try {
            String cmdline = "/proc/" + pid + "/cmdline";
            cmdlineReader = new BufferedReader(new InputStreamReader(new FileInputStream(cmdline), "iso-8859-1"));
            int c;
            StringBuilder processName = new StringBuilder();
            while ((c = cmdlineReader.read()) > 0) {
                processName.append((char) c);
            }
            // Write process name.
            writer.name(CrashReportField.System.PROCESS_NAME).value(processName.toString());
        } finally {
            if (cmdlineReader != null) {
                cmdlineReader.close();
            }
        }
    }

    /**
     * Write logcat record.
     *
     * @param writer {@link JsonWriter} Writer
     */
    private static void writeLogcatRecord(JsonWriter writer) throws Exception {
        Context context = LoggerFactory.context();
        if (context == null) {
            return;
        }
        PackageManagerWrapper pm = new PackageManagerWrapper(context);
        final boolean permitted = pm.hasPermission(Manifest.permission.READ_LOGS)
                || (Compatibility.getAPILevel() >= Build.VERSION_CODES.JELLY_BEAN);
        if (permitted) {
            writer.name(CrashReportField.Report.LOGCAT).beginObject();
            writer.name(CrashReportField.Logcat.LOGCAT).value(LogCatCollector.collect(null));
            writer.name(CrashReportField.Logcat.EVENTS).value(LogCatCollector.collect("events"));
            writer.name(CrashReportField.Logcat.RADIO).value(LogCatCollector.collect("radio"));
            writer.endObject();
        }
    }

    private static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = {"/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return in.readLine() != null;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

}
