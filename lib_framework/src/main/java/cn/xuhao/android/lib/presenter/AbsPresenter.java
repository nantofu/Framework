package cn.xuhao.android.lib.presenter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import cn.xuhao.android.lib.UIStuff;
import cn.xuhao.android.lib.http.PaxOk;
import cn.xuhao.android.lib.observer.action.ActionObserverCompat;
import cn.xuhao.android.lib.observer.action.IActionObservable;
import cn.xuhao.android.lib.observer.action.IActionObserver;
import cn.xuhao.android.lib.observer.lifecycle.IComponentLifecycleObserver;
import cn.xuhao.android.lib.observer.lifecycle.ILifecycleObservable;
import cn.xuhao.android.lib.observer.lifecycle.ILifecycleObserver;
import cn.xuhao.android.lib.observer.lifecycle.LifecycleObserverCompat;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author xuhao
 */

public abstract class AbsPresenter<T extends IBaseView>
        implements IComponentLifecycleObserver, ILifecycleObservable, IActionObservable, UIStuff, IActionObserver {

    protected T mView;

    private CompositeSubscription compositeSubscription;

    private LifecycleObserverCompat mLifeObserverCompat;

    private ActionObserverCompat mActionObserverCompat;

    public AbsPresenter(@NonNull T view) {
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
        unSubscribe();
        PaxOk.getInstance().cancelTag(this);
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

    public final void addSubscribe(Subscription subscription) {
        if (compositeSubscription == null) {
            compositeSubscription = new CompositeSubscription();
        }
        compositeSubscription.add(subscription);
    }

    public final void unSubscribe() {
        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
        }
    }

    @Override
    public final void showPDialog() {
        if (mView instanceof UIStuff) {
            ((UIStuff) mView).showPDialog();
        }
    }

    @Override
    public final void showPDialog(String msg) {
        if (mView instanceof UIStuff) {
            ((UIStuff) mView).showPDialog(msg);
        }
    }

    @Override
    public final void showPDialog(String msg, boolean cancelable) {
        if (mView instanceof UIStuff) {
            ((UIStuff) mView).showPDialog(msg, cancelable);
        }
    }

    @Override
    public final void closePDialog() {
        if (mView instanceof UIStuff) {
            ((UIStuff) mView).closePDialog();
        }
    }

    @Override
    public final boolean isWaitDialogShowing() {
        if (mView instanceof UIStuff) {
            return ((UIStuff) mView).isWaitDialogShowing();
        }
        return false;
    }

    @Override
    public final void showToast(String msg) {
        if (mView instanceof UIStuff) {
            ((UIStuff) mView).showToast(msg);
        }
    }

    @Override
    public void showToast(@StringRes int resId) {
        if (mView instanceof UIStuff) {
            ((UIStuff) mView).showToast(resId);
        }
    }

    @Override
    public void showToast(@StringRes int resId, int time) {
        if (mView instanceof UIStuff) {
            ((UIStuff) mView).showToast(resId, time);
        }
    }

    @Override
    public final void showToast(String msg, int time) {
        if (mView instanceof UIStuff) {
            ((UIStuff) mView).showToast(msg, time);
        }
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
