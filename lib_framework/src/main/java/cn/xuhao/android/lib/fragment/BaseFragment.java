package cn.xuhao.android.lib.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.xuhao.android.lib.observer.action.ActionObserverCompat;
import cn.xuhao.android.lib.observer.action.IActionObservable;
import cn.xuhao.android.lib.observer.action.IActionObserver;
import cn.xuhao.android.lib.observer.lifecycle.ILifecycleObservable;
import cn.xuhao.android.lib.observer.lifecycle.ILifecycleObserver;
import cn.xuhao.android.lib.observer.lifecycle.LifecycleObserverCompat;

/**
 * Created by xuhao on 15/10/12.
 */
public abstract class BaseFragment extends Fragment implements ILifecycleObservable, IActionObservable,
        IActionObserver {

    private View root;

    private LifecycleObserverCompat mLifeObserverCompat = new LifecycleObserverCompat();

    private ActionObserverCompat mActionObserverCompat = new ActionObserverCompat();

    protected Activity mActivity;

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutRes(), container, false);
        this.root = rootView;
        return rootView;
    }

    @Override
    public final void onViewCreated(View view, Bundle savedInstanceState) {
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
    public final void addLifecycleObserver(ILifecycleObserver observer) {
        mLifeObserverCompat.addLifecycleObserver(observer);
    }

    @Override
    public final boolean removeLifecycleObserver(ILifecycleObserver observer) {
        return mLifeObserverCompat.removeLifecycleObserver(observer);
    }

    @Override
    public void addActionObserver(IActionObserver listener) {
        mActionObserverCompat.addActionObserver(listener);
    }

    @Override
    public boolean removeActionObserver(IActionObserver listener) {
        return mActionObserverCompat.removeActionObserver(listener);
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            removeParentActionObserver();
        } else {
            addParentActionObserver();
        }
        mLifeObserverCompat.onHiddenChanged(hidden);
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
    public void onDestroy() {
        super.onDestroy();
        mLifeObserverCompat.onDestroy();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = getActivity();
        addParentActionObserver();
    }

    @Override
    public void onDetach() {
        removeParentActionObserver();
        super.onDetach();
        mActivity = null;
    }

    /**
     * 添加父行为观察者
     */
    protected void addParentActionObserver() {//如果可以,观察父级的行为
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof IActionObservable) {//父Fragment为空,说明父为Activty
            ((IActionObservable) parentFragment).addActionObserver(this);
        } else if (mActivity instanceof IActionObservable) {
            ((IActionObservable) mActivity).addActionObserver(this);
        }
    }

    /**
     * 删除父行为观察者
     */
    protected void removeParentActionObserver() {//如果可以,取消观察父级行为
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof IActionObservable) {
            ((IActionObservable) parentFragment).removeActionObserver(this);
        } else if (mActivity instanceof IActionObservable) {
            ((IActionObservable) mActivity).removeActionObserver(this);
        }
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mActionObserverCompat.onActivityResult(requestCode, resultCode, data);
//        L.e("Action", getClass().getSimpleName() + "->onActivityResult" + " requestCode:" + requestCode);
    }

    @Override
    @CallSuper
    public void onBackPressed() {
        mActionObserverCompat.onBackPressed();
//        L.e("Action", getClass().getSimpleName() + "->onBackPressed");
    }
}
