package com.coder.binauralbeats;

import android.app.Application;

import com.coder.binauralbeats.utils.Preferences;
import com.google.android.gms.ads.MobileAds;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by TUS on 2018/3/8.
 */

public class MyApp extends Application {
    private static MyApp mInstance;
    public static MyApp getInstance() {
        if (mInstance == null) {
            synchronized (MyApp.class) {
                if (mInstance == null) {
                    mInstance = new MyApp();
                }
            }
        }
        return mInstance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
        Preferences.init(this);
        /**Bugly*/
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setAppChannel("google play");
        CrashReport.initCrashReport(getApplicationContext(), "345a2cad31", false,strategy);
        CrashReport.setIsDevelopmentDevice(this, false);
        MobileAds.initialize(this,"ca-app-pub-4727610544131155~6488840515");

    }

}
