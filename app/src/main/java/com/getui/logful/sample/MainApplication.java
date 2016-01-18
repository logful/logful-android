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

        LoggerFactory.setApiUrl("http://demo.logful.aoapp.com:9600");
        LoggerFactory.setAppKey("beed06257195f47de875fa222c636769");
        LoggerFactory.setAppSecret("9bd521b5bdc5d5ae3b54380495f10e55");
        LoggerFactory.setDebug(true);
        LoggerFactory.init(this, builder.build());

        // Init getui push sdk.
        PushManager.getInstance().initialize(this);
    }
}
