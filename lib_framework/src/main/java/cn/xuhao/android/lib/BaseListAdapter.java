package cn.xuhao.android.lib;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListAdapter<DATA> extends BaseAdapter {
    /**
     * 上下文实例
     */
    protected Context mContext;
    /**
     * 数据列表
     */
    private List<DATA> data;

    public BaseListAdapter(Context context, List<DATA> data) {
        this.mContext = context;
        this.data = data == null ? new ArrayList<DATA>() : new ArrayList<>(data);
    }

    public List<DATA> getDataList() {
        return data;
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    @Override
    public DATA getItem(int position) {
        if (data == null || position < 0 || position >= data.size()) {
            return null;
        }
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 虚函数，继承此类的需要指定item的layout布局文件
     */
    public abstract int getItemResource();

    /**
     * 虚函数，继承此类的需要通过此函数绑定数据
     */
    public abstract View getItemView(int position, View convertView, ViewHolder holder);

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (null == view) {
            view = LayoutInflater.from(mContext).inflate(getItemResource(), viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        return getItemView(position, view, holder);
    }

    /**
     * 静态ViewHolder，优化加载数据
     */
    public static class ViewHolder {
        private SparseArray<View> views = new SparseArray<>();
        private View convertView;

        public ViewHolder(View convertView) {
            this.convertView = convertView;
        }

        public <T extends View> T getView(int resId) {
            View v = views.get(resId);
            if (null == v) {
                v = convertView.findViewById(resId);
                views.put(resId, v);
            }
            return (T) v;
        }
    }
}
