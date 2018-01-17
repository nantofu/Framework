package cn.xuhao.android.lib.observer.action;

import android.content.Intent;

/**
 * Created by xuhao on 2017/4/19.
 */

public interface IActionObserver {
    void onBackPressed();

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
