package com.getui.logful.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.getui.logful.LoggerFactory;
import com.getui.logful.util.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleExecutor {

    private static final String TAG = "ScheduleExecutor";

    private static final long FIRST_EXEC_DELAY = 8;

    private ScheduledExecutorService executor;

    private static class ClassHolder {
        static ScheduleExecutor scheduler = new ScheduleExecutor();
    }

    public static ScheduleExecutor scheduler() {
        return ClassHolder.scheduler;
    }

    public static void schedule(long scheduleTime) {
        ScheduleExecutor scheduler = ScheduleExecutor.scheduler();

        SchemeRunnable runnable = new SchemeRunnable();
        runnable.addTask(new RefreshScheduleTask("Refresh"));
        runnable.addTask(new ClearScheduledTask("Clear"));
        runnable.addTask(new UploadScheduleTask("Upload"));
        scheduler.getExecutor().scheduleAtFixedRate(runnable, FIRST_EXEC_DELAY, scheduleTime, TimeUnit.SECONDS);
    }

    public static void schedule(String[] scheduleArray) {
        Context context = LoggerFactory.context();
        if (context == null) {
            return;
        }

        if (scheduleArray == null || scheduleArray.length == 0) {
            return;
        }

        String action = "LOGFUL_SCHEDULE";
        IntentFilter intentFilter = new IntentFilter(action);
        context.registerReceiver(new ScheduleReceiver(), intentFilter);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            alarmManager.cancel(pendingIntent);
        } catch (Exception e) {
            LogUtil.e(TAG, "Cancel all alarm failed.");
        }
        for (String timeString : scheduleArray) {
            if (timeString.length() == 5) {
                // Repeat every day.
                String temp = String.format("%s %s",
                        new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date()),
                        timeString);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(formatter.parse(temp));
                    pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000,
                            pendingIntent);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (timeString.length() == 11) {
                Calendar today = Calendar.getInstance();
                String temp = today.get(Calendar.YEAR) + "/" + timeString;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(formatter.parse(temp));
                    pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ScheduledExecutorService getExecutor() {
        if (executor == null) {
            executor = Executors.newSingleThreadScheduledExecutor();
        }
        return executor;
    }

}
