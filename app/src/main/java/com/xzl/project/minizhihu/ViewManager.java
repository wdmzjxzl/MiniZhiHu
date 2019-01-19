package com.xzl.project.minizhihu;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by lxh on 2017/7/4.
 */

public class ViewManager {

    private SparseArray<View> mView;
    public View mConvertView;
    public Activity mActivity;
    public View.OnClickListener mClickListener;

    public ViewManager(Activity activity, View.OnClickListener onClickListener) {
        this.mActivity = activity;
        this.mView = new SparseArray<View>();
        this.mClickListener = onClickListener;
    }

    public ViewManager(View convertView, View.OnClickListener onClickListener) {
        this.mConvertView = convertView;
        this.mView = new SparseArray<View>();
        this.mClickListener = onClickListener;
    }

    /**
     * 设置ViewManager对象
     * @param convertView
     * @param onClickListener
     * @return
     */
    public static ViewManager getViewManager(View convertView, View.OnClickListener onClickListener) {
        if (null != convertView) {
            return new ViewManager(convertView ,onClickListener);
        }
        return null;
    }

    /**
     * 设置ViewManager对象
     * @param activity
     * @return
     */
    public static ViewManager getViewManager(Activity activity, View.OnClickListener onClickListener) {
        if (null != activity) {
            return new ViewManager(activity,onClickListener);
        }
        return null;
    }

    /**
     * 通过控件的Id获取对于的控件
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mView.get(viewId);

        if (null == view) {
            if (null != mConvertView){
                view = getWiew(viewId);
            }
            if (null != mActivity){
                view = $(viewId);
            }
            mView.put(viewId, view);
        }
        return (T) view;
    }


    /**
     * 获取Button字符串
     * @param viewId
     */
    public String getButtonStr(int viewId){
        Button view = getView(viewId);
        return get(view.getText().toString());
    }


    /**
     * 获取EditText字符串
     * @param viewId
     */
    public String getEditStr(int viewId){
        EditText view = getView(viewId);
        return get(view.getText().toString());
    }

    /**
     * 获取TextView字符串
     * @param viewId
     * @return
     */
    public String getTextStr(int viewId) {
        TextView view = getView(viewId);
        return get(view.getText().toString());
    }

    private String get(String str){
        return str.replace(" ","");
    }

/***************************************添加控件数据**********************************************/

    /**
     * 为TextView设置字符串
     * @param viewId
     * @param text
     * @return
     */
    public void setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
    }

    public void setText(int [] viewId, String[] text) {
        if (viewId.length == text.length){
            for (int i = 0; i < viewId.length; i++){
                setText(viewId[i] , text[i]);
            }
        }
    }

    /**
     * 为Button设置字符串
     * @param viewId
     * @param text
     */
    public void setButtonStr(int viewId, String text){
        Button view = getView(viewId);
        view.setText(text);
    }

    /**
     * 为EditText设置字符串
     * @param viewId
     * @param text
     */
    public void setEditStr(int viewId, String text){
        EditText view = getView(viewId);
        view.setText(text);
    }

    public void setEditStr(int[] viewId, String text) {
        for (int i = 0; i < viewId.length; i++){
            EditText view = getView(viewId[i]);
            view.setText(text);
        }
    }

    /**
     * 为ImageView设置图片
     * @param viewId
     * @param url
     * @return
     */
    public void setImageByUrl(int viewId, String url) {
//        BaseUtils.getInstance().loadPictures(Config.URL_IMAGE+url, getImageView(viewId));
    }

    /**
     * 为ImageView设置图片
     * @param viewId
     * @param drawableId
     * @return
     */
    public void setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
    }

    /**
     * 为ImageView设置图片
     * @param viewId
     * @param bm
     * @return
     */
    public void setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
    }



 /**********************************获取对象*******************************************/

    /**
     * 获取ListView对象
     * @param viewId
     * @return
     */
    public ListView getListView(int viewId) {
        ListView view = getView(viewId);
        return view;
    }

    /**
     * 获取ViewPager对象
     * @param viewId
     * @return
     */
    public ViewPager getViewPager(int viewId) {
        ViewPager view = getView(viewId);
        return view;
    }

    /**
     * 获取TabLayout对象
     * @param viewId
     * @return
     */
    public TabLayout getTabLayout(int viewId) {
        TabLayout view = getView(viewId);
        return view;
    }

    /**
     * 获取ExpandableListView对象
     * @param viewId
     * @return
     */
    public ExpandableListView getExListView(int viewId) {
        ExpandableListView view = getView(viewId);
        return view;
    }

    /**
     * 获取GridView对象
     * @param viewId
     * @return
     */
    public GridView getGridView(int viewId) {
        GridView view = getView(viewId);
        return view;
    }

