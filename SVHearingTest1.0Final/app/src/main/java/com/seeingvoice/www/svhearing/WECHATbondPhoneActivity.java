package com.seeingvoice.www.svhearing;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.okhttpUtil.OkHttpManager;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;

public class WECHATbondPhoneActivity extends TopBarBaseActivity implements View.OnClickListener {

    private static final String TAG = WECHATbondPhoneActivity.class.getName();
    private WECHATbondPhoneActivity.TimeCount time;//验证码倒计时
    private Button btnSetNewPwd,btnVerifyPhone;
    private String phoneNumStr, verifyCodeStr;//账户ID，和电话号码字符串
    private EditText edPhoneNum, edVerifyCode;
    private OkHttpManager mOkHttpManager;
    private TextView tvBondPhoneNum;
    private String bondingType;

    @Override
    protected int getConentView() {
        return R.layout.activity_wechatbond_phone;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initTitleBar();//设置标题栏
        initViewData();//初始化控件，和计时器
        mOkHttpManager = OkHttpManager.getInstence();
        mOkHttpManager.postNet(AppConstant.CHECK_WECHAT_BOND_PHONE_STATE, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                Log.e(TAG, "onSuccess: 微信绑定手机：判断微信是否绑定手机失败");
            }

            @Override
            public void onSuccess(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    if (jsonObject == null) {
                        return;
                    }
                    String messageCode = jsonObject.getString("message_code");
                    String errorInfo = jsonObject.getString("error_info");
                    String errorCode = jsonObject.getString("error_code");
                    if (messageCode.equals(AppConstant.NET_STATE_SUCCESS)) {
                        Log.e(TAG, "onSuccess: 该微信没有绑定手机");
                        tvBondPhoneNum.setText("该微信没有绑定手机");
                    } else {
                        Log.e(TAG, "onSuccess: 该微信已经绑定手机" + errorInfo + errorCode);
                        tvBondPhoneNum.setText("该微信已经绑定手机，无需再绑定");
                        btnVerifyPhone.setEnabled(false);
                        btnSetNewPwd.setEnabled(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new OkHttpManager.Param("user_id", String.valueOf(MyApplication.userId)));
    }

    /**
     * 设置标题栏
     */
    private void initTitleBar() {
        switch (MyApplication.loginType) {
            case AppConstant.WECHAT_LOGIN:
                setTitle("微信绑定手机");
                break;
            case AppConstant.QQ_LOGIN:
                setTitle("QQ绑定手机");
                break;
            case AppConstant.PHONE_LOGIN:
                break;
        }
        setTitleBack(true);
        setToolBarMenuOne("分享", R.mipmap.share_icon, null);
        setToolBarMenuOne("分享", R.mipmap.share_icon, null);
    }

    /**
     * 初始化控件，和计时器
     */
    private void initViewData() {
//        HideIMEUtil.wrap(this);
        edPhoneNum = findViewById(R.id.ed_phone_num);
        edVerifyCode = findViewById(R.id.ed_verify_code);
        btnVerifyPhone = findViewById(R.id.btn_verify_phone);
        btnVerifyPhone.setOnClickListener(this);
        btnSetNewPwd = findViewById(R.id.btn_SetNewPwd);
        btnSetNewPwd.setOnClickListener(this);
        time = new WECHATbondPhoneActivity.TimeCount(60000, 1000);//构造CountDownTimer对象
        tvBondPhoneNum = findViewById(R.id.tv_state_hint);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_verify_phone:
                startRockOn();//开始计时
                getVerifyMessage();//不能再点了
                phoneNumStr = edPhoneNum.getText().toString().trim();

                if (phoneNumStr.length() == 11){
                    getVerifyCode(phoneNumStr);
                }else {
                    tvBondPhoneNum.setText("请输入有效手机号！");
                }
                break;
            case R.id.btn_SetNewPwd://设置密码
                phoneNumStr = edPhoneNum.getText().toString().trim();
                verifyCodeStr = edVerifyCode.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumStr)) {
                    ToastUtil.showLongToast("手机号码格式不对");
                    return;
                }
                if (TextUtils.isEmpty(verifyCodeStr)) {
                    ToastUtil.showLongToast("验证码格式不对");
                    return;
                }
                //验证码是否正确
                String verifyCodeURL = AppConstant.USER_VERIFY_URL + "?user_tel=" + phoneNumStr + "&verify_code=" + verifyCodeStr;
                mOkHttpManager.getNet(verifyCodeURL, new OkHttpManager.ResultCallback() {
                    @Override
                    public void onFailed(Request request, IOException e) {
                        Log.e(TAG, "验证失败" + request.toString());
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "验证成功");
                        Intent mIntent = new Intent(WECHATbondPhoneActivity.this, WechatStartBondPhone.class);
                        mIntent.putExtra("phoneNum", phoneNumStr);
                        mIntent.putExtra("binding_type", bondingType);
                        startActivity(mIntent);
                        finish();
                    }
                });
                break;
        }
    }

    private void getVerifyCode(String phonenumstr) {
        mOkHttpManager.postNet(AppConstant.WECHAT_BOND_PHONE_CHECK_PHONE, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                tvBondPhoneNum.setText("网络错误"+request.toString());
            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject == null){
                        return;
                    }else {
                        if (jsonObject.getString("message_code").equals(AppConstant.NET_STATE_SUCCESS)){
                            bondingType = jsonObject.getJSONObject("data").getString("binding_type");
                            Log.e(TAG, "binding_type"+ bondingType);
                            if (bondingType != ""){
                                switch (bondingType){
                                    case "1"://该手机号已经注册  GET_VERIFY_CODE_URL   /v1/message/send_message_2
                                        mOkHttpManager.getNet(AppConstant.GET_VERIFY_CODE_URL+phoneNumStr, new OkHttpManager.ResultCallback() {
                                            @Override
                                            public void onFailed(Request request, IOException e) {
                                                tvBondPhoneNum.setText("获得验证码失败！");
                                            }

                                            @Override
                                            public void onSuccess(String response) {
                                                try{
                                                    JSONObject jsonObject=new JSONObject(response);
                                                    int status=jsonObject.getInt("status");
                                                    if (status == 1){
                                                        tvBondPhoneNum.setText("获得验证码成功！");
                                                    }else {
                                                        String message=jsonObject.getString("msg");
                                                        if (message!=null){
                                                            tvBondPhoneNum.setText(message);
                                                        }
                                                    }
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        break;
                                    case "2"://该手机号没有注册   USER_REGISTER_GET_VERIFY_CODE_URL = URL+"/v1/message/send_message";//注册时申请验证码
                                        mOkHttpManager.getNet(AppConstant.USER_REGISTER_GET_VERIFY_CODE_URL+"?user_tel="+phoneNumStr, new OkHttpManager.ResultCallback() {
                                            @Override
                                            public void onFailed(Request request, IOException e) {
                                                ToastUtil.showLongToast("网络连接失败");
                                            }

                                            @Override
                                            public void onSuccess(String response) {
                                                try{
                                                    JSONObject jsonObject=new JSONObject(response);
                                                    String messageCode = jsonObject.getString("message_code");
                                                    if (messageCode.equals(AppConstant.NET_STATE_SUCCESS)){
                                                        tvBondPhoneNum.setText("已成功发送验证码");
                                                    }else {
                                                        String message = jsonObject.getString("error_info");
                                                        if (message!=null){
                                                            tvBondPhoneNum.setText(message);
                                                        }
                                                    }
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        break;
                                    case "0":
                                        tvBondPhoneNum.setText("异常");
                                        break;
                                }
                            } else {
                                tvBondPhoneNum.setText("网络错误，稍后再试！");
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new OkHttpManager.Param("user_tel",phonenumstr));
    }

    /**
     * 验证码倒计时类
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onTick(long time) {//计时过程显示
            btnVerifyPhone.setClickable(false);
            btnVerifyPhone.setText("剩余" + time / 1000 + "秒");
        }

        @Override
        public void onFinish() {//计时完毕时触发
            btnVerifyPhone.setText("重新获取验证码");
            btnVerifyPhone.setEnabled(true);
            btnVerifyPhone.setClickable(true);
        }
    }

    /**
     * 获取验证码请求
     */
    private void getVerifyMessage() {
        //验证码获取成功后
        btnVerifyPhone.setEnabled(false);
        btnSetNewPwd.setEnabled(true);
    }

    /**
     * 开始计时
     */
    private void startRockOn() {
        time.start();//开始计时
    }
}

