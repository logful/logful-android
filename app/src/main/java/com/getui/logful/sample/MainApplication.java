package com.getui.logful.sample;

import android.app.Application;

import com.getui.logful.LoggerConfigurator;
import com.getui.logful.LoggerFactory;
import com.getui.logful.annotation.LogProperties;
import com.igexin.sdk.PushManager;

@LogProperties(defaultLogger = "logger",
        defaultMsgLayout = "s,sendMessage,%s|g,getMessage,%s|r,result,%n|b,back,%n|c,call,%n")
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LoggerConfigurator.Builder builder = LoggerConfigurator.newBuilder();
        builder.setCaughtException(true);
        builder.setDefaultLoggerName("logger");
        builder.setDefaultMsgLayout("s,sendMessage,%s|g,getMessage,%s|r,result,%n|b,back,%n|c,call,%n");
        builder.setUseNativeCryptor(false);

        LoggerFactory.setApiUrl("http://log.aoapp.com:9600");
        LoggerFactory.setAppKey("35dfb5b0b514d67260de923ea46d7a72");
        LoggerFactory.setAppSecret("464b5e734014fad356efd36b476ca105");
        LoggerFactory.setDebug(true);
        LoggerFactory.init(this, builder.build());

        // Init getui push sdk.
        PushManager.getInstance().initialize(this);
    }
}
