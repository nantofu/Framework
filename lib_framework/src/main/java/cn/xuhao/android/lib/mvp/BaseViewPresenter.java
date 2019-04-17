package cn.xuhao.android.lib.mvp;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import cn.xuhao.android.lib.observer.lifecycle.ILifecycleObserver;

/**
 * Created by xuhao on 2017/4/19.
 */

public abstract class BaseViewPresenter<T extends IBaseView> implements ILifecycleObserver {

    protected T mView;

    public BaseViewPresenter(@NonNull T view) {
        mView = view;
        mView.addLifecycleObserver(this);
    }

    @Override
    @CallSuper
    public void onCreate() {

    }

    @Override
    @CallSuper
    public void onDestroy() {
    }

    public boolean isViewAttached() {
        return mView != null;
    }

    public T getView() {
        return mView;
    }

    public Context getContext() {
        if (isViewAttached()) {
            return mView.getContext();
        }
        return null;
    }

    public final String getString(@StringRes int resId) {
        return getContext().getString(resId);
    }

    public final String getString(@StringRes int resId, Object... args) {
        return getContext().getString(resId, args);
    }

}
