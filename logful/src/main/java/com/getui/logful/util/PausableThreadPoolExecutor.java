package com.getui.logful.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PausableThreadPoolExecutor extends ThreadPoolExecutor {
    private boolean isPaused;
    private ReentrantLock lock;
    private Condition condition;

    /**
     * PausableThreadPoolExecutor.
     *
     * @param corePoolSize    The size of the pool
     * @param maximumPoolSize The maximum size of the pool
     * @param keepAliveTime   The amount of time you wish to keep a single task alive
     * @param unit            The unit of time that the keep alive time represents
     * @param workQueue       The queue that holds your tasks
     * @see {@link ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue)}
     */
    public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                      BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    /**
     * beforeExecute.
     *
     * @param thread   The thread being executed
     * @param runnable The runnable task
     * @see {@link ThreadPoolExecutor#beforeExecute(Thread, Runnable)}
     */
    @Override
    protected void beforeExecute(Thread thread, Runnable runnable) {
        super.beforeExecute(thread, runnable);
        lock.lock();
        try {
            while (isPaused) {
                condition.await();
            }
        } catch (InterruptedException ie) {
            thread.interrupt();
        } finally {
            lock.unlock();
        }
    }

    public boolean isRunning() {
        return !isPaused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Pause the execution.
     */
    public void pause() {
        lock.lock();
        try {
            isPaused = true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Resume pool execution.
     */
    public void resume() {
        lock.lock();
        try {
            isPaused = false;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
