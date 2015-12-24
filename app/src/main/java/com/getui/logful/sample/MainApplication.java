package com.getui.logful.sample;

import android.app.Application;

import com.getui.logful.LoggerFactory;
import com.getui.logful.annotation.LogProperties;

@LogProperties(defaultLogger = "logger",
        defaultMsgLayout = "s,sendMessage,%s|g,getMessage,%s|r,result,%n|b,back,%n|c,call,%n")
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Init logful sdk.
        LoggerFactory.setApiUrl("http://192.168.14.198:8800");
        LoggerFactory.setAppKey("b24a1290e9755c63b9ec5703be91883f");
        LoggerFactory.setAppSecret("3c7f66c0341b6892342b785b235b5455");
        LoggerFactory.init(this);

        // Init getui push sdk.
        //PushManager.getInstance().initialize(this);
    }
}
