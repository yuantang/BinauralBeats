package com.coder.binauralbeats.base;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.coder.binauralbeats.BuildConfig;
import com.coder.binauralbeats.R;
import com.coder.binauralbeats.basemvp.MvpBasePresenter;
import com.coder.binauralbeats.basemvp.MvpBaseView;
import com.coder.binauralbeats.event.BusEvent;
import com.coder.binauralbeats.permission.PermissionReq;
import com.coder.binauralbeats.utils.Preferences;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;


import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by TUS on 2017/8/24.
 */

public abstract class BaseActivity<V extends MvpBaseView, P extends MvpBasePresenter> extends AppCompatActivity {

    private static String TAG ;
    private Unbinder unbinder;
    protected P mPresenter;
    private V mView;
    protected Handler mHandler = new Handler(Looper.getMainLooper());
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

//        if (Preferences.isNightMode()) {
//            setTheme(getDarkTheme());
//        }
        setContentView(getLayout());
        unbinder = ButterKnife.bind(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (this.mPresenter==null){
            this.mPresenter=createPresenter();
        }
        if (this.mView==null){
            this.mView=createView();
        }
        if (mPresenter!=null && mView!=null){
            mPresenter.attachView(mView);
        }
        TAG=this.getLocalClassName();
        ActivityCollector.addActivity(this);
        superInit(getIntent());
        initEventAndData();
        setToolBar();
        firebaseConfig();

        super.onCreate(savedInstanceState);
    }

    private void displayWelcomeMessage() {
        boolean isOpenAd = mFirebaseRemoteConfig.getBoolean("bb_is_open_ad");
        Log.e(TAG,"====isOpenAd:"+isOpenAd);
        if (isOpenAd) {
            addAdView();
        }
    }

    @StyleRes
    protected int getDarkTheme() {
        return R.style.AppThemeDark;
    }
    protected void setToolBar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        if (mToolbar !=null){
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    protected abstract int getLayout();
    protected abstract P createPresenter();
    protected abstract V createView();
    protected abstract void superInit(Intent intent);
    protected abstract void initEventAndData();
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null){
            mPresenter.detachView();
        }
        unbinder.unbind();
        ActivityCollector.removeActivity(this);
    }
    private void firebaseConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                        }
                        displayWelcomeMessage();
                    }
                });
    }
    private void addAdView(){
        final AdView adView=findViewById(R.id.adView);
        if (adView==null){
            return;
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                adView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                adView.setVisibility(View.GONE);
            }
            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }
            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                adView.setVisibility(View.GONE);
            }
        });
    }
}
