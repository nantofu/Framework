package cn.xuhao.android.lib.mvp;

import android.content.Context;

import cn.xuhao.android.lib.observer.lifecycle.ILifecycleObservable;
import cn.xuhao.android.lib.observer.lifecycle.ILifecycleObserver;

/**
 * Created by xuhao on 2017/4/7.
 */

public interface IBaseView<T extends ILifecycleObserver> extends ILifecycleObservable<T> {
    Context getContext();
}
