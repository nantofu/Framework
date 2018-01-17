package cn.xuhao.android.lib.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import cn.xuhao.android.lib.observer.lifecycle.ILifecycleObservable;
import cn.xuhao.android.lib.observer.lifecycle.ILifecycleObserver;
import cn.xuhao.android.lib.observer.lifecycle.LifecycleObserverCompat;

/**
 * Created by xuhao on 15/10/12.
 */
public abstract class BaseDialogFragment extends DialogFragment implements ILifecycleObservable {

    private View root;

    private LifecycleObserverCompat mLifeObserverCompat = new LifecycleObserverCompat();

    private DialogInterface.OnDismissListener mOnDismissListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutRes(), container, false);
        this.root = rootView;
        return rootView;
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isAutoCall()) {
            parseBundle(getArguments());
            findViews();
            initObjects();
            initData();
            setListener();
        }
        mLifeObserverCompat.onCreate();
    }

    @Override
    public final void addLifecycleObserver(@NonNull ILifecycleObserver observer) {
        mLifeObserverCompat.addLifecycleObserver(observer);
    }

    @Override
    public final boolean removeLifecycleObserver(ILifecycleObserver observer) {
        return mLifeObserverCompat.removeLifecycleObserver(observer);
    }

    /**
     * 查找当前Fragment中的view对象
     *
     * @param resId 需要查找的View对象的ID值
     * @return 找到的View对象, 可能为空
     */
    @Nullable
    public final <T extends View> T findViewById(@IdRes int resId) {
        return (T) root.findViewById(resId);
    }

    /**
     * 视图主布局,用于包含InnerLayout
     *
     * @return 布局ResID
     */
    @LayoutRes
    protected abstract int getLayoutRes();

    /**
     * 处理传入参数,初始化传入参数
     *
     * @param bundle bundle参数,可能为空
     */
    protected abstract void parseBundle(Bundle bundle);

    /**
     * 初始化所有的view从ContentLayout中
     */
    protected abstract void findViews();

    /**
     * 初始化所有的非View对象,例如Adapter,Fragment,Manager,Presenter等相关对象
     */
    protected abstract void initObjects();

    /**
     * 初始化页面上的数据,可以设置相关view的显示方式,静态数据,或者发起网络请求,数据库加载等
     */
    protected abstract void initData();

    /**
     * 设置所有的监听器,广播接收器等相关回调
     */
    protected abstract void setListener();

    /**
     * 是否由父类控制基础方法的调用顺序
     * 基础方法包括
     * <p>
     * parseBundle(Bundle);
     * findViews();
     * initObjects();
     * initData();
     * setListener();
     * </p>
     *
     * @return true则由基类控制以上基础方法调用顺序, false反之, 基类将不会主动调用
     */
    protected boolean isAutoCall() {
        return true;
    }

    /**
     * 通过此方法获得该Fragment的Title
     *
     * @return 该fragment具有的业务属性字符串, 将会用做fragment的唯一标示
     */
    public String getTitle() {
        return this.getClass().getSimpleName();
    }

    /**
     * 设置对话框占屏百分比宽度
     *
     * @param percent 百分比(0 ~ 1)之间的数字
     * @return true成功, false失败
     */
    protected boolean setDialogSizePercent(float percent) {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return setDialogSizeExactly((int) (dm.widthPixels * percent), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 设置对话框精确宽度像素值
     *
     * @param width 像素值
     * @return true成功, false失败
     */
    protected boolean setDialogSizeExactly(int width, int height) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width, height);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置对话框显示位置
     *
     * @param gravity 显示位置常量{@link android.view.Gravity}
     * @return true成功, false失败
     */
    protected boolean setDialogGravity(int gravity) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = gravity;
            window.setAttributes(lp);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(cancelable);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mLifeObserverCompat.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mLifeObserverCompat.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLifeObserverCompat.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLifeObserverCompat.onStop();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
        destroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroy();
    }

    public boolean isShowing() {
        if (getDialog() != null) {
            return getDialog().isShowing();
        }
        return false;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        manager.executePendingTransactions();
        if (!isAdded()) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        if (!isAdded()) {
            return super.show(transaction, tag);
        }
        return -1;
    }

    private void destroy() {
        mLifeObserverCompat.onDestroy();
    }
}
