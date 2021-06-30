package com.HYPech.SV8.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.HYPech.SV8.R;
import com.HYPech.SV8.base.BaseActivity;

import androidx.annotation.Nullable;

public class LoginWithpwdActivity extends BaseActivity {

    private EditText mPhoneEt;
    private EditText mPwdEt;
    private TextView mLoginBt;
    private TextView mLoginCodeBt;
    private TextView mRegisterBt;
    private TextView mForgetPwdBt;
    private ImageView mQQLoginIv;
    private ImageView mWechatLoginIv;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_login_pwd);
        super.onCreate(savedInstanceState);


    }

    @Override
    public void initViews() {
        mPhoneEt = this.findViewById(R.id.phone_Et);
        mPwdEt= this.findViewById(R.id.code_Et);
        mLoginBt= this.findViewById(R.id.register_bt);
        mLoginCodeBt= this.findViewById(R.id.pwd_login);
        mRegisterBt= this.findViewById(R.id.register_tv);
        mForgetPwdBt= this.findViewById(R.id.forget_pwd);
        mQQLoginIv= this.findViewById(R.id.login_qq);
        mWechatLoginIv= this.findViewById(R.id.login_wechat);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void installListeners() {
        mPhoneEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mPwdEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mLoginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mLoginCodeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mRegisterBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginWithpwdActivity.this, Register1Activity.class);
                startActivity(intent);
            }
        });
        mForgetPwdBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mQQLoginIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mWechatLoginIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void processHandlerMessage(Message msg) {

    }
}
