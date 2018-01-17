package cn.xuhao.android.lib.permission;

import android.support.annotation.Nullable;

/**
 * Created by xuhao on 2017/4/10.
 */

public abstract class SimplePermissionCallback implements PermissionCallback {
    @Override
    public void onGranted(@Nullable String... permissions) {

    }

    @Override
    public void onRefuse(@Nullable String... permissions) {

    }
}
