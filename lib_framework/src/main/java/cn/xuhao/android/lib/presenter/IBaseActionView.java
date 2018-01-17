package cn.xuhao.android.lib.presenter;

import cn.xuhao.android.lib.observer.action.IActionObservable;
import cn.xuhao.android.lib.observer.action.IActionObserver;
import cn.xuhao.android.lib.observer.lifecycle.ILifecycleObserver;

/**
 * 有行为观察的MVPView基类
 * Created by xuhao on 2017/4/7.
 */
public interface IBaseActionView<T extends ILifecycleObserver & IActionObserver>
        extends IBaseView<T>, IActionObservable<T> {
}
