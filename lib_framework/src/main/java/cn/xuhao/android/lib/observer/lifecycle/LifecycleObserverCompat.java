package cn.xuhao.android.lib.observer.lifecycle;

import android.support.annotation.MainThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhao on 2017/4/19.
 */

public final class LifecycleObserverCompat implements ILifecycleObservable, IComponentLifecycleObserver {

    private boolean mIsCreated = false;

    private boolean mIsStarted = false;

    private boolean mIsDestroy = false;

    private boolean mIsResumed = false;

    private boolean mIsHidden = false;

    private final List<ILifecycleObserver> mObserverList = new ArrayList<>();

    @Override
    public void addLifecycleObserver(ILifecycleObserver observer) {
        synchronized (mObserverList) {
            mObserverList.add(observer);
            if (observer instanceof IComponentLifecycleObserver) {
                addComponentObserver((IComponentLifecycleObserver) observer);
            } else {
                addBaseObserver(observer);
            }
        }
    }

    private void addComponentObserver(IComponentLifecycleObserver observer) {
        if (!mIsDestroy) {
            if (mIsCreated) {
                observer.onCreate();
            }
            if (mIsStarted) {
                observer.onStart();
            }
            if (mIsResumed) {
                observer.onResume();
            }
            if (mIsHidden) {
                observer.onHiddenChanged(true);
            }
        } else {
            if (!mIsResumed) {
                observer.onPause();
            }
            if (!mIsStarted) {
                observer.onStop();
            }
            observer.onDestroy();
            mObserverList.remove(observer);
        }
    }

    private void addBaseObserver(ILifecycleObserver observer) {
        if (!mIsDestroy) {
            if (mIsCreated) {
                observer.onCreate();
            }
        } else {
            observer.onDestroy();
            mObserverList.remove(observer);
        }
    }

    @Override
    public boolean removeLifecycleObserver(ILifecycleObserver listener) {
        synchronized (mObserverList) {
            return mObserverList.remove(listener);
        }
    }

    @Override
    @MainThread
    public void onCreate() {
        mIsDestroy = false;
        mIsCreated = true;
        for (ILifecycleObserver observer : mObserverList) {
            observer.onCreate();
        }
    }

    @Override
    @MainThread
    public void onStart() {
        for (ILifecycleObserver observer : mObserverList) {
            if (observer instanceof IComponentLifecycleObserver) {
                ((IComponentLifecycleObserver) observer).onStart();
            }
        }
        mIsStarted = true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        for (ILifecycleObserver observer : mObserverList) {
            if (observer instanceof IComponentLifecycleObserver) {
                ((IComponentLifecycleObserver) observer).onHiddenChanged(hidden);
            }
        }
        mIsHidden = hidden;
    }

    @Override
    @MainThread
    public void onResume() {
        for (ILifecycleObserver observer : mObserverList) {
            if (observer instanceof IComponentLifecycleObserver) {
                ((IComponentLifecycleObserver) observer).onResume();
            }
        }
        mIsResumed = true;
    }

    @Override
    @MainThread
    public void onPause() {
        for (ILifecycleObserver observer : mObserverList) {
            if (observer instanceof IComponentLifecycleObserver) {
                ((IComponentLifecycleObserver) observer).onPause();
            }
        }
        mIsResumed = false;
    }

    @Override
    @MainThread
    public void onStop() {
        for (ILifecycleObserver observer : mObserverList) {
            if (observer instanceof IComponentLifecycleObserver) {
                ((IComponentLifecycleObserver) observer).onStop();
            }
        }
        mIsStarted = false;
    }

    @Override
    @MainThread
    public void onDestroy() {
        mIsCreated = false;
        mIsDestroy = true;
        for (ILifecycleObserver observer : mObserverList) {
            observer.onDestroy();
        }
        mObserverList.clear();
    }
}
