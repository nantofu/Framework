package cn.xuhao.android.lib.activity.permisstion.callback;

import android.support.annotation.NonNull;

import java.util.List;

import cn.xuhao.android.lib.activity.permisstion.AuthorizationInfo;

/**
 * Created by xuhao on 2017/3/18.
 */

public interface PermissionCallback {
    /**
     * 在系统授权框弹出之前
     *
     * @param requestList 申请的权限信息列表
     * @param request     权限请求控制类
     */
    void onBeforeGranted(@NonNull List<AuthorizationInfo> requestList, @NonNull IPermissionRequest request);

    /**
     * 已授权权限
     *
     * @param grantedList 已授权的权限信息
     */
    void onGranted(@NonNull List<AuthorizationInfo> grantedList);

    /**
     * 拒绝授权的权限
     *
     * @param refuseList 拒绝授权的权限信息
     */
    void onRefuse(@NonNull List<AuthorizationInfo> refuseList);
}
