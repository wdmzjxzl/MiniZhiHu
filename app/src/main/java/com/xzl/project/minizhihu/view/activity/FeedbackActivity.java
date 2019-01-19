package com.xzl.project.minizhihu.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.TextView;

import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.widget.WebViewProgressBar;

import butterknife.BindView;

public class FeedbackActivity extends AppCompatActivity {
    private WebView feedbackWebView;
    private Toolbar toolbar;
    private TextView toolbar_text;
    boolean isContinue;
    private String url = "https://support.qq.com/product/37626?d-wx-push=1";
    WebViewProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle("");
        initView();

        setSupportActionBar(toolbar);
        toolbar_text.setText("用户反馈");

        WebSettings settings = feedbackWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        feedbackWebView.loadUrl(url);
        feedbackWebView.setWebViewClient(webViewClient);
        feedbackWebView.setWebChromeClient(new WCC());
    }

    private void initView() {
        feedbackWebView = findViewById(R.id.feedback_webview);
        toolbar = findViewById(R.id.feedback_toolbar);
        toolbar_text = findViewById(R.id.feedback_title);
        progressBar = findViewById(R.id.webprogress);
    }

    private WebViewClient webViewClient = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String s) {
            if (s == null){
                return false;
            }
            try{
                if (s.startsWith("weixin://")){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                    webView.getContext().startActivity(intent);
                    return true;
                }
            }catch (Exception e){
                return false;
            }
            webView.loadUrl(url);
            return true;
        }
    };

    private class WCC extends WebChromeClient {
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
        AnimationSet animation = getDismissAnim(FeedbackActivity.this);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (feedbackWebView !=null && feedbackWebView.canGoBack()){
                //仅仅回退当前网页，禁止退出当前activity
                feedbackWebView.goBack();
                return true;
            }else {
                return super.onKeyDown(keyCode,event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
