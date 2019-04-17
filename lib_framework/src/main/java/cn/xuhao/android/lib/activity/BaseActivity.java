package cn.xuhao.android.lib.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import cn.xuhao.android.lib.activity.permisstion.IPermissionCompat;
import cn.xuhao.android.lib.activity.permisstion.PermissionCompat;
import cn.xuhao.android.lib.activity.permisstion.callback.PermissionCallback;
import cn.xuhao.android.lib.activity.permisstion.callback.PermissionString;
import cn.xuhao.android.lib.observer.action.ActionObserverCompat;
import cn.xuhao.android.lib.observer.action.IActionObservable;
import cn.xuhao.android.lib.observer.action.IActionObserver;
import cn.xuhao.android.lib.observer.lifecycle.ILifecycleObservable;
import cn.xuhao.android.lib.observer.lifecycle.ILifecycleObserver;
import cn.xuhao.android.lib.observer.lifecycle.LifecycleObserverCompat;

/**
 * Created by xuhao on 15/10/12.
 */
public abstract class BaseActivity extends AppCompatActivity implements ILifecycleObservable, IActionObservable, IPermissionCompat {

    protected View mInnerLayout = null;

    protected LayoutInflater mInflater = null;

    private PermissionCompat mPermissionCompat;

    private boolean mIsDestory;

    private LifecycleObserverCompat mLifeObserverCompat = null;

    private ActionObserverCompat mActionObserverCompat = null;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {//防止重建时导致fragment恢复
            String FRAGMENTS_TAG = "android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        initBaseSelf();
        super.onCreate(savedInstanceState);
        beforeContentViewLoaded();
        inflateContentView();
        onContentViewLoaded();
        onInstanceStateRestore(savedInstanceState);

        if (isAutoCall()) {
            parseBundle(getIntent().getExtras());
            findViews();
            initObjects();
            initData();
            setListener();
        }
        mLifeObserverCompat.onCreate();
    }

    @CallSuper
    protected void initBaseSelf() {
        mPermissionCompat = new PermissionCompat(this);
        mActionObserverCompat = new ActionObserverCompat();
        mLifeObserverCompat = new LifecycleObserverCompat();
    }

