package com.xzl.project.minizhihu.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.xzl.project.minizhihu.Constants;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.http.HttpUtil;
import com.xzl.project.minizhihu.utils.StatusBarUtil;
import com.xzl.project.minizhihu.utils.T;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.login_group)
    RelativeLayout mLoginGroup;
    @BindView(R.id.login_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.login_account_edit)
    EditText mAccountEdit;
    @BindView(R.id.login_password_edit)
    EditText mPasswordEdit;
    @BindView(R.id.login_btn)
    Button mLoginBtn;
    @BindView(R.id.login_register_btn)
    Button mRegisterBtn;

    private String account;
    private String password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void OnRegisterListener(View view) {
        //跳转到注册界面
        startActivity(new Intent(LoginActivity.this,RegistActivity.class));
    }

    public void OnLoginListener(View view) {
        //用户登录
        account = mAccountEdit.getText().toString();
        password = mPasswordEdit.getText().toString();
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)){
            BmobUser user = new BmobUser();
            user.setUsername(account);
            user.setPassword(password);
            user.login(new SaveListener<Object>() {
                @Override
                public void done(Object o, BmobException e) {
                    if (e==null){
                        T.s(LoginActivity.this,"登录成功");
                        finish();
                    }else {
                        T.s(LoginActivity.this,"登录失败："+e.getMessage());
                    }
                }
            });
        }else T.s(LoginActivity.this,"请输入用户名和密码");
    }
}
