package cn.xuhao.android.lib.widget.topbarview.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.xuhao.android.lib.R;
import cn.xuhao.android.lib.widget.topbarview.TopBarView;


/**
 * Created by xuhao on 15/10/21.
 */
public class BaseTopBarCenterTextAdapter extends BaseTopBarAdapter {
    protected String centerTextStr = "";

    public BaseTopBarCenterTextAdapter(Activity activity) {
        super(activity);
    }

    @Override
    public View getLeftView(View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public View getCenterView(View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        TextView textView = null;
        if (convertView == null) {
            convertView = textView = new TextView(context);
            textView.setTextColor(0xff333333);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setMaxLines(1);
            textView.setGravity(Gravity.CENTER);
        } else {
            textView = (TextView) convertView;
        }

        if (!TextUtils.isEmpty(centerTextStr)) {
            textView.setText(centerTextStr);
        } else {//默认去拿lable
            CharSequence title = context.getString(R.string.app_name);
            if (context instanceof Activity) {
                title = ((Activity) context).getTitle();
            }
            centerTextStr = title == null ? "" : title.toString();
            if (!TextUtils.isEmpty(title)) {
                textView.setText(title);
            }
        }
        return convertView;
    }

    @Override
    public View getRightView(View convertView, ViewGroup parent) {
        return null;
    }

    public String getCenterTextStr() {
        return centerTextStr;
    }

    public void setCenterTextStr(String centerTextStr) {
        this.centerTextStr = centerTextStr;
        notifyItemChanged(TopBarView.CENTER_VIEW_INDEX);
    }
}
