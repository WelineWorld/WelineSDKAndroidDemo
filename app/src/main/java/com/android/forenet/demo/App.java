package com.android.forenet.demo;


import android.app.Application;
import net.sdvn.shield.MobileAPI;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //你的 devicesClassNum，需配置，如29823
        int devicesClassNum = 196865;
        //将app注册到平台
        MobileAPI.init(this, "CN6SDL3H5K4UL55YP77L", "Y1DMATNYSMZPOKC3R8NJ", devicesClassNum);
        //配置PAC域名过滤，
//        MobileAPI.setPacOptionEnable(new String[]{"xxx.xxx.xxx"});
    }
}
