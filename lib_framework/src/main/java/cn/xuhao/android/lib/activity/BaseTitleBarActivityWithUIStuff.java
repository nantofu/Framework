package cn.xuhao.android.lib.activity;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import cn.xuhao.android.lib.R;
import cn.xuhao.android.lib.UIStuff;
import cn.xuhao.android.lib.widget.progressbar.ProgressDialog;

/**
 * Created by xuhao on 15/10/29.
 */
public abstract class BaseTitleBarActivityWithUIStuff extends BaseTitleBarActivity implements UIStuff {
    /**
     * 等待对话框
     */
    private ProgressDialog progressDialog = null;

    @Override
    public void showPDialog() {
        showPDialog(getString(R.string.progress_hint_text), true);
    }

    @Override
    public void showPDialog(String msg) {
        showPDialog(msg, true);
    }

    @Override
    public void showPDialog(String msg, boolean cancelable) {
        if (isDestroyed() || TextUtils.isEmpty(msg)) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setCancelable(cancelable);
        progressDialog.setCanceledOnTouchOutside(cancelable);
        progressDialog.setMessage(msg);
        if (!this.isFinishing()) {
            progressDialog.show();
        }
    }


    @Override
    public void closePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public boolean isWaitDialogShowing() {
        return progressDialog.isShowing();
    }

    @Override
    public void showToast(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    @Override
    public void showToast(@StringRes int resId) {
        showToast(getString(resId));
    }

    @Override
    public void showToast(@StringRes int resId, int time) {
        showToast(getString(resId), time);
    }

    @Override
    public void showToast(String msg, int time) {
        if (isDestroyed() || TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(this, msg, time).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closePDialog();
    }
}
