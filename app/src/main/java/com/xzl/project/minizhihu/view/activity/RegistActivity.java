package com.xzl.project.minizhihu.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.utils.T;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegistActivity extends AppCompatActivity {
    @BindView(R.id.common_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.common_toolbar_title_tv)
    TextView mTitleTv;
    @BindView(R.id.register_account_edit)
    EditText mAccountEdit;
    @BindView(R.id.register_password_edit)
    EditText mPasswordEdit;
    @BindView(R.id.register_confirm_password_edit)
    EditText mConfirmPasswordEdit;
    @BindView(R.id.register_btn)
    Button mRegisterBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);

        mTitleTv.setText("注册账号");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 提交注册
     * @param view
     */
    public void RegisterBtn(View view) {
        String account = mAccountEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();
        String confirmpassword = mConfirmPasswordEdit.getText().toString();
        if (TextUtils.isEmpty(account)&&TextUtils.isEmpty(password)&&TextUtils.isEmpty(confirmpassword)){
            T.s(RegistActivity.this,"所有项目为必填项");
        }else if (!password.equals(confirmpassword)){
            T.s(RegistActivity.this,"请再次确认密码是否一致");
        }else {
            BmobUser user = new BmobUser();
            user.setUsername(account);
            user.setPassword(password);
            user.signUp(new SaveListener<Object>() {
                @Override
                public void done(Object o, BmobException e) {
                    if (e==null){
                        T.s(RegistActivity.this,"注册成功");
                        finish();
                    }else {
                        T.s(RegistActivity.this,"注册失败："+e.getMessage());
                    }
                }
            });
        }
    }
}
