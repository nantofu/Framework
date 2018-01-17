package cn.xuhao.android.lib;

import android.support.annotation.StringRes;

/**
 * Created by xuhao on 15/10/29.
 */
public interface UIStuff {

    void showPDialog();

    void showPDialog(String msg);

    void showPDialog(String msg, boolean cancelable);

    void closePDialog();

    boolean isWaitDialogShowing();

    void showToast(String msg);

    void showToast(@StringRes int resId);

    void showToast(@StringRes int resId, int time);

    void showToast(String msg, int time);

}
