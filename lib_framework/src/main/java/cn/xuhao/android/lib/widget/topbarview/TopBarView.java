package cn.xuhao.android.lib.widget.topbarview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import cn.xuhao.android.lib.MyObserver;
import cn.xuhao.android.lib.widget.topbarview.adapter.BaseTopBarAdapter;

/**
 * Created by xuhao on 15/10/13.
 */
public class TopBarView extends RelativeLayout implements MyObserver {
    /**
     * 左边视图顺序值
     */
    public static final int LEFT_VIEW_INDEX = 0;
    /**
     * 中间视图顺序值
     */
    public static final int CENTER_VIEW_INDEX = 1;
    /**
     * 右边视图顺序值
     */
    public static final int RIGHT_VIEW_INDEX = 2;

    /**
     * 左边的视图
     */
    private View leftView = null;
    /**
     * 中间的视图
     */
    private View centerView = null;
    /**
     * 右边的视图
     */
    private View rightView = null;
    /**
     * 适配器
     */
    private BaseTopBarAdapter adapter = null;
    /**
     * 左边视图适配器点击缓存
     */
    private View.OnClickListener mOnLeftClickHolder = null;
    /**
     * 中间视图适配器点击缓存
     */
    private View.OnClickListener mOnCenterClickHolder = null;
    /**
     * 右边视图适配器点击缓存
     */
    private View.OnClickListener mOnRightClickHolder = null;

    public TopBarView(Context context) {
        super(context);
    }

    public TopBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TopBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setAdapter(BaseTopBarAdapter adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterObserver(this);
        }
        this.adapter = adapter;
        if (this.adapter != null) {
            this.adapter.registerObserver(this);
        }
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                TopBarView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (TopBarView.this.adapter != null) {
                    TopBarView.this.adapter.notifyDataSetChanged();
                }
            }
        });
        requestLayout();
        resetChildViews();
    }

    public BaseTopBarAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onChanged(int index) {
        //Just In Case
        if (this.getChildCount() > 3) {
            resetChildViews();
            index = -1;
        }

        View leftTemp = null;
        View centerTemp = null;
        View rightTemp = null;
        switch (index) {
            case LEFT_VIEW_INDEX: {
                leftTemp = this.adapter.getLeftView(leftView, this);
                break;
            }
            case CENTER_VIEW_INDEX: {
                centerTemp = this.adapter.getCenterView(centerView, this);
                break;
            }
            case RIGHT_VIEW_INDEX: {
                rightTemp = this.adapter.getRightView(rightView, this);
                break;
            }
            default: {
                leftTemp = this.adapter.getLeftView(leftView, this);
                centerTemp = this.adapter.getCenterView(centerView, this);
                rightTemp = this.adapter.getRightView(rightView, this);
                break;
            }
        }

        if (leftView == null && leftTemp != null) {
            leftView = leftTemp;
            addView(leftView, getChildParams(leftView, LEFT_VIEW_INDEX));
        }

        if (centerView == null && centerTemp != null) {
            centerView = centerTemp;
            addView(centerView, getChildParams(centerView, CENTER_VIEW_INDEX));
        }

        if (rightView == null && rightTemp != null) {
            rightView = rightTemp;
            addView(rightView, getChildParams(rightView, RIGHT_VIEW_INDEX));
        }

        coverClickListener();
    }

    private void coverClickListener() {
        if (mOnLeftClickHolder != null && leftView != null) {
            leftView.setOnClickListener(mOnLeftClickHolder);
        }
        if (mOnCenterClickHolder != null && centerView != null) {
            centerView.setOnClickListener(mOnCenterClickHolder);
        }
        if (mOnRightClickHolder != null && rightView != null) {
            rightView.setOnClickListener(mOnRightClickHolder);
        }
    }

    @Override
    public void onInvalidated(int index) {
        switch (index) {
            case LEFT_VIEW_INDEX: {
                if (leftView != null) {
                    leftView.invalidate();
                }
                break;
            }
            case CENTER_VIEW_INDEX: {
                if (centerView != null) {
                    centerView.invalidate();
                }
                break;
            }
            case RIGHT_VIEW_INDEX: {
                if (rightView != null) {
                    rightView.invalidate();
                }
                break;
            }
            default: {
                if (leftView != null) {
                    leftView.invalidate();
                }
                if (centerView != null) {
                    centerView.invalidate();
                }
                if (rightView != null) {
                    rightView.invalidate();
                }
                break;
            }
        }
        this.invalidate();
    }

    private LayoutParams getChildParams(View child, int index) {
        LayoutParams relParams = (LayoutParams) child.getLayoutParams();
        if (relParams == null) {
            relParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT);
        }
        switch (index) {
            case LEFT_VIEW_INDEX: {
                relParams.addRule(ALIGN_PARENT_LEFT);
                relParams.addRule(CENTER_VERTICAL);
                child.setPadding(dip2px(15), 0, dip2px(15), 0);
                break;
            }
            case CENTER_VIEW_INDEX: {
                relParams.addRule(CENTER_IN_PARENT);
                break;
            }
            case RIGHT_VIEW_INDEX: {
                relParams.addRule(ALIGN_PARENT_RIGHT);
                relParams.addRule(CENTER_VERTICAL);
                child.setPadding(dip2px(15), 0, dip2px(15), 0);
                break;
            }
        }
        return relParams;
    }

    private void resetChildViews() {
        this.removeAllViews();
        leftView = null;
        centerView = null;
        rightView = null;
    }

    private int dip2px(int dip) {
        if (getContext() == null || getResources() == null) {
            return 0;
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    public void setOnLeftClickListener(View.OnClickListener listener) {
        this.mOnLeftClickHolder = listener;
        if (leftView != null) {
            leftView.setOnClickListener(listener);
        }
    }

    public void setOnCenterClickListener(View.OnClickListener listener) {
        this.mOnCenterClickHolder = listener;
        if (centerView != null) {
            centerView.setOnClickListener(listener);
        }
    }

    public void setOnRightClickListener(View.OnClickListener listener) {
        this.mOnRightClickHolder = listener;
        if (rightView != null) {
            rightView.setOnClickListener(listener);
        }
    }

    @Nullable
    public View getLeftView() {
        return leftView;
    }

    @Nullable
    public View getCenterView() {
        return centerView;
    }

    @Nullable
    public View getRightView() {
        return rightView;
    }
}
