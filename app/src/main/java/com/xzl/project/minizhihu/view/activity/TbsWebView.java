package com.xzl.project.minizhihu.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.TextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xzl.project.minizhihu.DataBean.CollectionBean;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.listener.MJavascriptInterface;
import com.xzl.project.minizhihu.listener.PermissionsListener;
import com.xzl.project.minizhihu.utils.PermissionManager;
import com.xzl.project.minizhihu.utils.StatusBarUtil;
import com.xzl.project.minizhihu.utils.T;
import com.xzl.project.minizhihu.widget.CustomDialog;
import com.xzl.project.minizhihu.widget.WebViewProgressBar;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class TbsWebView extends AppCompatActivity implements DownloadListener,Toolbar.OnMenuItemClickListener,Toolbar.OnClickListener{
    WebView webView;
    WebViewProgressBar progressBar;
    boolean isContinue;
    private String webView_title;
    private String TAG = "=====";
    Intent intent;
    Toolbar toolbar;
    TextView bartitle;
    SmartRefreshLayout refreshLayout;
    TextView qqbrowser_view_header;
    private PermissionManager helper;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_tbs_webview);

        initVIew();
        //接受intent传过来的数据
        intent = getIntent();
        initWebSetting();

//        actionBar.setHomeButtonEnabled(true);//主键按钮能否可点击
//        actionBar.setDisplayHomeAsUpEnabled(true);//显示返回图标

        //状态栏透明和间距处理
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setOnMenuItemClickListener(this);
        bartitle.setText(intent.getStringExtra("title"));
        toolbar.setNavigationOnClickListener(this);

        //加载网页
        webView.loadUrl(intent.getStringExtra("url"));
        webView.setDownloadListener(this);

        /** 判断webview是否滑动到顶部 滑到到顶部才启用下拉刷新 */
        webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0){
                    refreshLayout.setEnableRefresh(true);
                }else {
                    refreshLayout.setEnableRefresh(false);
                }
            }
        });
        qqbrowser_view_header.setText("网页由 "+intent.getStringExtra("author")+" 提供"+"\nAPP由 徐泽林 开发\nQQ浏览器X5内核提供技术支持");
    }

    /** websetting基础配置 */
    private void initWebSetting() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setSupportMultipleWindows(false);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setAppCacheMaxSize(Long.MAX_VALUE);
        settings.setAppCachePath(this.getDir("appcache",0).getPath());
        settings.setDatabasePath(this.getDir("databass",0).getPath());
        settings.setGeolocationDatabasePath(this.getDir("geolocation",0).getPath());
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        //不显示缩放按钮
        settings.setDisplayZoomControls(false);
        //设置自适应屏幕，两者合用
        //将图片调整到适合webview的大小
        settings.setUseWideViewPort(true);
        //缩放只屏幕的大小
        settings.setLoadWithOverviewMode(true);
        //自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }

    /** 界面初始化 */
    private void initVIew() {
        webView = findViewById(R.id.tbs_webview);
        progressBar = findViewById(R.id.webprogress);
        webView.addJavascriptInterface(new MJavascriptInterface(this),"imagelistener");
        webView.setWebViewClient(client);
        webView.setWebChromeClient(new WCC());
        toolbar = findViewById(R.id.tbs_toolbar);
        bartitle = findViewById(R.id.tbs_toolbar_title_tv);
        refreshLayout = findViewById(R.id.refreshLayout);
        qqbrowser_view_header = findViewById(R.id.header);
    }

    /**
     * 设置webview客户端时，防止网页加载时调起系统浏览器或者默认浏览器
     */
    private WebViewClient client = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
