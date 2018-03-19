package com.coder.binauralbeats;

import android.app.Application;

import com.coder.binauralbeats.utils.Preferences;

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

    }

}
