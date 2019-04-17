package cn.xuhao.android.lib.activity.permisstion;

import android.support.annotation.Nullable;

import java.util.List;

import cn.xuhao.android.lib.activity.permisstion.callback.PermissionCallback;
import cn.xuhao.android.lib.activity.permisstion.callback.PermissionString;

public interface IPermissionCompat {

    /**
     * 申请运行时权限
     *
     * @param callback    权限回调,如果在低于6.0(Marshmallow)将直接回调{@link PermissionCallback#onGranted(List)}}
     * @param permissions 权限列表,详见{@link android.Manifest.permission}
     */
    void requestPermission(@Nullable final PermissionCallback callback, @PermissionString final String... permissions);

    /**
     * 检查运行时权限是否授予
     *
     * @param permissions 权限列表,详见{@link android.Manifest.permission}
     * @return true 代表所有权限均已授予,false代表其中有权限没有授予
     */
    boolean checkPermissionsIsGranted(String... permissions);
}
