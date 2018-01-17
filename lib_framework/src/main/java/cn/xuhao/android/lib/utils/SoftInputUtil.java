package cn.xuhao.android.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @author yexiuliang
 */

public class SoftInputUtil {

    private static final int DURATION = 100;

    private SoftInputUtil() {

    }

    public static void closeSoftInput(final Context context) {
        final View view = ((Activity) context).getCurrentFocus();
        if (view == null) { return; }

        view.postDelayed(new Runnable() {

            @Override
            public void run() {

                hideInputMethod(context, view);
            }
        }, DURATION);

    }

    public static void openSoftInput(final Context context) {
        final View view = ((Activity) context).getCurrentFocus();
        if (view == null) { return; }
        view.postDelayed(new Runnable() {

            @Override
            public void run() {
                showInputMethod(context, view);
            }
        }, DURATION);
    }


    /**
     * Hides the input method.
     *
     * @param context context
     * @param view The currently focused view
     * @return success or not.
     */
    private static boolean hideInputMethod(@Nullable Context context, @Nullable View view) {
        if (context == null || view == null) {
            return false;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        return imm != null && imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    /**
     * Show the input method.
     *
     * @param context context
     * @param view The currently focused view, which would like to receive soft keyboard input
     * @return success or not.
     */
    private static boolean showInputMethod(@Nullable Context context, @Nullable View view) {
        if (context == null || view == null) {
            return false;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        return imm != null && imm.showSoftInput(view, 0);

    }

    /**
     * 判断点击屏幕时，是否应该关闭输入法。
     *
     * @param activity Activity界面
     * @param event 点击事件
     * @return 如果点击事件在EditText上，则不应该关闭，返回false；否则判断为可关闭，返回true
     */
    public static boolean canHideInputMethod(@NonNull Activity activity,
            @NonNull MotionEvent event) {
        View view = activity.getCurrentFocus();
        return !(view != null && view.hasWindowFocus() && (view instanceof EditText))
                || !isTouchEventHitViewArea(view, event);
    }

    /**
     * 判断触摸事件是否在指定View的区域内
     *
     * @param view View
     * @param event 触摸事件
     * @return 如果触摸事件在指定View的区域内，返回true；否则返回false
     */
    public static boolean isTouchEventHitViewArea(@NonNull View view, @NonNull MotionEvent event) {
        int[] leftTop = new int[2];
        view.getLocationOnScreen(leftTop);
        int left = leftTop[0];
        int top = leftTop[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();
        RectF rect = new RectF(left, top, right, bottom);
        return rect.contains(event.getRawX(), event.getRawY());
    }
}
