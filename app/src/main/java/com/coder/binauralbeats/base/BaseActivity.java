package com.coder.binauralbeats.base;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.coder.binauralbeats.R;
import com.coder.binauralbeats.event.BusEvent;
import com.coder.binauralbeats.basemvp.MvpBasePresenter;
import com.coder.binauralbeats.basemvp.MvpBaseView;
import com.coder.binauralbeats.permission.PermissionReq;
import com.coder.binauralbeats.utils.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by TUS on 2017/8/24.
 */

public abstract class BaseActivity<V extends MvpBaseView, P extends MvpBasePresenter> extends AppCompatActivity {

    private Unbinder unbinder;
    protected P mPresenter;
    private V mView;
    protected Handler mHandler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if (Preferences.isNightMode()) {
            setTheme(getDarkTheme());
        }

        setContentView(getLayout());
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
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

        ActivityCollector.addActivity(this);
        initEventAndData();
        setToolBar();
        super.onCreate(savedInstanceState);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventReceive(BusEvent event) {

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
        EventBus.getDefault().unregister(this);
    }
}
