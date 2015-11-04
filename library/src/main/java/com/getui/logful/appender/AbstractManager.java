package com.getui.logful.appender;

import com.getui.logful.util.LogUtil;

import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractManager {

    private static final String TAG = "AbstractManager";

    private final String filePath;

    private static final ConcurrentHashMap<String, AbstractManager> MAP = new ConcurrentHashMap<String, AbstractManager>();

    protected AbstractManager(final String filePath) {
        this.filePath = filePath;
    }

    public static <M extends AbstractManager, T> M getManager(final String filePath,
                                                              final ManagerFactory<M, T> factory,
                                                              final T data) {
        M manager = (M) MAP.get(filePath);
        if (manager == null) {
            manager = factory.createManager(filePath, data);
            if (manager == null) {
                LogUtil.e(TAG, "Unable to create a manager.");
            }
            MAP.put(filePath, manager);
        }
        return manager;
    }

    protected void releaseSub() {

    }

    public void release() {
        MAP.remove(filePath);
        releaseSub();
    }

    public void remove() {
        MAP.remove(filePath);
    }

    public String getFilePath() {
        return filePath;
    }
}
