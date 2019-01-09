package cn.xuhao.android.lib.activity.permisstion.callback;

import android.support.annotation.NonNull;

import java.util.List;

import cn.xuhao.android.lib.activity.permisstion.AuthorizationInfo;

/**
 * Created by xuhao on 2017/4/10.
 */

public abstract class SimplePermissionCallback implements PermissionCallback {
    @Override
    public void onGranted(@NonNull List<AuthorizationInfo> grantedList) {

    }

    @Override
    public void onRefuse(@NonNull List<AuthorizationInfo> refuseList) {

    }
}
