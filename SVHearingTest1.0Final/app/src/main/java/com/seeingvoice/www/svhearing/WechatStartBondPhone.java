package com.seeingvoice.www.svhearing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.login.UpdatePwdActivity;
import com.seeingvoice.www.svhearing.okhttpUtil.OkHttpManager;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;

public class WechatStartBondPhone extends TopBarBaseActivity implements View.OnClickListener {

    private Intent mIntent;
    private EditText edPsw,edRepeatPsw;
    private String phoneNumStr,bondType;//bondType 1 是手机号注册了  2 是手机号没注册
    private Button btnComfirm;
    private OkHttpManager mOkHttpManager;
    private final static String TAG = UpdatePwdActivity.class.getName();
    private String userPswStr,messageCode,errorInfo,errorCode;

    @Override
    protected int getConentView() {
        return R.layout.activity_wechat_start_bond_phone;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("微信绑定手机-设置密码");
        setTitleBack(true);
        setToolBarMenuOne(null,R.mipmap.share_icon,null);
        setToolBarMenuTwo(null,R.mipmap.share_icon,null);
        edPsw = findViewById(R.id.ed_password);
        edRepeatPsw = findViewById(R.id.ed_repeat_password);
        btnComfirm = findViewById(R.id.btn_comfirm);
        btnComfirm.setOnClickListener(this);
//        if (edPsw.getText().toString().trim().equals(edRepeatPsw.getText().toString().trim())){
//            userPswStr = edRepeatPsw.getText().toString().trim();
//        }
        mIntent = getIntent();
        phoneNumStr = mIntent.getStringExtra("phoneNum");
        bondType = mIntent.getStringExtra("binding_type");
        mOkHttpManager = OkHttpManager.getInstence();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_comfirm:
                switch (MyApplication.loginType){
                    case AppConstant.WECHAT_LOGIN:
                        break;
                }
                if (edPsw.getText().toString().trim().equals(edRepeatPsw.getText().toString().trim()) && !edPsw.getText().toString().trim().isEmpty()){
                    userPswStr = edPsw.getText().toString().trim();

                    mOkHttpManager.postNet(AppConstant.WECHAT_START_BOND_PHONE,new OkHttpManager.ResultCallback() {
                        @Override
                        public void onFailed(Request request, IOException e) {
                            Log.e(TAG,"微信绑定手机号码失败");
                        }

                        @Override
                        public void onSuccess(String response) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);
                                messageCode = jsonObject.getString("message_code");
                                errorInfo = jsonObject.getString("error_info");
                                errorCode = jsonObject.getString("error_code");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (messageCode.equals(AppConstant.NET_STATE_SUCCESS)){
                                Log.e(TAG,"微信绑定手机号码成功"+response);
                                ToastUtil.showLongToast("微信绑定手机成功");
                            }else {
                                ToastUtil.showLongToast("错误代码："+errorCode+","+errorInfo);
                            }
                        }
                    },new OkHttpManager.Param("user_id",String.valueOf(MyApplication.userId)),new OkHttpManager.Param("user_tel",phoneNumStr),new OkHttpManager.Param("user_pwd",userPswStr),new OkHttpManager.Param("binding_type",bondType));
                }
                break;

        }
    }
}
