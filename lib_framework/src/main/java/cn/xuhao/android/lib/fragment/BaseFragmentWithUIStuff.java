package cn.xuhao.android.lib.fragment;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import cn.xuhao.android.lib.R;
import cn.xuhao.android.lib.UIStuff;
import cn.xuhao.android.lib.widget.progressbar.ProgressDialog;

/**
 * Created by xuhao on 15/10/12.
 */
public abstract class BaseFragmentWithUIStuff extends BaseFragment implements UIStuff {
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
        if (getActivity() == null || TextUtils.isEmpty(msg)) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
        }
        progressDialog.setCancelable(cancelable);
        progressDialog.setCanceledOnTouchOutside(cancelable);
        progressDialog.setMessage(msg);
        if (!getActivity().isFinishing()) {
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
        if (progressDialog == null) {
            return false;
        }
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
        if (getActivity() == null || TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(getContext(), msg, time).show();
    }

    @Override
    public void onDestroy() {
        closePDialog();
        super.onDestroy();
    }
}
