package com.getui.logful.exception;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.getui.logful.LoggerFactory;
import com.getui.logful.util.Compatibility;
import com.getui.logful.util.LogUtil;
import com.getui.logful.util.activitylifecycled.ActivityLifecycleCallbacksCompat;
import com.getui.logful.util.activitylifecycled.ApplicationHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ExceptionReporter implements Thread.UncaughtExceptionHandler, ActivityLifecycleCallbacksCompat {

    private static final String TAG = ExceptionReporter.class.getSimpleName();

    private int elapsedTime = 0;

    private boolean toastWaitEnded = true;

    private static final int TOAST_WAIT_DURATION = 200;

    private final List<ReportSender> reportSenders = new ArrayList<ReportSender>();

    private WeakReference<Activity> lastActivityCreated = new WeakReference<Activity>(null);

    private long startTimeMillis;

    /**
     * Default UncaughtExceptionHandler.
     */
    private static Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    private static class ClassHolder {
        static ExceptionReporter instance = new ExceptionReporter();
    }

    public static ExceptionReporter reporter() {
        return ClassHolder.instance;
    }

    public static void caught() {
        ExceptionReporter reporter = reporter();
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(reporter);
    }

    public ExceptionReporter() {
        Application application = LoggerFactory.application();
        if (application == null) {
            throw new NullPointerException("Application is null!");
        }

        this.startTimeMillis = System.currentTimeMillis();

        if (Compatibility.getAPILevel() >= Compatibility.VersionCodes.ICE_CREAM_SANDWICH) {
            ApplicationHelper.registerActivityLifecycleCallbacks(application, this);
        }

        HttpReportSender httpSender = new HttpReportSender();
        reportSenders.add(httpSender);

        FileReportSender fileSender = new FileReportSender();
        reportSenders.add(fileSender);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        ReportBuilder reportBuilder = ReportBuilder.create(thread, throwable);
        report(thread, throwable, reportBuilder);
    }

    public static long getStartTimeMillis() {
        ExceptionReporter reporter = reporter();
        return reporter.startTimeMillis;
    }

    private void report(final Thread thread, final Throwable throwable, final ReportBuilder reportBuilder) {
        final Context context = LoggerFactory.context();
        if (context == null) {
            exitApplication(thread, throwable);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, "Save crash report and try send to server", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();

        toastWaitEnded = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (elapsedTime < TOAST_WAIT_DURATION) {
                    try {
                        // Wait a bit to let the user read the toast
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        LogUtil.e("ExceptionReporter", "InterruptedException", e);
                    }
                    elapsedTime += 100;
                }
                toastWaitEnded = true;
            }
        }).start();

        final CrashReportData crashData = reportBuilder.build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!toastWaitEnded) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        LogUtil.e("ExceptionReporter", "InterruptedException", e);
                    }
                }
                for (ReportSender sender : reportSenders) {
                    sender.send(context, crashData);
                }

                exitApplication(thread, throwable);
            }
        }).start();
    }

    /**
     * 退出应用程序.
     */
    private void exitApplication(final Thread thread, final Throwable throwable) {
        if (defaultUncaughtExceptionHandler != null) {
            Thread.setDefaultUncaughtExceptionHandler(defaultUncaughtExceptionHandler);
            defaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
        } else {
            final Activity lastActivity = lastActivityCreated.get();
            if (lastActivity != null) {
                lastActivity.finish();
                lastActivityCreated.clear();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        lastActivityCreated = new WeakReference<Activity>(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
