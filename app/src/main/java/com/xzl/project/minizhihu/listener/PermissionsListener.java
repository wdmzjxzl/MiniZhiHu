package com.xzl.project.minizhihu.listener;

/**
 * 权限监听器
 */

public interface PermissionsListener {
    /**
     * 用户授权后调用
     */
    public void onGranted();

    /**
     * 用户禁止后调用
     */
    public void onDenied();

    /**
     * 是否显示阐述性说明
     */
    public void onShowRationale(String[] permissions);
}
