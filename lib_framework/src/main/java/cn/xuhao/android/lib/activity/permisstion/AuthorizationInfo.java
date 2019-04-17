package cn.xuhao.android.lib.activity.permisstion;

import java.io.Serializable;
import java.util.Objects;

public class AuthorizationInfo implements Serializable, Cloneable {
    /**
     * 请求的权限名称
     */
    private String mRequestPermission;
    /**
     * 是否授权
     */
    private boolean mAuthorization;
    /**
     * 请求次数
     */
    private int mRequestTimes;
    /**
     * 是否勾选再也不询问
     */
    private boolean isDoNotAskAgain;
    /**
     * 是否应该进行权限申请解释,和引导的弹框
     */
    private boolean isShouldShowAlert;

    public String getRequestPermission() {
        return mRequestPermission;
    }

    public void setRequestPermission(String requestPermission) {
        mRequestPermission = requestPermission;
    }

    public boolean isAuthorization() {
        return mAuthorization;
    }

    public void setAuthorization(boolean authorization) {
        mAuthorization = authorization;
    }

    public int getRequestTimes() {
        return mRequestTimes;
    }

    public boolean isShouldShowAlert() {
        return isShouldShowAlert;
    }

    public void setShouldShowAlert(boolean shouldShowAlert) {
        isShouldShowAlert = shouldShowAlert;
    }

    public void setRequestTimes(int requestTimes) {
        mRequestTimes = requestTimes;
    }

    public boolean isDoNotAskAgain() {
        return isDoNotAskAgain;
    }

    public void setDoNotAskAgain(boolean doNotAskAgain) {
        isDoNotAskAgain = doNotAskAgain;
    }

    @Override
    public AuthorizationInfo clone() {
        AuthorizationInfo authorizationInfo = new AuthorizationInfo();
        authorizationInfo.setAuthorization(mAuthorization);
        authorizationInfo.setDoNotAskAgain(isDoNotAskAgain);
        authorizationInfo.setRequestPermission(mRequestPermission);
        authorizationInfo.setRequestTimes(mRequestTimes);
        return authorizationInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizationInfo that = (AuthorizationInfo) o;
        return Objects.equals(mRequestPermission, that.mRequestPermission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mRequestPermission);
    }

    @Override
    public String toString() {
        return "AuthorizationInfo{" +
                "mRequestPermission='" + mRequestPermission + '\'' +
                ", mAuthorization=" + mAuthorization +
                ", mRequestTimes=" + mRequestTimes +
                ", isDoNotAskAgain=" + isDoNotAskAgain +
                ", isShouldShowAlert=" + isShouldShowAlert +
                '}';
    }
}
