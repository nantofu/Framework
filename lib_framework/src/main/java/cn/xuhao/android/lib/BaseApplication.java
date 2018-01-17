package cn.xuhao.android.lib;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import java.util.List;

public abstract class BaseApplication extends Application {

    @Override
    public final void onCreate() {
        super.onCreate();

        initAlways();

        if (isMainProcess()) {
            initOnMainProcess();
        } else {
            initOnOtherProcess(getCurrentProcessName(), android.os.Process.myPid());
        }
    }

    private boolean isMainProcess() {
        return getPackageName().equals(getCurrentProcessName());
    }

    protected String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();

        if (runningProcesses != null && !runningProcesses.isEmpty()) {
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.pid == pid) {
                    return processInfo.processName;
                }
            }
        }
        return "";
    }

    protected abstract void initOnMainProcess();

    /**
     * 不管是否主进程都要初始化的东西放在这里
     */
    protected abstract void initAlways();

    @Deprecated
    protected void initOnOtherProcess() {}

    protected abstract void initOnOtherProcess(String processName, int processId);

}
