package cn.xuhao.android.lib.widget.progressbar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.xuhao.android.lib.R;

/**
 * Created by xuhao on 2017/4/30.
 */

public class ProgressDialog extends AlertDialog {

    private ProgressBar mProgress;

    private TextView mMessage;

    public ProgressDialog(Context context) {
        super(context);
        init();
    }

    public ProgressDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.progress_dialog_layout, null);
        mProgress = (ProgressBar) view.findViewById(R.id.progress_bar);
        mMessage = (TextView) view.findViewById(R.id.message_text);
        mProgress.setIndeterminate(true);
        setView(view);
    }

    public static ProgressDialog show(Context context, CharSequence title,
            CharSequence message) {
        return show(context, title, message, false, null);
    }

    public static ProgressDialog show(Context context, CharSequence title,
            CharSequence message, boolean cancelable) {
        return show(context, title, message, cancelable, null);
    }

    public static ProgressDialog show(Context context, CharSequence title,
            CharSequence message,
            boolean cancelable, OnCancelListener cancelListener) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int color = getContext().getResources().getColor(R.color.colorAccent);
        mProgress.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void setMessage(CharSequence message) {
        if (mProgress != null) {
            mMessage.setText(message);
        }
    }
}
