package com.didi.android.framework;

import android.support.annotation.NonNull;

import com.didi.android.framework.activity.IMainActivityContract;

import cn.xuhao.android.lib.mvp.BasePresenter;

public class MainPresenter extends BasePresenter<IMainActivityContract.IView> implements IMainActivityContract.IPresenter {
    public MainPresenter(@NonNull IMainActivityContract.IView view) {
        super(view);
    }


}
