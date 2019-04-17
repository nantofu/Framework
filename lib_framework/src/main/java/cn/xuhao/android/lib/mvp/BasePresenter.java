package cn.xuhao.android.lib.mvp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import java.util.List;

import cn.xuhao.android.lib.activity.permisstion.IPermissionCompat;
import cn.xuhao.android.lib.activity.permisstion.callback.PermissionCallback;
import cn.xuhao.android.lib.activity.permisstion.callback.PermissionString;
import cn.xuhao.android.lib.observer.action.ActionObserverCompat;
import cn.xuhao.android.lib.observer.action.IActionObservable;
import cn.xuhao.android.lib.observer.action.IActionObserver;
import cn.xuhao.android.lib.observer.lifecycle.IComponentLifecycleObserver;
import cn.xuhao.android.lib.observer.lifecycle.ILifecycleObservable;
import cn.xuhao.android.lib.observer.lifecycle.ILifecycleObserver;
import cn.xuhao.android.lib.observer.lifecycle.LifecycleObserverCompat;

/**
 * @author xuhao
 */

public abstract class BasePresenter<T extends IBaseView>
        implements IComponentLifecycleObserver, ILifecycleObservable, IActionObservable, IActionObserver, IPermissionCompat {

    protected T mView;

    private LifecycleObserverCompat mLifeObserverCompat;

    private ActionObserverCompat mActionObserverCompat;

    public BasePresenter(@NonNull T view) {
        mView = view;

        mLifeObserverCompat = new LifecycleObserverCompat();
        mActionObserverCompat = new ActionObserverCompat();

        mView.addLifecycleObserver(this);
    }

    @Override
    public void addLifecycleObserver(ILifecycleObserver listener) {
        mLifeObserverCompat.addLifecycleObserver(listener);
    }

    @Override
    public boolean removeLifecycleObserver(ILifecycleObserver listener) {
        return mLifeObserverCompat.removeLifecycleObserver(listener);
    }

    @Override
    public void addActionObserver(IActionObserver listener) {
        mActionObserverCompat.addActionObserver(listener);
    }

    @Override
    public boolean removeActionObserver(IActionObserver listener) {
        return mActionObserverCompat.removeActionObserver(listener);
    }

    @Override
    @CallSuper
    public void onCreate() {
        addAttachViewActionObserver();
        mLifeObserverCompat.onCreate();
    }

    @Override
    @CallSuper
    public void onStart() {
        mLifeObserverCompat.onStart();
    }

    @Override
    @CallSuper
    public void onResume() {
        mLifeObserverCompat.onResume();
    }

    @Override
    @CallSuper
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            removeAttachViewActionObserver();
        } else {
            addAttachViewActionObserver();
        }
        mLifeObserverCompat.onHiddenChanged(hidden);
    }

    @Override
    @CallSuper
    public void onPause() {
        mLifeObserverCompat.onPause();
    }

    @Override
    @CallSuper
    public void onStop() {
        mLifeObserverCompat.onStop();
    }

    @Override
    @CallSuper
    public void onDestroy() {
        removeAttachViewActionObserver();
        mLifeObserverCompat.onDestroy();
    }

    private void addAttachViewActionObserver() {
        if (mView instanceof IActionObservable) {
            ((IActionObservable) mView).addActionObserver(this);
        }
    }

    private void removeAttachViewActionObserver() {
        if (mView instanceof IActionObservable) {
            ((IActionObservable) mView).removeActionObserver(this);
        }
    }

    public final boolean isViewAttached() {
        return mView != null;
    }

    public final T getView() {
        return mView;
    }

    public Context getContext() {
        if (isViewAttached()) {
            if (mView instanceof Activity) {
                return (Context) mView;
            } else if (mView instanceof Fragment) {
                return ((Fragment) mView).getActivity();
            }
            return mView.getContext();
        }
        return null;
    }

    public final String getString(@StringRes int resId) {
        if (getContext() == null) {
            return "";
        }
        return getContext().getString(resId);
    }

    public final String getString(@StringRes int resId, Object... args) {
        if (getContext() == null) {
            return "";
        }
        return getContext().getString(resId, args);
    }

    /**
     * 申请运行时权限
     *
     * @param callback    权限回调,如果在低于6.0(Marshmallow)将直接回调{@link PermissionCallback#onGranted(List)}}
     * @param permissions 权限列表,详见{@link android.Manifest.permission}
     */
    public final void requestPermission(@Nullable final PermissionCallback callback, @PermissionString final String... permissions) {
        if (!(getContext() instanceof IPermissionCompat)) {
            return;
        }
        ((IPermissionCompat) getContext()).requestPermission(callback, permissions);
    }

    /**
     * 检查运行时权限是否授予
     *
     * @param permissions 权限列表,详见{@link android.Manifest.permission}
     * @return true 代表所有权限均已授予,false代表其中有权限没有授予
     */
    public final boolean checkPermissionsIsGranted(String... permissions) {
        if (!(getContext() instanceof IPermissionCompat)) {
            return false;
        }

        return ((IPermissionCompat) getContext()).checkPermissionsIsGranted(permissions);
    }

    @Override
    @CallSuper
    public void onBackPressed() {
        mActionObserverCompat.onBackPressed();
    }

    @Override
    @CallSuper
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mActionObserverCompat.onActivityResult(requestCode, resultCode, data);
    }
}
