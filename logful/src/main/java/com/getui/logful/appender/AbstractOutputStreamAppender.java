package com.getui.logful.appender;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.getui.logful.layout.Layout;
import com.getui.logful.util.LogUtil;

public abstract class AbstractOutputStreamAppender<M extends OutputStreamManager> extends AbstractAppender {

    protected final boolean immediateFlush;

    private final M manager;

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private final Lock readLock = rwLock.readLock();

    private boolean isWriting;

    protected AbstractOutputStreamAppender(String loggerName, Layout layout, boolean ignoreExceptions,
            final boolean immediateFlush, final M manager) {
        super(loggerName, layout, ignoreExceptions);
        this.manager = manager;
        this.immediateFlush = immediateFlush;
        this.isWriting = false;
    }

    public M getManager() {
        return manager;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        manager.release();
    }

    @Override
    public boolean writing() {
        return isWriting;
    }

    @Override
    public synchronized void append(LogEvent logEvent) {
        readLock.lock();
        isWriting = true;
        try {
            final byte[] bytes = getLayout().toBytes(logEvent);
            if (bytes.length > 0) {
                manager.write(bytes);
                if (this.immediateFlush) {
                    manager.flush();
                }
            }
        } catch (final AppenderLoggingException e) {
            LogUtil.e("AbstractOutputStreamAppender", "", e);
        } finally {
            isWriting = false;
            readLock.unlock();
        }
    }
}
