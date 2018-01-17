package cn.xuhao.android.lib.widget.progressbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import cn.xuhao.android.lib.R;


/**
 * Created by xuhao.
 */
public class CustomColorProgressBar extends ProgressBar {

    public CustomColorProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public CustomColorProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomColorProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomColorProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int color = context.getResources().getColor(R.color.colorAccent);
        setColor(color);
    }

    public void setColor(int color) {
        getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        this.postInvalidate();
    }


}
