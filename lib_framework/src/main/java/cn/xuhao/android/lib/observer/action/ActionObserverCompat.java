package cn.xuhao.android.lib.observer.action;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhao on 2017/4/19.
 */

public final class ActionObserverCompat implements IActionObservable, IActionObserver {

    private final List<IActionObserver> mObserverList = new ArrayList<>();

    @Override
    public void addActionObserver(IActionObserver observer) {
        synchronized (mObserverList) {
            mObserverList.add(observer);
        }
    }

    @Override
    public boolean removeActionObserver(IActionObserver observer) {

        return mObserverList.remove(observer);
    }

    @Override
    public void onBackPressed() {
        for (IActionObserver observer : mObserverList) {
            observer.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (IActionObserver observer : mObserverList) {
            observer.onActivityResult(requestCode, resultCode, data);
        }
    }
}
