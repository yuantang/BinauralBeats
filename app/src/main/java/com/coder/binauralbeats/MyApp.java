package com.coder.binauralbeats;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Config;

import com.coder.binauralbeats.utils.Preferences;
import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.github.moduth.blockcanary.internal.BlockInfo;
import com.google.android.gms.ads.MobileAds;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

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
        // 在主进程初始化调用哈
        if (BuildConfig.DEBUG) {
            BlockCanary.install(this, new AppBlockCanaryContext()).start();
        }
    }
    public class AppBlockCanaryContext extends BlockCanaryContext {
        // 实现各种上下文，包括应用标示符，用户uid，网络类型，卡慢判断阙值，Log保存位置等

        /**
         * Implement in your project.
         *
         * @return Qualifier which can specify this installation, like version + flavor.
         */
        @Override
        public String provideQualifier() {
            String qualifier = "";
            try {
                PackageInfo info = MyApp.getInstance().getPackageManager()
                        .getPackageInfo(MyApp.getInstance().getPackageName(), 0);
                qualifier += info.versionCode + "_" + info.versionName + "_YYB";
            } catch (PackageManager.NameNotFoundException e) {

            }
            return qualifier;
        }

        /**
         * Implement in your project.
         *
         * @return user id
         */
        @Override
        public String provideUid() {
            return "1111111111111";
        }

        /**
         * Network type
         *
         * @return {@link String} like 2G, 3G, 4G, wifi, etc.
         */
        @Override
        public String provideNetworkType() {
            return "wifi";
        }

        /**
         * Config monitor duration, after this time BlockCanary will stop, use
         * with {@code BlockCanary}'s isMonitorDurationEnd
         *
         * @return monitor last duration (in hour)
         */
        @Override
        public int provideMonitorDuration() {
            return 999;
        }

        /**
         * Config block threshold (in millis), dispatch over this duration is regarded as a BLOCK. You may set it
         * from performance of device.
         *
         * @return threshold in mills
         */
        @Override
        public int provideBlockThreshold() {
            return 400;
        }

        /**
         * Thread stack dump interval, use when block happens, BlockCanary will dump on main thread
         * stack according to current sample cycle.
         * <p>
         * Because the implementation mechanism of Looper, real dump interval would be longer than
         * the period specified here (especially when cpu is busier).
         * </p>
         *
         * @return dump interval (in millis)
         */
        @Override
        public int provideDumpInterval() {
            return provideBlockThreshold();
        }

        /**
         * Path to save log, like "/blockcanary/", will save to sdcard if can.
         *
         * @return path of log files
         */
        @Override
        public String providePath() {
            return "/blockcanary/";
        }

        /**
         * If need notification to notice block.
         *
         * @return true if need, else if not need.
         */
        @Override
        public boolean displayNotification() {
            return true;
        }

        /**
         * Implement in your project, bundle files into a zip file.
         *
         * @param src  files before compress
         * @param dest files compressed
         * @return true if compression is successful
         */
        @Override
        public boolean zip(File[] src, File dest) {
            return false;
        }

        /**
         * Implement in your project, bundled log files.
         *
         * @param zippedFile zipped file
         */
        @Override
        public void upload(File zippedFile) {
            throw new UnsupportedOperationException();
        }


        /**
         * Packages that developer concern, by default it uses process name,
         * put high priority one in pre-order.
         *
         * @return null if simply concern only package with process name.
         */
        @Override
        public List<String> concernPackages() {
            return null;
        }

        /**
         * Filter stack without any in concern package, used with @{code concernPackages}.
         *
         * @return true if filter, false it not.
         */
        @Override
        public boolean filterNonConcernStack() {
            return false;
        }

        /**
         * Provide white list, entry in white list will not be shown in ui list.
         *
         * @return return null if you don't need white-list filter.
         */
        @Override
        public List<String> provideWhiteList() {
            LinkedList<String> whiteList = new LinkedList<>();
            whiteList.add("org.chromium");
            return whiteList;
        }

        /**
         * Whether to delete files whose stack is in white list, used with white-list.
         *
         * @return true if delete, false it not.
         */
        @Override
        public boolean deleteFilesInWhiteList() {
            return true;
        }

        /**
         * Block interceptor, developer may provide their own actions.
         */
        @Override
        public void onBlock(Context context, BlockInfo blockInfo) {

        }
    }

}
