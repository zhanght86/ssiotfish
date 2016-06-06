package com.ssiot.fish;

import android.app.Application;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.videogo.openapi.EZOpenSDK;

//http://blog.csdn.net/hyx1990/article/details/7584789  在任意位置获取应用程序Context
public class ContextUtilApp extends Application {
    private static final String tag = "ContextUtilApp";
    private static ContextUtilApp instance;
    public static String YSYUN_APP_KEY = "53c5088dc4a84ecea79c379d6ef31646";

    public static ContextUtilApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        Log.v(tag, "----fish app create----");
        super.onCreate();
        instance = this;
        initYSYUN();
    }
    
    private void initYSYUN(){
        com.videogo.constant.Config.LOGGING = true;
        EZOpenSDK.initLib(this, YSYUN_APP_KEY, "");

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}