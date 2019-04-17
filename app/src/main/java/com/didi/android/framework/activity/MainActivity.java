package com.didi.android.framework.activity;

import android.support.annotation.Nullable;
import android.os.Bundle;

import com.didi.android.framework.MainPresenter;
import com.didi.android.framework.R;

import cn.xuhao.android.lib.activity.BaseActivity;

public class MainActivity extends BaseActivity implements IMainActivityContract.IView {

    private IMainActivityContract.IPresenter mPresenter;

    @Override
    protected int getContentLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void parseBundle(@Nullable Bundle bundle) {

    }

    @Override
    protected void initObjects() {
        mPresenter = new MainPresenter(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }
}
