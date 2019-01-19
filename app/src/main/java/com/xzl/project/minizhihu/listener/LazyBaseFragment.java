package com.xzl.project.minizhihu.listener;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 *
 * ViewPager + Fragment 结构中
 * ViewPager 有预加载功能，在访问网络的时候会同时加载多个页面的网络，体验很不好，
 更会影响一些带有页面进度条的显示
 * 所以ViewPager中的Fragment 都继承这个类。 效果是只预加载布局，但是不会访问网络。
 */
public abstract class LazyBaseFragment extends Fragment {
    public View view;

    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;


    /**
     * 初始化view对象，这里在Fragment中的onCreateView方法中进行实现，返回一个View对象
     */
    public abstract View initView();


    /**
     * 初始化数据
     */
    public abstract void initData();


    //先于oncreatview执行的方法
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 可见
     */
    protected void onVisible() {
        lazyLoad();
    }

    /**
     * 不可见
     */
    protected void onInvisible() {

    }

    /**
     * 延迟加载
     */
    protected abstract void lazyLoad();


}
