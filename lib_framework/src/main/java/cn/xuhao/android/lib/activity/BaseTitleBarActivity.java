package cn.xuhao.android.lib.activity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import cn.xuhao.android.lib.R;
import cn.xuhao.android.lib.widget.topbarview.TopBarView;
import cn.xuhao.android.lib.widget.topbarview.adapter.BaseTopBarAdapter;
import cn.xuhao.android.lib.widget.topbarview.adapter.BaseTopBarCenterTextAdapter;


/**
 * Created by xuhao on 15/10/12.
 */
public abstract class BaseTitleBarActivity extends BaseActivity {
    protected TopBarView mTopbarView = null;

    @Override
    protected void onContentViewLoaded() {
        initTopBarView();
    }

    @Override
    protected final int getBasicContentLayoutResId() {
        return R.layout.title_bar_basic_activity_layout;
    }

    /**
     * 获得内部业务布局资源id,呈现在ContentView中的
     */
    protected abstract int getInnerLayoutResId();

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        setTopBarTitle(mTopbarView, title);
    }

    /**
     * 注入业务布局
     */
    protected void inflateContentView() {
        setContentView(getBasicContentLayoutResId());
        mInflater = LayoutInflater.from(this);
        FrameLayout rootLayout = (FrameLayout) findViewById(R.id.basic_rootlayout);
        mInnerLayout = mInflater.inflate(getInnerLayoutResId(), rootLayout, true);
    }

    /**
     * 初始化顶部标题栏
     */
    protected final void initTopBarView() {
        mTopbarView = (TopBarView) findViewById(R.id.basic_tool_bar);
//        ViewCompat.setElevation(mTopbarView, 10);
        mTopbarView.setAdapter(new BaseTopBarCenterTextAdapter(this));

        String title = getTitle().toString();
        if (TextUtils.isEmpty(title)) {
            title = getString(R.string.app_name);
        }
        setTopBarTitle(mTopbarView, title);
    }

    /**
     * 设置标题栏中间标题内容
     *
     * @param topBarView 需要设置的TopBarView
     * @param title 新的标题内容
     */
    private void setTopBarTitle(TopBarView topBarView, CharSequence title) {
        if (topBarView != null) {
            BaseTopBarAdapter adapter = topBarView.getAdapter();

            if (adapter != null && adapter instanceof BaseTopBarCenterTextAdapter) {
                ((BaseTopBarCenterTextAdapter) adapter).setCenterTextStr(title.toString());
            }
        }
    }

}
