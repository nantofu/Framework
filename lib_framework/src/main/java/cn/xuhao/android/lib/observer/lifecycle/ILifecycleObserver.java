package cn.xuhao.android.lib.observer.lifecycle;

import java.io.Serializable;

/**
 * 生命周期监听器,监听对象的声明周期
 */
public interface ILifecycleObserver extends Serializable {

    void onCreate();

    void onDestroy();
}
