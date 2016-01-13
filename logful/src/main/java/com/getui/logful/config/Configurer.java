package com.getui.logful.config;

public interface Configurer {

    long timestamp();

    boolean on();

    boolean interrupt();

    long interval();

    long frequency();
}