//    /**
//     * 获取SwipImageView对象
//     * @param viewId
//     * @return
//     */
//    public SwipImageView getSwipImageView(int viewId) {
//        SwipImageView view = getView(viewId);
//        return view;
//    }
//
//    /**
//     * 获取Refresh对象
//     * @param viewId
//     * @return
//     */
//    public SuperSwipeRefreshLayout getRefresh(int viewId) {
//        SuperSwipeRefreshLayout view = getView(viewId);
//        return view;
//    }

    /**
     * 获取TextView对象
     * @param viewId
     * @return
     */
    public TextView getTextView(int viewId) {
        TextView view = getView(viewId);
        return view;
    }

    /**
     * 获取TextView对象
     * @param viewId
     * @return
     */
    public RelativeLayout getRelativeLayout(int viewId) {
        RelativeLayout view = getView(viewId);
        return view;
    }

    /**
     * 获取Button对象
     * @param viewId
     */
    public Button getButton(int viewId){
        Button view = getView(viewId);
        return view;
    }

    /**
     * 获取ImageView对象
     * @param viewId
     * @return
     */
    public ImageView getImageView(int viewId) {
        ImageView view = getView(viewId);
        return view;
    }

    /**
     * 获取EditText对象
     * @param viewId
     */
    public EditText getEditText(int viewId){
        EditText view = getView(viewId);
        return view;
    }

    /**
     * 获取任意View对象
     * @param viewId
     * @return
     */
    public <T extends View> T get(int viewId) {
        T view = (T) mView.get(viewId);
        if (null == view) {
            if (null != mConvertView){
                view = getWiew(viewId);
            }
            if (null != mActivity){
                view = $(viewId);
            }
            mView.put(viewId, view);
        }
        return view;
    }

/****************************************控件显示属性***********************************************/

    /**
     * 设置当前控件显示属性
     * @param viewId
     * @param visibility
     * @return
     */
    public void setVisibility(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
    }

    public void setVisibility(int[] viewId, int visibility) {
        for (int i = 0; i < viewId.length; i++){
            getView(viewId[i]).setVisibility(visibility);
        }
    }

    /***********************************任意对象监听**********************************************/

    /**
     * 为View对象设置监听
     * @param viewId
     * @return
     */
    public void click(int[] viewId) {
        for (int i = 0; i < viewId.length; i++){
            click(viewId[i]);
        }
    }

    /**
     * 通过控件的Id设置控件点击事件
     * @param viewId
     * @return
     */
    public void click(int viewId) {
        View view = mView.get(viewId);

        if (null == view) {
            if (null != mConvertView){
                view = getWiew(viewId);
            }
            if (null != mActivity){
                view = $(viewId);
            }
            mView.put(viewId, view);

            if (null != mClickListener){
                view.setOnClickListener(mClickListener);
            }
        }
    }

/************************************基础类型转换***********************************************/

    /**
     * 设置类型转换
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T $(int id) {
        return (T) mActivity.findViewById(id);
    }

    protected <T extends View> T getWiew(int id) {
        return (T) mConvertView.findViewById(id);
    }

}
