package com.getui.logful;

public interface LifeCycle {

    public enum State {
        /**
         * Initialized but not yet started.
         */
        INITIALIZED,
        /**
         * In the process of starting.
         */
        STARTING,
        /**
         * Has started.
         */
        STARTED,
        /**
         * Stopping is in progress.
         */
        STOPPING,
        /**
         * Has stopped.
         */
        STOPPED
    }

    /**
     * Gets the life-cycle state.
     *
     * @return the life-cycle state
     */
    State getState();

    void start();

    void stop();

    boolean isStarted();

    boolean isStopped();
}
