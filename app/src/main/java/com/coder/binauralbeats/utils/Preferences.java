package com.coder.binauralbeats.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;

/**
 * SharedPreferences工具类
 * Created by wcy on 2015/11/28.
 */
public class Preferences {

    private static final String NIGHT_MODE = "night_mode";
    private static final String PREFS_VIZ = "VIZ";
    private static final String OPEN_TIME = "open_time";

    private static Context sContext;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }


    public static boolean isNightMode() {
        return getBoolean(NIGHT_MODE, false);
    }
    public static void saveNightMode(boolean on) {
        saveBoolean(NIGHT_MODE, on);
    }

    public static void saveVizEnabled(boolean on) {
        saveBoolean(PREFS_VIZ, on);
    }
    public static boolean isVizEnabled() {
        return getBoolean(PREFS_VIZ, true);
    }

    public static int getOpenTimes(){
        return getInt(OPEN_TIME,0);
    }
    public static void saveOpenTime(){
        int lastTime=getInt(OPEN_TIME,0);
        lastTime++;
        saveInt(OPEN_TIME,lastTime);
    }

    private static boolean getBoolean(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    private static void saveBoolean(String key, boolean value) {
        getPreferences().edit().putBoolean(key, value).apply();
    }

    private static int getInt(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    private static void saveInt(String key, int value) {
        getPreferences().edit().putInt(key, value).apply();
    }

    private static long getLong(String key, long defValue) {
        return getPreferences().getLong(key, defValue);
    }

    private static void saveLong(String key, long value) {
        getPreferences().edit().putLong(key, value).apply();
    }

    private static String getString(String key, @Nullable String defValue) {
        return getPreferences().getString(key, defValue);
    }

    private static void saveString(String key, @Nullable String value) {
        getPreferences().edit().putString(key, value).apply();
    }

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(sContext);
    }
}
