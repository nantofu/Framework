package cn.xuhao.android.lib.activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cn.xuhao.android.lib.utils.L;

/**
 * Created by xuhao on 2017/7/6.
 */

public class ActivityStack {
    private static boolean finishCallFromSelf = false;

    private final static List<AppCompatActivity> STACK = new ArrayList<>();

    private final static List<OnStackChangedListener> LISTENERS = new ArrayList<>();

    private final static Application.ActivityLifecycleCallbacks LIFECYCLE_CALLBACKS = new Application
            .ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (activity instanceof AppCompatActivity) {
                push((AppCompatActivity) activity);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (finishCallFromSelf) {
                finishCallFromSelf = false;
                return;
            }
            if (activity instanceof AppCompatActivity) {
                pop(lastIndexOf((AppCompatActivity) activity), false);
            }
        }
    };

    private ActivityStack() {}

    public static void init(Application application) {
        application.registerActivityLifecycleCallbacks(LIFECYCLE_CALLBACKS);
    }

    public interface OnStackChangedListener {
        /**
         * 压栈回调
         *
         * @param activity 实例
         */
        void onPush(AppCompatActivity activity);

        /**
         * 弹栈回调
         *
         * @param activity 实例
         */
        void onPop(AppCompatActivity activity);

        /**
         * 最后一个实例被弹出,会在Onpop之后调用此方法
         *
         * @param lastActivity 最后一个被弹出的实例
         */
        void onStackGonnaEmpty(AppCompatActivity lastActivity);
    }

    public static void addStackChangedListener(OnStackChangedListener listener) {
        synchronized (LISTENERS) {
            LISTENERS.add(listener);
        }
    }

    public static void removeStackChangedListener(OnStackChangedListener listener) {
        synchronized (LISTENERS) {
            LISTENERS.remove(listener);
        }
    }

    public static void removeAllStackChangedListener() {
        synchronized (LISTENERS) {
            LISTENERS.clear();
        }
    }

    private static void notifyPushListener(AppCompatActivity activity) {
        for (OnStackChangedListener listener : LISTENERS) {
            try {
                listener.onPush(activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void notifyPopListener(AppCompatActivity activity) {
        for (OnStackChangedListener listener : LISTENERS) {
            try {
                listener.onPop(activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void notifyGonnaEmptyListener(AppCompatActivity activity) {
        for (OnStackChangedListener listener : LISTENERS) {
            try {
                listener.onStackGonnaEmpty(activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String logStack() {
        StringBuilder builder = new StringBuilder("stack_bottom");
        for (AppCompatActivity activity : STACK) {
            builder.append("->" + activity.getClass().getSimpleName());
        }
        builder.append("->stack_head");
        L.i("ActivityStack", builder.toString());
        return builder.toString();
    }

    /**
     * 压栈
     *
     * @param activity 需要压栈的Activity对象
     */
    public static void push(AppCompatActivity activity) {
        synchronized (STACK) {
            STACK.add(activity);
            L.i("ActivityStack", "push:" + activity.getClass().getSimpleName());
            logStack();
            notifyPushListener(activity);
        }
    }

    /**
     * 弹栈
     *
     * @return 栈顶的Activity对象
     */
    public static AppCompatActivity pop() {
        AppCompatActivity activity = pop(STACK.size() - 1, false);
        return activity;
    }

    /**
     * 取栈内实例
     *
     * @param index 栈内下标
     * @return 实例对象
     */
    public static AppCompatActivity take(int index) {
        AppCompatActivity activity = STACK.get(index);
        return activity;
    }

    /**
     * 取栈顶实例
     *
     * @return 实例对象
     */
    public static AppCompatActivity take() {
        return take(STACK.size() - 1);
    }

    /**
     * 弹栈
     *
     * @param index 弹出的下标
     * @param isClearTop 是否清除(关闭)该下标以上的Activity
     * @return 该index的Activity对象
     */
    public static AppCompatActivity pop(int index, boolean isClearTop) {
        if (index == -1) {
            return null;
        }
        synchronized (STACK) {
            try {
                AppCompatActivity activity = STACK.remove(index);
                if (!activity.isFinishing()) {
                    finishCallFromSelf = true;
                    activity.finish();
                }
                if (isClearTop) {
                    clearUpByIndex(index - 1);
                }
                L.i("ActivityStack", "pop:" + activity.getClass().getSimpleName());
                logStack();
                notifyPopListener(activity);
                if (size() == 0) {
                    notifyGonnaEmptyListener(activity);
                }
                return activity;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 清空栈内该下标以上(到栈底)的所有实例
     *
     * @param index 下标
     */
    private static void clearUpByIndex(int index) {
        synchronized (STACK) {
            if (index < 0) {
                return;
            }
            if (index >= STACK.size()) {
                index = STACK.size() - 1;
            }
            for (int i = index; i >= 0; i--) {
                AppCompatActivity activity = STACK.get(i);
                activity.finish();
            }
            if (index != 0) {
                STACK.subList(0, index).clear();
            } else {
                STACK.clear();
            }
        }
    }

    /**
     * 退出整个程序
     *
     * @param isDesc 是否倒序
     */
    public static void exitApplication(boolean isDesc) {
        synchronized (STACK) {
            if (isDesc) {
                for (int i = STACK.size() - 1; i >= 0; i--) {
                    AppCompatActivity activity = STACK.get(i);
                    activity.finish();
                }
            } else {
                for (int i = 0; i < STACK.size(); i++) {
                    AppCompatActivity activity = STACK.get(i);
                    activity.finish();
                }
            }
            STACK.clear();
        }
    }

    /**
     * 退出整个程序,倒序
     */
    public static void exitApplication() {
        exitApplication(true);
    }

    /**
     * 栈的大小
     *
     * @return Activity实例数
     */
    public static int size() {
        return STACK.size();
    }

    /**
     * 从左到右查找Activity实例下标
     *
     * @param activity 实例对象
     * @return 对应栈内的下标数
     */
    public static int indexOf(AppCompatActivity activity) {
        return STACK.indexOf(activity);
    }

    /**
     * 从右到左查找Activity实例下标
     *
     * @param activity 实例对象
     * @return 对应栈内的下标数
     */
    public static int lastIndexOf(AppCompatActivity activity) {
        return STACK.lastIndexOf(activity);
    }

    /**
     * 根据Class从左到右查找
     *
     * @param clz 相应的Activity的Class
     * @return 对应栈内下标数
     */
    public static int indexOf(Class<? extends AppCompatActivity> clz) {
        for (int i = 0; i < STACK.size(); i++) {
            AppCompatActivity activity = STACK.get(i);
            activity.getClass().equals(clz);
            return i;
        }
        return -1;
    }

    /**
     * 根据Class从右到左查找
     *
     * @param clz 相应的Activity的Class
     * @return 对应栈内下标数
     */
    public static int lastIndexOf(Class<? extends AppCompatActivity> clz) {
        for (int i = STACK.size() - 1; i >= 0; i--) {
            AppCompatActivity activity = STACK.get(i);
            activity.getClass().equals(clz);
            return i;
        }
        return -1;
    }

    /**
     * 对应某一个Activity的数量
     *
     * @param clz
     * @return 数量
     */
    public static int sizeOf(Class<? extends AppCompatActivity> clz) {
        int count = 0;
        for (AppCompatActivity activity : STACK) {
            if (activity.getClass().equals(clz)) {
                count++;
            }
        }
        return count;
    }

}