//            addImageClickListener(webView);
        }

        /**
         * 单击图片查看大图
         * @param webView
         */
        private void addImageClickListener(WebView webView){
            webView.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\"); " +
                    "for(var i=0;i<objs.length;i++) " +
                    "{"
                    + " objs[i].onclick=function() " +
                    " { "
                    + "  window.imagelistener.openImage(this.src); " +//通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
                    " } " +
                    "}" +
                    "})()");
        }
    };


    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//        downloadBySystem(url,contentDisposition,mimetype);
        downloadByBrowser(url);
    }

    /** 菜单单击事件 */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.web_share:
                //调用系统分享
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT,"WanAndroid分享【"+intent.getStringExtra("title")+"】\n\n专题： "+intent.getStringExtra("url"));
                startActivity(Intent.createChooser(textIntent,"分享"));
                break;
            case R.id.web_browser:
                //使用系统浏览器打开网址
                Uri uri = Uri.parse(intent.getStringExtra("url"));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(browserIntent);
                break;
            case R.id.collection_select:
                BmobUser user = BmobUser.getCurrentUser();
                if (user!=null){
                    BmobQuery<CollectionBean> query = new BmobQuery<>();
                    query.addWhereEqualTo("title", intent.getStringExtra("title"));
                    query.findObjects(new FindListener<CollectionBean>() {
                        @Override
                        public void done(List<CollectionBean> list, BmobException e) {
                            if (e==null){
                                CollectionBean bean = new CollectionBean();
                                bean.setObjectId(list.get(0).getObjectId());
                                bean.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e==null){
                                            T.s(TbsWebView.this,"取消收藏成功");
                                        }else {
                                            T.s(TbsWebView.this,"取消收藏失败："+e.getMessage());
                                        }
                                    }
                                });
                            }else {
                                CollectionBean bean = new CollectionBean();
                                bean.setLink(intent.getStringExtra("url"));
                                bean.setAuthor(intent.getStringExtra("author"));
                                bean.setTitle(intent.getStringExtra("title"));
                                bean.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e==null){
                                            T.s(TbsWebView.this,"收藏成功，请前往收藏列表查看");
                                        }else{
                                            T.s(TbsWebView.this,"收藏失败 日志："+e.getMessage());
                                        }
                                    }
                                });
                            }
                        }
                    });
                }else {
                    T.s(TbsWebView.this,"请登陆后使用收藏功能");
                }


                break;
            case R.id.qr_code:
                helper= PermissionManager.with(TbsWebView.this)
                        .addRequestCode(MainActivity.CODE_WRITE_EXTERNAL_STORAGE)

                        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)

                        .setPermissionsListener(new PermissionsListener() {
                            @Override
                            public void onGranted() {
                                //当权限被授予时调用
                                CustomDialog dialog = new CustomDialog(TbsWebView.this,R.style.Theme_AppCompat_Dialog,intent.getStringExtra("url"));

                                dialog.show();
                            }

                            @Override
                            public void onDenied() {
                                //用户拒绝该权限时调用
                                T.s(TbsWebView.this,"您已拒绝授权，分享二维码需要读取手机存储权限，请前往设置给APP授权后使用");
                            }

                            @Override
                            public void onShowRationale(String[] permissions) {
                                //当用户拒绝某权限事并点击“不再提醒”的按钮是，需要给出合适的响应(比如扎实对话框来解释)
                                T.s(TbsWebView.this,"分享二维码需要读取手机存储权限，请前往设置给APP授权后使用");
                            }
                        }).request();

                break;

                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        finish();
    }


    private class WCC extends WebChromeClient{
        @Override
        public void onProgressChanged(WebView view, final int newProgress) {
            super.onProgressChanged(view, newProgress);

            //如果进度条隐藏则让它显示
            if (View.GONE == progressBar.getVisibility()) {
                progressBar.setVisibility(View.VISIBLE);
            }

            if (newProgress >= 80) {
                if (isContinue) {
                    return;
                }
                isContinue = true;
                progressBar.setCurProgress(1000, new WebViewProgressBar.EventEndListener() {
                    @Override
                    public void onEndEvent() {
                        isContinue = false;

//                        Log.e(TAG, "onProgressChanged: 11111111111" );
                        if (View.VISIBLE == progressBar.getVisibility()) {
                            hideProgress();
                        }
                    }
                });
            } else {
                progressBar.setNormalProgress(newProgress);
            }
        }
    }

    /** 进度条加载完毕后渐变式隐藏 */
    private void hideProgress() {
        AnimationSet animation = getDismissAnim(TbsWebView.this);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        progressBar.startAnimation(animation);
    }

    /** 渐变隐藏动画 */
    private AnimationSet getDismissAnim(Context context) {
        AnimationSet dismiss = new AnimationSet(context, null);
        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
        alpha.setDuration(1000);
        dismiss.addAnimation(alpha);
        return dismiss;
    }

    /** 拦截Android系统页面回退 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (webView !=null && webView.canGoBack()){
                //仅仅回退当前网页，禁止退出当前activity
                webView.goBack();
                return true;
            }else {
                return super.onKeyDown(keyCode,event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /** 注销当前activity页面是，注销所有内存引用，防止内存泄漏 */
    @Override
    protected void onDestroy() {
        if (webView != null){
            webView.destroy();
        }
        super.onDestroy();
    }

    /** 销毁当前页面 */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    /** 创建菜单项 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setIconsVisible(menu,true); //显示menuicon
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tbs_menu,menu);

        return true;
    }

    private void setIconsVisible(Menu menu,boolean flag){
        //判断menu是否为空
        if (menu != null){
            try{
                //如果不为空，就反射拿到menu的setOptionalIconsVisible方法
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible",Boolean.TYPE);
                //暴力访问该方法
                method.setAccessible(true);
                //调用该方法显示icon
                method.invoke(menu,flag);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 跳转到浏览器下载
     */
    private void downloadByBrowser(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    /**
     * 使用系统的下载服务
     */
//    private void downloadBySystem(String url,String contentDisposition,String mimeType){
//        //指定下载地址
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        //允许媒体扫描，根据下载的文件类型加入相册、音乐等媒体
//        request.allowScanningByMediaScanner();
//        //设置通知的显示类型，下载进行时和完成后显示通知
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        //设置通知栏的标题，如果不设置，默认使用文件名
//        request.setTitle("徐泽林的APP正在下载东西");
//        //设置通知栏描述
////        request.setDescription("111");
//        //允许在计费流量下下载
//        request.setAllowedOverMetered(true);
//        //允许该记录在下载管理界面可见
//        request.setVisibleInDownloadsUi(true);
//        //允许漫游时下载
//        request.setAllowedOverRoaming(true);
//        //允许下载的网络类型
////        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
//        //设置下载文件的保存路径和文件名
//        String fileName = URLUtil.guessFileName(url,contentDisposition,mimeType);
//        Log.e(TAG, "downloadBySystem: "+fileName );
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);
//        //另外可选一下方法，自定义下载路径
////        request.setDestinationUri()
////        request.setDestinationInExternalFilesDir()
//        final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//        //添加一个下载任务
//        long downloadId = downloadManager.enqueue(request);
//        Log.e(TAG, "downloadBySystem: "+downloadId );
//    }
}
