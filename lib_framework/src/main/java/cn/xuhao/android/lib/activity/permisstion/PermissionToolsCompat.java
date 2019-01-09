package cn.xuhao.android.lib.activity.permisstion;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.xuhao.android.lib.activity.permisstion.callback.IPermissionRequest;
import cn.xuhao.android.lib.activity.permisstion.callback.PermissionCallback;
import cn.xuhao.android.lib.activity.permisstion.callback.PermissionString;
import cn.xuhao.android.lib.activity.permisstion.utils.ManufacturerUtils;

public class PermissionToolsCompat {

    private Activity mActivity;

    private final SparseArray<PermissionCallback> mCallbackArray = new SparseArray<>();

    private volatile int mRequestCode = 9999;

    private final SparseArray<Map<String, AuthorizationInfo>> mInfoArray = new SparseArray<>();

    private SharedPreferences mSp;

    public PermissionToolsCompat(Activity activity) {
        mActivity = activity;
        mSp = mActivity.getSharedPreferences("permission_toools_sp", Activity.MODE_PRIVATE);
    }

    public void requestPermission(@Nullable final PermissionCallback callback,
                                  @PermissionString final String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return;
        }
        final Map<String, AuthorizationInfo> authorizationInfos = getAuthorizationInfoList(permissions);
        List<AuthorizationInfo> defaultGrantedList = getDefaultInfoList(authorizationInfos);
        //先判断是否需要申请权限
        boolean needGranted = false;
        //先判断系统版本
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (callback != null) {
                callback.onGranted(defaultGrantedList);
            }
            return;
        }
        //判断权限
        needGranted = !checkPermissionsIsGranted(permissions);
        if (needGranted) {//申请权限
            for (String permission : permissions) {
                //第一次false,如果拒绝,第二次true,如果未点击不再显示,以后都是true直到点击授权或者不再显示之后才会一直是false
                boolean shouldOrNot = ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission);
                authorizationInfos.get(permission).setShouldShowAlert(shouldOrNot);
            }

            IPermissionRequest request = new IPermissionRequest() {
                @Override
                public void proceed() {
                    for (String permission : authorizationInfos.keySet()) {
                        int time = mSp.getInt(permission, 0);
                        time++;
                        mSp.edit().putInt(permission, time).commit();
                        authorizationInfos.get(permission).setRequestTimes(time);
                    }
                    synchronized (mCallbackArray) {
                        mCallbackArray.put(mRequestCode, callback);
                        mInfoArray.put(mRequestCode, authorizationInfos);
                    }
                    ActivityCompat.requestPermissions(mActivity, permissions, mRequestCode);
                    mRequestCode++;
                }
            };


            if (callback != null) {//根据返回值判断是否申请
                callback.onBeforeGranted(turnToInfoList(authorizationInfos), request);
            } else {//直接进行申请
                request.proceed();
            }
        } else {
            if (callback != null) {
                callback.onGranted(defaultGrantedList);
            }
        }
    }

    @NonNull
    private List<AuthorizationInfo> turnToInfoList(Map<String, AuthorizationInfo> authorizationInfos) {
        List<AuthorizationInfo> defaultGrantedList = new ArrayList<>();
        Iterator<String> it = authorizationInfos.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            AuthorizationInfo info = authorizationInfos.get(key);
            defaultGrantedList.add(info);
        }
        return defaultGrantedList;
    }

    @NonNull
    private List<AuthorizationInfo> getDefaultInfoList(Map<String, AuthorizationInfo> authorizationInfos) {
        List<AuthorizationInfo> defaultGrantedList = new ArrayList<>();
        Iterator<String> it = authorizationInfos.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            AuthorizationInfo info = authorizationInfos.get(key);
            info.setDoNotAskAgain(false);
            info.setShouldShowAlert(false);
            info.setAuthorization(true);
            defaultGrantedList.add(info);
        }
        return defaultGrantedList;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionCallback callback = mCallbackArray.get(requestCode);
        if (callback != null) {
            //授权与拒绝分类
            List<AuthorizationInfo> grantedList = new ArrayList<>();
            List<AuthorizationInfo> refuseList = new ArrayList<>();
            Map<String, AuthorizationInfo> authorizationInfoMap = mInfoArray.get(requestCode);
            for (int i = 0; i < grantResults.length && i < permissions.length; i++) {
                int grantResult = grantResults[i];
                String permission = permissions[i];
                AuthorizationInfo info = authorizationInfoMap.get(permission);
                if (grantResult == PackageManager.PERMISSION_GRANTED) {//授权
                    info.setAuthorization(true);
                    info.setDoNotAskAgain(false);
                    grantedList.add(info);
                } else {//拒绝
                    boolean isShouldShowDialog = ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission);
                    info.setAuthorization(false);
                    switch (info.getRequestTimes()) {//第一次二次请求被拒绝,不再显示为false
                        case 1:
                            info.setDoNotAskAgain(false);
                            break;
                        default://第2次及以后根据是否显示引导框判断
                            if (!isShouldShowDialog) {
                                info.setDoNotAskAgain(true);
                                info.setShouldShowAlert(false);
                            } else {
                                info.setDoNotAskAgain(false);
                            }
                    }
                    refuseList.add(info);
                }
            }
            if (!grantedList.isEmpty()) {
                callback.onGranted(grantedList);
            }
            if (!refuseList.isEmpty()) {
                callback.onRefuse(refuseList);
            }
        }
        synchronized (mCallbackArray) {
            mCallbackArray.remove(requestCode);
            mInfoArray.remove(requestCode);
        }
    }

    @NonNull
    private Map<String, AuthorizationInfo> getAuthorizationInfoList(@PermissionString String[] permissions) {
        Map<String, AuthorizationInfo> authorizationInfos = new HashMap();
        for (String permission : permissions) {
            AuthorizationInfo info = new AuthorizationInfo();
            info.setRequestPermission(permission);
            info.setRequestTimes(mSp.getInt(permission, 0));
            authorizationInfos.put(permission, info);
        }
        return authorizationInfos;
    }

    /**
     * 检查权限是否已经授权
     *
     * @param permissionGroup
     * @return true已授权, false有未授权的权限
     */
    public boolean checkPermissionsIsGranted(String... permissionGroup) {
        if (permissionGroup != null) {
            for (String permission : permissionGroup) {
                if (ActivityCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 打开手机的权限管理页面
     */
    public void launchPermissionSettingPageOnThePhone(Context context) {
        Intent intent = new Intent();
        if (ManufacturerUtils.isSamsung()) {
            goSamsung(context, intent);
        } else if (ManufacturerUtils.isMotorola()) {
            goMotorola(context, intent);
        } else if (ManufacturerUtils.isHuawei()) {
            goHuawei(context, intent);
        } else if (ManufacturerUtils.isLG()) {
            goLG(context, intent);
        } else if (ManufacturerUtils.isMIUI()) {
            goXiaomi(context, intent);
        } else if (ManufacturerUtils.isSony()) {
            goSony(context, intent);
        } else if (ManufacturerUtils.isFlyme()) {
            goMeizu(context, intent);
        } else {
            goSetting(context);
        }
    }

    private void goSamsung(Context context, Intent intent) {
        goInstalledAppDetail(context, intent);
    }

    private void goMotorola(Context context, Intent intent) {
        goInstalledAppDetail(context, intent);
    }

    private void goLG(Context context, Intent intent) {
        try {
            intent.setAction(context.getPackageName());
            ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
            intent.setComponent(comp);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            goInstalledAppDetail(context, new Intent());
        }

    }

    private void goHuawei(Context context, Intent intent) {
        // 华为跳转权限管理页面异常，所以跳转到详情页
        goInstalledAppDetail(context, intent);
    }

    private void goMeizu(Context context, Intent intent) {
        try {
            intent.setAction("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            goInstalledAppDetail(context, new Intent());
        }
    }

    /**
     * 获取 MIUI 版本号
     */
    private String getMiuiVersion() {
        String propName = "ro.miui.ui.version.name";
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(
                    new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }


    private void goXiaomi(Context context, Intent intent) {
        String rom = getMiuiVersion();
        if ("V6".equals(rom) || "V7".equals(rom)) {
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if ("V8".equals(rom)) {
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            goInstalledAppDetail(context, intent);
        }

    }

    private void goSony(Context context, Intent intent) {
        try {
            intent.setAction(context.getPackageName());
            ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
            intent.setComponent(comp);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            goInstalledAppDetail(context, new Intent());
        }
    }

    private void goInstalledAppDetail(Context context, Intent intent) {
        try {
            //调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
            final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
            //调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
            final String APP_PKG_NAME_22 = "pkg";
            //InstalledAppDetails所在包名
            final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
            //InstalledAppDetails类名
            final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

            final int apiLevel = Build.VERSION.SDK_INT;
            if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
            } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
                // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
                final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                        APP_DETAILS_CLASS_NAME);
                intent.putExtra(appPkgName, context.getPackageName());
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            goSetting(context);
        }

    }

    private void goSetting(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
