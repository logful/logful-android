package com.getui.logful.sample;

import android.app.Application;

import com.getui.logful.annotation.LogProperties;
import com.igexin.sdk.PushManager;

@LogProperties(defaultLogger = "logger",
        defaultMsgLayout = "s,sendMessage,%s|g,getMessage,%s|r,result,%n|b,back,%n|c,call,%n")
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Init getui push sdk.
        PushManager.getInstance().initialize(this);
    }
}
