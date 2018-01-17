package cn.xuhao.android.lib.observer.lifecycle;


/**
 * 生命周期观察者
 */
public interface ILifecycleObservable<T extends ILifecycleObserver> {
    void addLifecycleObserver(T listener);

    boolean removeLifecycleObserver(T listener);
}
