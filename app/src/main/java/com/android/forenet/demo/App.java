package com.android.forenet.demo;


import android.app.Application;
import net.sdvn.shield.MobileAPI;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //你的 devicesClassNum，需配置，如29823
        int devicesClassNum = 0;
        //将app注册到平台
        MobileAPI.init(this, "你的 appId", "你的 partnerId", devicesClassNum);
        //配置PAC域名过滤，
//        MobileAPI.setPacOptionEnable(new String[]{"xxx.xxx.xxx"});
    }
}
