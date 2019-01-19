package com.xzl.project.minizhihu.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.xzl.project.minizhihu.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.PreferencesFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import yanzhikai.textpath.SyncTextPathView;

public class SplashActivity extends AppCompatActivity {
    @BindView(R.id.atpv1)
    SyncTextPathView pathView0;
    @BindView(R.id.atpv2)
    SyncTextPathView pathView1;
    @BindView(R.id.atpv3)
    SyncTextPathView pathView2;
    @BindView(R.id.atpv4)
    SyncTextPathView pathView3;
    @BindView(R.id.atpv5)
    SyncTextPathView pathView4;
    @BindView(R.id.atpv6)
    SyncTextPathView pathView5;
    @BindView(R.id.atpv7)
    SyncTextPathView pathView6;
    @BindView(R.id.atpv8)
    SyncTextPathView pathView7;
    @BindView(R.id.intent_btn)
    Button button;

    private final int SPLASH_DISPLAY_LENGHT = 5000;
    private Handler handler;
    private int isrun = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        pathView0.startAnimation(0,1);
        pathView1.startAnimation(0,1);
        pathView2.startAnimation(0,1);
        pathView3.startAnimation(0,1);
        pathView4.startAnimation(0,1);
        pathView5.startAnimation(0,1);
        pathView6.startAnimation(0,1);
        pathView7.startAnimation(0,1);

        handler = new Handler();
        //延迟SPLASH_DISPLAY_LENGHT的时间然后跳转到MianActivity
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isrun == 0){
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    SplashActivity.this.finish();
                }

            }
        },SPLASH_DISPLAY_LENGHT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onclick(View view) {
        isrun = 1;
        startActivity(new Intent(SplashActivity.this,MainActivity.class));
        SplashActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
