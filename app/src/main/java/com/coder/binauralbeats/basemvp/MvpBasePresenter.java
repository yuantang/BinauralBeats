package com.coder.binauralbeats.basemvp;

/**
 * Created by TUS on 2017/9/25.
 */

public class MvpBasePresenter<V extends MvpBaseView> {
    private V mMvpView;

    public void attachView(V view) {
        this.mMvpView=view;
    }

    public void detachView() {
        mMvpView=null;
    }

    public boolean isAttachView() {
        return mMvpView!=null;
    }

    public V getView() {
        return mMvpView;
    }
}
