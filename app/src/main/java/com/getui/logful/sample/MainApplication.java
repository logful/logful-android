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

        LoggerFactory.setApiUrl("http://192.168.14.198:9600");
        LoggerFactory.setAppKey("24ce2fd43ca804de04592013df2982a7");
        LoggerFactory.setAppSecret("eb02f34a882e29d1b27056ac975212d1");
        LoggerFactory.setDebug(true);
        LoggerFactory.init(this, builder.build());

        // Init getui push sdk.
        // PushManager.getInstance().initialize(this);
    }
}
