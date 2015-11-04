package com.getui.logful;

import java.io.Serializable;

public class AbstractLifeCycle implements LifeCycle, Serializable {

    private volatile LifeCycle.State state = LifeCycle.State.INITIALIZED;

    protected boolean equalsImpl(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LifeCycle other = (LifeCycle) obj;
        return state == other.getState();
    }

    protected int hashCodeImpl() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return result;
    }

    @Override
    public State getState() {
        return this.state;
    }

    public boolean isInitialized() {
        return this.state == LifeCycle.State.INITIALIZED;
    }

    @Override
    public boolean isStarted() {
        return this.state == LifeCycle.State.STARTED;
    }

    public boolean isStarting() {
        return this.state == LifeCycle.State.STARTING;
    }

    @Override
    public boolean isStopped() {
        return this.state == LifeCycle.State.STOPPED;
    }

    public boolean isStopping() {
        return this.state == LifeCycle.State.STOPPING;
    }

    protected void setStarted() {
        this.setState(LifeCycle.State.STARTED);
    }

    protected void setStarting() {
        this.setState(LifeCycle.State.STARTING);
    }

    protected void setState(final LifeCycle.State newState) {
        this.state = newState;
    }

    protected void setStopped() {
        this.setState(LifeCycle.State.STOPPED);
    }

    protected void setStopping() {
        this.setState(LifeCycle.State.STOPPING);
    }

    @Override
    public void start() {
        this.setStarted();
    }

    @Override
    public void stop() {
        this.state = LifeCycle.State.STOPPED;
    }
}
