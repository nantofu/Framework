package cn.xuhao.android.lib;

/**
 * Created by xuhao on 15/10/10.
 */
public interface MyObserver {
    /**
     * 有变化的条目,全部为-1
     *
     * @param index 下标数,从0开始
     */
    void onChanged(int index);

    /**
     * 需要重绘的条目,全部为-1
     *
     * @param index 下标数,从0开始
     */
    void onInvalidated(int index);
}
