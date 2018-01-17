package cn.xuhao.android.lib.widget.topbarview.adapter;

import android.app.Activity;
import android.database.Observable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import cn.xuhao.android.lib.MyObserver;
import cn.xuhao.android.lib.widget.topbarview.TopbarIndex;


/**
 * Created by xuhao on 15/10/13.
 */
public abstract class BaseTopBarAdapter extends Observable<MyObserver> {
    protected Activity mActivity;

    public BaseTopBarAdapter(Activity activity) {
        mActivity = activity;
    }

    public abstract View getLeftView(View convertView, ViewGroup parent);

    public abstract View getCenterView(View convertView, ViewGroup parent);

    public abstract View getRightView(View convertView, ViewGroup parent);

    public void notifyDataSetChanged() {
        for (MyObserver observer : mObservers) {
            observer.onChanged(-1);
            observer.onInvalidated(-1);
        }
    }

    public void notifyItemChanged(@TopbarIndex int index) {
        for (MyObserver observer : mObservers) {
            observer.onChanged(index);
            observer.onInvalidated(index);
        }
    }

    protected int dip2px(int dip) {
        if (mActivity == null || mActivity.getResources() == null) {
            return 0;
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, mActivity.getResources()
                .getDisplayMetrics());
    }

}
