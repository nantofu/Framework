package com.didi.android.framework.fragment;

import android.os.Bundle;

import com.didi.android.framework.MainPresenter2;
import com.didi.android.framework.R;

import cn.xuhao.android.lib.fragment.BaseFragment;
import cn.xuhao.android.lib.mvp.IBaseView;

public class MainFragment extends BaseFragment implements IBaseView {

    private MainPresenter2 mMainPresenter2;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void parseBundle(Bundle bundle) {

    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void initObjects() {
        mMainPresenter2 = new MainPresenter2(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }
}
