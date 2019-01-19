package com.xzl.project.minizhihu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxFragment;

import javax.inject.Inject;

/**
 * Created by lxh on 2017/6/28.
 */

public abstract class BaseFragment extends RxFragment implements View.OnClickListener{

    @Inject
//    public RequestHttp request;
    public ViewManager mView , mFoot , mHead;

    private int        pageNum = 1 , SCANNIN_GREQUEST_CODE = 1;
    private String query;

    /**
     * 获取布局view
     * @param inflater
     * @param container
     * @param layout
     * @return
     */
    public View setLayout(LayoutInflater inflater , ViewGroup container , int layout){
        return inflater.inflate(layout,container,false);
    }

    /**
     * 初始化布局管理器
     * @param onClick
     * @param view
     */
    public View setView(View view  , View.OnClickListener onClick){

        mView = ViewManager.getViewManager(view , onClick);
        if (null == mView)return view;
        return view;
    }

    /**
     * 回退
     * @param view
     */
    public void back(View view) {
        getActivity().finish();
    }
}
