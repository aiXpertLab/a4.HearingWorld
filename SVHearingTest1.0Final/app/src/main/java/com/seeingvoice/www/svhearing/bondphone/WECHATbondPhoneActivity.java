package com.seeingvoice.www.svhearing.bondphone;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.seeingvoice.www.svhearing.MyApplication;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.beans.VerifyTelNumBean;
import com.seeingvoice.www.svhearing.okhttpUtil.OkHttpManager;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;

import static com.seeingvoice.www.svhearing.AppConstant.CHECK_WECHAT_BOND_PHONE_STATE;
import static com.seeingvoice.www.svhearing.AppConstant.GET_VERIFY_CODE_URL;
import static com.seeingvoice.www.svhearing.AppConstant.NET_STATE_BONDED;
import static com.seeingvoice.www.svhearing.AppConstant.NET_STATE_FAILED;
import static com.seeingvoice.www.svhearing.AppConstant.NET_STATE_SUCCESS;
import static com.seeingvoice.www.svhearing.AppConstant.QQ_LOGIN;
import static com.seeingvoice.www.svhearing.AppConstant.USER_REGISTER_GET_VERIFY_CODE_URL;
import static com.seeingvoice.www.svhearing.AppConstant.USER_VERIFY_URL;
import static com.seeingvoice.www.svhearing.AppConstant.WECHAT_BOND_PHONE_CHECK_PHONE;
import static com.seeingvoice.www.svhearing.AppConstant.WECHAT_LOGIN;

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
        mOkHttpManager.postNet(CHECK_WECHAT_BOND_PHONE_STATE, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                tvBondPhoneNum.setText("网络错误，请检查网络！");
                Log.e(TAG, "onFailed: 绑定手机：判断是否绑定手机失败");
            }

            @Override
            public void onSuccess(String response) {
                VerifyTelNumBean verifyTelNumBean = null;
                String messageCode = "";
                String errorInfo = "";
                String errorCode = "";
                String telNum = "0";
                try {
                    Gson gson = new Gson();
                    verifyTelNumBean = gson.fromJson(response,VerifyTelNumBean.class);
                    if (null != verifyTelNumBean){
                        messageCode = verifyTelNumBean.getMessage_code();
                        errorInfo = verifyTelNumBean.getError_info();
                        errorCode = verifyTelNumBean.getError_code();
                        telNum = verifyTelNumBean.getData().getUser_info().get(0).getUser_tel();
                    }
                    if (messageCode.equals(NET_STATE_SUCCESS)) {
                        tvBondPhoneNum.setText("没有绑定手机");
                        btnVerifyPhone.setEnabled(true);
                        btnVerifyPhone.setClickable(true);
                        btnSetNewPwd.setEnabled(false);
                        btnSetNewPwd.setClickable(false);
                    } else if (messageCode.equals(NET_STATE_FAILED)){
                        if (errorCode.equals(NET_STATE_BONDED)){
                            tvBondPhoneNum.setText("已绑定尾号为"+telNum.substring(7,Integer.valueOf(telNum.length()))+"的手机");
//                            telNum.substring(6,telNum.length()-1)
                        }else {
                            tvBondPhoneNum.setText("网络原因，稍后再试！"+errorCode+errorInfo);
                        }
                        btnVerifyPhone.setEnabled(false);
                        btnVerifyPhone.setClickable(false);
                        btnSetNewPwd.setEnabled(false);
                        btnSetNewPwd.setClickable(false);
                    }

                } catch (JsonSyntaxException e) {
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
            case WECHAT_LOGIN:
                setTitle("微信绑定手机");
                break;
            case QQ_LOGIN:
                setTitle("QQ绑定手机");
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
                boolean isPhoneNum = judgePhone(edPhoneNum);
                if (isPhoneNum){
                    phoneNumStr = edPhoneNum.getText().toString().trim();
                    startRockOn();//开始计时
                    getVerifyMessage();//不能再点了
                    getVerifyCode(phoneNumStr);
                }else {
                    tvBondPhoneNum.setText("手机号码格式不对");
                    btnVerifyPhone.setText("重新验证");
                }
                break;
            case R.id.btn_SetNewPwd://设置密码
//                phoneNumStr = edPhoneNum.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumStr)) {
                    ToastUtil.showLongToast("手机号码格式不对");
                    return;
                }
                verifyCodeStr = edVerifyCode.getText().toString().trim();
                if (TextUtils.isEmpty(verifyCodeStr) || verifyCodeStr.length() != 6) {
                    ToastUtil.showLongToast("验证码格式不对");
                    return;
                }
                //验证码是否正确
                String verifyCodeURL = USER_VERIFY_URL + "?user_tel=" + phoneNumStr + "&verify_code=" + verifyCodeStr;
                mOkHttpManager.getNet(verifyCodeURL, new OkHttpManager.ResultCallback() {
                    @Override
                    public void onFailed(Request request, IOException e) {
                        ToastUtil.showLongToast("网络原因验证失败！请稍后再试！");
                    }

                    @Override
                    public void onSuccess(String response) {
                        ToastUtil.showLongToast("验证成功");
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

    //验证手机号
    private boolean judgePhone(EditText edTelNum) {
        if (TextUtils.isEmpty(edTelNum.getText().toString().trim())) {
            ToastUtil.showLongToast("请输入您的手机号码");
            edTelNum.requestFocus();
            return false;
        } else if (edTelNum.getText().toString().trim().length() != 11) {
            ToastUtil.showLongToast("您的手机号码位数不正确");
            edTelNum.requestFocus();
            return false;
        } else {
            String number = edTelNum.getText().toString().trim();
            String num = "[1]\\d{10}";
            if (number.matches(num)){
                return true;
            }else {
                ToastUtil.showLongToast("请输入正确的手机号码");
                return false;
            }
        }
    }

    /**
     * @param phonenumstr
     */
    private void getVerifyCode(String phonenumstr) {
        mOkHttpManager.postNet(WECHAT_BOND_PHONE_CHECK_PHONE, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                tvBondPhoneNum.setText("网络错误"+request.toString());
            }

            @Override
            public void onSuccess(String response) {
                try {
                JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("message_code").equals(NET_STATE_SUCCESS)){
                        bondingType = jsonObject.getJSONObject("data").getString("binding_type");
                        Log.e(TAG, "binding_type"+ bondingType);
                        if (!bondingType.equals("")){
                            switch (bondingType){
                                case "1"://该手机号已经注册  GET_VERIFY_CODE_URL   /v1/message/send_message_2
                                    mOkHttpManager.getNet(GET_VERIFY_CODE_URL+"?user_tel="+phoneNumStr, new OkHttpManager.ResultCallback() {
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
                                                    btnSetNewPwd.setEnabled(true);
                                                    btnSetNewPwd.setClickable(true);
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
                                    mOkHttpManager.getNet(USER_REGISTER_GET_VERIFY_CODE_URL+"?user_tel="+phoneNumStr, new OkHttpManager.ResultCallback() {
                                        @Override
                                        public void onFailed(Request request, IOException e) {
                                            ToastUtil.showLongToast("网络连接失败");
                                        }

                                        @Override
                                        public void onSuccess(String response) {
                                            try{
                                                JSONObject jsonObject=new JSONObject(response);
                                                String messageCode = jsonObject.getString("message_code");
                                                if (messageCode.equals(NET_STATE_SUCCESS)){
                                                    tvBondPhoneNum.setText("已成功发送验证码,输入验证码并验证");
                                                    btnSetNewPwd.setEnabled(true);
                                                    btnSetNewPwd.setClickable(true);
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

                    }else if (jsonObject.getString("message_code").equals(NET_STATE_FAILED)){
//                        tvBondPhoneNum.setText("验证失败："+jsonObject.getString("error_code")+jsonObject.getString("error_info"));
                        tvBondPhoneNum.setText("验证失败："+jsonObject.getString("error_info"));
                        btnSetNewPwd.setEnabled(false);
                        btnSetNewPwd.setClickable(false);
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
            btnVerifyPhone.setEnabled(false);
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
        btnVerifyPhone.setClickable(false);
        btnSetNewPwd.setEnabled(true);
        btnSetNewPwd.setClickable(true);
    }

    /**
     * 开始计时
     */
    private void startRockOn() {
        time.start();//开始计时
    }
}

