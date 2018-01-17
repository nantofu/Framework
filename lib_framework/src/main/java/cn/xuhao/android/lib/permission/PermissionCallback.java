package cn.xuhao.android.lib.permission;

import android.support.annotation.Nullable;

/**
 * Created by xuhao on 2017/3/18.
 */

public interface PermissionCallback {
    /**
     * 在系统授权框弹出之前
     *
     * @param isShouldShowAlert 是否应该弹出自己的提示框
     * @param request 权限请求控制类
     * @param permissions 权限列表
     */
    void onBeforeGranted(boolean isShouldShowAlert, IPermissionRequest request, String... permissions);

    /**
     * 已授权权限
     *
     * @param permissions 已授权的权限名称
     */
    void onGranted(@Nullable String... permissions);

    /**
     * 拒绝授权的权限
     *
     * @param permissions 拒绝授权的权限名称
     */
    void onRefuse(@Nullable String... permissions);
}
