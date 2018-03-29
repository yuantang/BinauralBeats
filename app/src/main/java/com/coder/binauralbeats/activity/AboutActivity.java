package com.coder.binauralbeats.activity;

import android.content.Intent;

import com.coder.binauralbeats.R;
import com.coder.binauralbeats.base.BaseActivity;
import com.coder.binauralbeats.basemvp.MvpBasePresenter;
import com.coder.binauralbeats.basemvp.MvpBaseView;

public class AboutActivity extends BaseActivity {

    @Override
    protected int getLayout() {
        return R.layout.activity_about;
    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return null;
    }

    @Override
    protected MvpBaseView createView() {
        return null;
    }

    @Override
    protected void superInit(Intent intent) {

    }

    @Override
    protected void initEventAndData() {

    }
}