    @Override
    protected final void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            parseBundle(intent.getExtras());
        }
    }

    /**
     * 注入业务布局
     */
    protected void inflateContentView() {
        mInflater = LayoutInflater.from(this);
        setContentView(getContentLayoutResId());
    }

    /**
     * 将在{@link AppCompatActivity#setContentView(int)}之前调用<br>
     * 可以设置一些关于本Window相关的设置,详见{@link #requestWindowFeature(int)}方法
     */
    protected void beforeContentViewLoaded() {

    }

    /**
     * 将在{@link AppCompatActivity#setContentView(int)}之后调用<br>
     * 可以进行相关的基础页面控件的初始化,例如TopBar等
     */
    protected void onContentViewLoaded() {

    }

    /**
     * 状态存储恢复方法,详情请参阅{@link #onSaveInstanceState(Bundle)}
     *
     * @param savedInstanceState 需要回复的Bundle,可能为空
     */
    protected void onInstanceStateRestore(@Nullable Bundle savedInstanceState) {

    }

    /**
     * 视图主布局,用于包含InnerLayout
     *
     * @return 布局ResID
     */
    @LayoutRes
    protected abstract int getContentLayoutResId();

    /**
     * 初始化所有的view从ContentLayout中
     */
    protected abstract void findViews();

    /**
     * 处理传入参数,初始化传入参数
     *
     * @param bundle bundle参数,可能为空
     */
    protected abstract void parseBundle(@Nullable Bundle bundle);

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
     * 获取Activity对象,返回当前的Activity实例
     *
     * @return 返回当前的Activity实例
     */
    public Context getContext() {
        return this;
    }

    /**
     * 添加生命周期观察者
     *
     * @param observer 生命周期观察者
     */
    @Override
    public final void addLifecycleObserver(@NonNull ILifecycleObserver observer) {
        mLifeObserverCompat.addLifecycleObserver(observer);
    }

    /**
     * 移除生命周期观察者
     *
     * @param observer 生命周期观察者
     */
    @Override
    public final boolean removeLifecycleObserver(@NonNull ILifecycleObserver observer) {
        return mLifeObserverCompat.removeLifecycleObserver(observer);
    }

    /**
     * 添加行为观察者
     *
     * @param listener
     */
    @Override
    public void addActionObserver(IActionObserver listener) {
        mActionObserverCompat.addActionObserver(listener);
    }

    /**
     * 移除行为观察者
     *
     * @param listener
     */
    @Override
    public boolean removeActionObserver(IActionObserver listener) {
        return mActionObserverCompat.removeActionObserver(listener);
    }

    /**
     * 申请运行时权限
     *
     * @param callback    权限回调,如果在低于6.0(Marshmallow)将直接回调{@link PermissionCallback#onGranted(List)}}
     * @param permissions 权限列表,详见{@link android.Manifest.permission}
     */
    public final void requestPermission(@Nullable final PermissionCallback callback,
                                        @PermissionString final String... permissions) {
        mPermissionCompat.requestPermission(callback, permissions);
    }

    /**
     * 检查权限是否已经授权
     *
     * @param permissionGroup
     * @return true已授权, false有未授权的权限
     */
    public final boolean checkPermissionsIsGranted(String... permissionGroup) {
        return mPermissionCompat.checkPermissionsIsGranted(permissionGroup);
    }

    @Override
    @CallSuper
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionCompat.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        mLifeObserverCompat.onStart();
    }

    @Override
    @CallSuper
    protected void onPostResume() {
        super.onPostResume();
        mLifeObserverCompat.onResume();
    }

    @Override
    @CallSuper
    protected void onPause() {
        super.onPause();
        mLifeObserverCompat.onPause();
    }

    @Override
    @CallSuper
    protected void onStop() {
        super.onStop();
        mLifeObserverCompat.onStop();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        mIsDestory = true;
        super.onDestroy();
        mLifeObserverCompat.onDestroy();
    }

    public boolean isDestroyed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return super.isDestroyed();
        } else {
            return mIsDestory;
        }
    }

    @Override
    public final void onBackPressed() {
        if (onBackPressedCall()) {
            super.onBackPressed();
        }
        mActionObserverCompat.onBackPressed();
    }

    /**
     * onBackPressed回调方法,取代了系统的onBackPressed
     *
     * @return true表示需要调用系统的onBackPressed, false反之
     */
    protected boolean onBackPressedCall() {
        return true;
    }

    /**
     * startForResult的回调方法,取代了系统的OnActivityResult
     *
     * @param requestCode       与系统相同
     * @param resultCode        与系统相同
     * @param data              与系统相同
     * @param isFragmentSponsor 发起这次StartForResult的是否是fragment
     * @return true表示消费了此次回调, 将不会通知Fragment及子Fragment.false反之
     */
    @CallSuper
    protected boolean onActivityResult(int requestCode, int resultCode, Intent data, boolean isFragmentSponsor) {
        return false;
    }

    /**
     * 不能复写系统的OnActivityResult,因为框架需要进行顺序调用,请使用{@link #onActivityResult(int, int, Intent, boolean)}
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected final void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int requestIndex = requestCode >> 16;

        if (requestIndex == 0) {
            // 说明是Activity发起的StartForResult,fragment是不会收到回调的
            // 但是普遍程序员认为会收到,我们打破安卓的规则,按照普遍认知,
            // 回调fragment的OnActivityResult
            // 优先调用  onActivityResult(requestCode, resultCode, data, false);保证activity优先于Fragment的调用顺序
            if (!onActivityResult(requestCode, resultCode, data, false)) {
                mActionObserverCompat.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            //这里不要进行行为观察者回调,因为第一层Fragment会收到系统的OnActivityResult回调,这里添加观察者会回调多次
            //这次StartForResult是Fragment发起的
            onActivityResult(requestCode, resultCode, data, true);
        }

    }
}
