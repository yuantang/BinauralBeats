package com.coder.binauralbeats.activity;

import android.os.Bundle;

import com.coder.binauralbeats.R;
import com.coder.binauralbeats.base.BaseActivity;
import com.coder.binauralbeats.basemvp.MvpBasePresenter;
import com.coder.binauralbeats.basemvp.MvpBaseView;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
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
    protected void initEventAndData() {

    }
}
