package com.getui.logful.util;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.util.ArrayMap;
import android.view.View;

import com.getui.logful.Logger;
import com.getui.logful.LoggerConfigurator;
import com.getui.logful.LoggerConstants;
import com.getui.logful.LoggerFactory;
import com.getui.logful.appender.AsyncAppenderManager;
import com.getui.logful.appender.DefaultEvent;
import com.getui.logful.db.DatabaseManager;
import com.getui.logful.entity.AttachmentFileMeta;

public class CaptureTool {

    private static final String TAG = "CaptureTool";
    private static ThreadPoolExecutor executor;

    private static class ClassHolder {
        static CaptureTool tool = new CaptureTool();
    }

    public static CaptureTool tool() {
        return ClassHolder.tool;
    }

    public static void captureThenLog(Logger logger, int level, String tag, String msg) {
        CaptureTool tool = tool();
        tool.doAction(logger, level, tag, msg);
    }

    public void doAction(Logger logger, int level, String tag, String msg) {
        int location;
        String dirPath;
        if (LogStorage.writable()) {
            location = LoggerConstants.LOCATION_EXTERNAL;
            dirPath = LogStorage.externalAttachmentDir();
        } else {
            location = LoggerConstants.LOCATION_INTERNAL;
            dirPath = LogStorage.internalAttachmentDir();
        }
        if (!StringUtils.isEmpty(dirPath)) {
            int sequence = UniqueSequenceTool.sequence();
            String filename = sequence + ".jpg";
            final String filePath = dirPath + "/" + filename;
            // Submit capture task.
            CaptureTask task = new CaptureTask(logger, level, tag, msg, filename, filePath, location, sequence);
            getExecutor().submit(task);
        }
    }

    private ThreadPoolExecutor getExecutor() {
        if (executor == null || executor.isTerminated()) {
            executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        }
        return executor;
    }

    @TargetApi(value = Build.VERSION_CODES.KITKAT)
    private static Activity currentActivity() {
        Object activityThread = null;
        Field activitiesField = null;

        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
        } catch (Exception e) {
            LogUtil.e(TAG, "", e);
        }

        if (activityThread != null && activitiesField != null) {
            Collection collection = null;
            try {
                Object object = activitiesField.get(activityThread);
                // Begin api level 19 Google use ArrayMap instead of HashMap.
                if (object instanceof HashMap) {
                    HashMap activities = (HashMap) object;
                    collection = activities.values();
                } else if (object instanceof ArrayMap) {
                    ArrayMap activities = (ArrayMap) object;
                    collection = activities.values();
                }
                if (collection != null) {
                    try {
                        for (Object activityRecord : collection) {
                            Class activityRecordClass = activityRecord.getClass();
                            Field pausedField = activityRecordClass.getDeclaredField("paused");
                            pausedField.setAccessible(true);
                            if (!pausedField.getBoolean(activityRecord)) {
                                Field activityField = activityRecordClass.getDeclaredField("activity");
                                activityField.setAccessible(true);
                                return (Activity) activityField.get(activityRecord);
                            }
                        }
                    } catch (Exception e) {
                        LogUtil.e(TAG, "", e);
                    }
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "", e);
            }
        }
        return null;
    }

    private class CaptureTask implements Runnable {

        private Logger logger;
        private int level;
        private String tag;
        private String msg;

        private String filename;
        private String filePath;
        private int location;
        private int sequence;

        public CaptureTask(Logger logger, final int level, final String tag, final String msg, final String filename,
                           final String filePath, final int location, final int sequence) {
            this.logger = logger;
            this.level = level;
            this.tag = tag;
            this.msg = msg;

            this.filename = filename;
            this.filePath = filePath;
            this.location = location;
            this.sequence = sequence;
        }

        @Override
        public void run() {
            if (StringUtils.isEmpty(filename) || StringUtils.isEmpty(filePath) || sequence <= 0) {
                return;
            }

            Context context = LoggerFactory.context();
            if (context == null) {
                return;
            }

            Activity activity = currentActivity();
            if (activity == null) {
                return;
            }

            LoggerConfigurator config = LoggerFactory.config();
            if (config == null) {
                return;
            }

            View view = activity.getWindow().getDecorView().getRootView();
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            try {
                File imageFile = new File(filePath);
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = config.getScreenshotQuality();
                float scale = config.getScreenshotScale();

                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                int newWidth = Math.round(width * scale);
                int newHeight = Math.round(height * scale);

                Matrix matrix = new Matrix();
                matrix.setRectToRect(new RectF(0, 0, width, height), new RectF(0, 0, newWidth, newHeight),
                        Matrix.ScaleToFit.CENTER);
                Bitmap scaleBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

                scaleBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();
                saveThenLog();
            } catch (Exception e) {
                LogUtil.e(TAG, "", e);
            }
        }

        private void saveThenLog() {
            AttachmentFileMeta meta = new AttachmentFileMeta();
            meta.setFilename(filename);
            meta.setLocation(location);
            meta.setSequence(sequence);
            if (DatabaseManager.saveAttachmentFileMeta(meta)) {
                if (logger != null) {
                    DefaultEvent event =
                            DefaultEvent
                                    .createEvent(logger.getName(), level, tag, msg, logger.getMsgLayout(), sequence);
                    AsyncAppenderManager.manager().append(event);
                }
            }
        }
    }
}
