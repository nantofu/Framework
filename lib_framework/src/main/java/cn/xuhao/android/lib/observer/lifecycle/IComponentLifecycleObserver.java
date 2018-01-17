package cn.xuhao.android.lib.observer.lifecycle;

/**
 * Created by xuhao on 2017/4/19.
 */

public interface IComponentLifecycleObserver extends ILifecycleObserver {
    void onResume();

    void onStart();

    void onHiddenChanged(boolean hidden);

    void onPause();

    void onStop();
}
