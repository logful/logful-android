package com.getui.logful.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.UUID;

import android.Manifest;
import android.content.Context;

import com.getui.logful.LoggerFactory;
import com.getui.logful.util.Compatibility;
import com.getui.logful.util.DateTimeUtil;
import com.getui.logful.util.EnvironmentUtil;
import com.getui.logful.util.LogUtil;
import com.getui.logful.util.PackageManagerWrapper;

public class CrashReportDataFactory {

    public static CrashReportData createCrashData(Thread thread, Throwable throwable) {
        CrashReportData crashReportData = new CrashReportData();

        // Generate uid
        crashReportData.setUid(UUID.randomUUID().toString());

        // Collect throwable stack trace
        crashReportData.setStackTrace(stackTrace(throwable));

        // Generate stack track hash code
        crashReportData.setStackTraceHash(stackTraceHash(throwable));

        // Collect thread detail
        crashReportData.setThreadDetail(threadDetail(thread));

        // Collect total internal memory
        crashReportData.setTotalMemorySize(Long.toString(EnvironmentUtil.getTotalInternalMemorySize()));

        // Collect available memory
        crashReportData.setAvailableMemorySize(Long.toString(EnvironmentUtil.getAvailableInternalMemorySize()));

        // Collect schedule time
        long startTimeMillis = ExceptionReporter.getStartTimeMillis();
        crashReportData.setAppStartDate(DateTimeUtil.timeString(startTimeMillis));
        crashReportData.setAppStartTimeMillis(Long.toString(startTimeMillis));

        // Collect crash time
        long crashTimeMillis = System.currentTimeMillis();
        crashReportData.setCrashTimeMillis(Long.toString(crashTimeMillis));
        crashReportData.setCrashDate(DateTimeUtil.timeString(crashTimeMillis));

        final Context context = LoggerFactory.context();
        if (context == null) {
            return crashReportData;
        }

        // Collect LogCat data
        // Before JellyBean, this required the READ_LOGS permission
        // Since JellyBean, READ_LOGS is not granted to third-party apps anymore for security
        // reasons.
        // Though, we can call logcat without any permission and still get traces related to our
        // app.
        PackageManagerWrapper pm = new PackageManagerWrapper(context);
        final boolean hasReadLogsPermission =
                pm.hasPermission(Manifest.permission.READ_LOGS)
                        || (Compatibility.getAPILevel() >= Compatibility.VersionCodes.JELLY_BEAN);
        if (hasReadLogsPermission) {
            try {
                crashReportData.setLogcat(LogCatCollector.collectLogCat(null));
            } catch (RuntimeException e) {
                LogUtil.e("CrashReportDataFactory", "RuntimeException", e);
            }

            try {
                crashReportData.setEventsLog(LogCatCollector.collectLogCat("events"));
            } catch (RuntimeException e) {
                LogUtil.e("CrashReportDataFactory", "RuntimeException", e);
            }

            try {
                crashReportData.setRadioLog(LogCatCollector.collectLogCat("radio"));
            } catch (RuntimeException e) {
                LogUtil.e("CrashReportDataFactory", "RuntimeException", e);
            }
        }

        return crashReportData;
    }

    /**
     * Get throwable stack trace.
     *
     * @param throwable Throwable
     * @return Stack trace {@link String}
     */
    private static String stackTrace(Throwable throwable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        Throwable cause = throwable;
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        final String stacktraceAsString = result.toString();
        printWriter.close();

        return stacktraceAsString;
    }

    private static String stackTraceHash(Throwable throwable) {
        final StringBuilder res = new StringBuilder();
        Throwable cause = throwable;
        while (cause != null) {
            final StackTraceElement[] stackTraceElements = cause.getStackTrace();
            for (final StackTraceElement e : stackTraceElements) {
                res.append(e.getClassName());
                res.append(e.getMethodName());
            }
            cause = cause.getCause();
        }

        return Integer.toHexString(res.toString().hashCode());
    }

    /**
     * Get thread detail.
     *
     * @param thread Broken thread
     * @return Thread detail {@link String}
     */
    public static String threadDetail(Thread thread) {
        StringBuilder result = new StringBuilder(100);
        if (thread != null) {
            String info =
                    "id=" + thread.getId() + "\n" + "name=" + thread.getName() + "\n" + "priority="
                            + thread.getPriority() + "\n";
            result.append(info);
            if (thread.getThreadGroup() != null) {
                String groupInfo = "groupName=" + thread.getThreadGroup().getName() + "\n";
                result.append(groupInfo);
            }
        } else {
            result.append("No broken thread, this might be a silent exception.");
        }
        return result.toString();
    }

    /**
     * Generate serialize JSON string.
     *
     * @param reportData CrashReportData
     * @return JSON {@link String} of {@link CrashReportData}
     */
    public static String toJson(CrashReportData reportData) {
        // TODO
        return "";
    }

    /**
     * Generate properties string.
     *
     * @param reportData CrashReportData
     * @return properties {@link String}
     */
    public static String properties(CrashReportData reportData) {
        return "appStartDate\n" + reportData.getAppStartDate() + "\n\nappStartTimeMillis\n"
                + reportData.getAppStartTimeMillis() + "\n\ncrashDate\n" + reportData.getCrashDate()
                + "\n\ncrashTimeMillis\n" + reportData.getCrashTimeMillis() + "\n\ntotalMemorySize\n"
                + reportData.getTotalMemorySize() + "\n\navailableMemorySize\n" + reportData.getAvailableMemorySize()
                + "\n\nthreadDetail\n" + reportData.getThreadDetail() + "\n\nstackTraceHash\n"
                + reportData.getStackTraceHash() + "\n\nstackTrace\n" + reportData.getStackTrace() + "\n\nlogcat\n"
                + reportData.getLogcat() + "\n\neventsLog\n" + reportData.getEventsLog() + "\n\nradioLog\n"
                + reportData.getRadioLog();
    }
}
