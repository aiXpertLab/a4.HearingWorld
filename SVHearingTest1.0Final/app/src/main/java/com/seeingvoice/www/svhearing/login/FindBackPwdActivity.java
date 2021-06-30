package com.seeingvoice.www.svhearing.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.okhttpUtil.OkHttpManager;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import static com.seeingvoice.www.svhearing.AppConstant.GET_VERIFY_CODE_URL;
import static com.seeingvoice.www.svhearing.AppConstant.NET_STATE_FAILED;
import static com.seeingvoice.www.svhearing.AppConstant.NET_STATE_SUCCESS;
import static com.seeingvoice.www.svhearing.AppConstant.USER_VERIFY_URL;

/**
 * Date:2019/8/9
 * Time:9:05
 * auther:zyy
 */
public class FindBackPwdActivity extends TopBarBaseActivity implements View.OnClickListener {

    private static final String TAG = FindBackPwdActivity.class.getName();
    private TimeCount time;//验证码倒计时
    private Button btnSetNewPwd,btnGetVerifyCode;
    private String phoneNumStr,verifyCodeStr;//账户ID，和电话号码字符串
    private EditText edPhoneNum,edVerifyCode;
    private OkHttpManager mOkHttpManager;
    private TextView tvBondPhoneNum;
    private String messageCode,errorInfo,errorCode;

    @Override
    protected int getConentView() {
        return R.layout.activity_findback_pwd;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initTitleBar();//设置标题栏
        initViewData();//初始化控件，和计时器
        mOkHttpManager = OkHttpManager.getInstence();
    }

    /**
     * 设置标题栏
     */
    private void initTitleBar() {
        setTitle("找回密码");
        setTitleBack(true);
        setToolBarMenuOne("分享",R.mipmap.share_icon,null);
        setToolBarMenuOne("分享",R.mipmap.share_icon,null);
    }

    /**
     * 初始化控件，和计时器
     */
    private void initViewData() {
//        HideIMEUtil.wrap(this);
        btnGetVerifyCode = findViewById(R.id.btn_verify_code);
        edPhoneNum = findViewById(R.id.ed_phone_num);
        edVerifyCode = findViewById(R.id.ed_verify_code);
        btnSetNewPwd = findViewById(R.id.btn_SetNewPwd);
        btnGetVerifyCode.setOnClickListener(this);
        btnSetNewPwd.setOnClickListener(this);
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
        tvBondPhoneNum = findViewById(R.id.tv_state_hint);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_verify_code:
                phoneNumStr = edPhoneNum.getText().toString().trim();
                startRockOn();//开始计时
                getVerifyMessage();//不能再点了
                getVerifyCode(phoneNumStr);//获得验证码
                break;
            case R.id.btn_SetNewPwd://设置密码
                phoneNumStr = edPhoneNum.getText().toString().trim();
                verifyCodeStr = edVerifyCode.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumStr)){
                    ToastUtil.showLongToast("手机号码格式不对");
                    return;
                }
                if (TextUtils.isEmpty(verifyCodeStr) && verifyCodeStr.length() == 6){
                    ToastUtil.showLongToast("验证码格式不对");
                    return;
                }
                //验证码是否正确
                String verifyCodeURL = USER_VERIFY_URL+"?user_tel=" + phoneNumStr + "&verify_code=" + verifyCodeStr;
                mOkHttpManager.getNet(verifyCodeURL, new OkHttpManager.ResultCallback() {
                    @Override
                    public void onFailed(Request request, IOException e) {
                        Log.e(TAG,"验证失败"+request.toString());
                        ToastUtil.showShortToastCenter("网络错误，请稍后重试！");
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

                        if (messageCode.equals(NET_STATE_SUCCESS)){
                            try {
                                Log.e(TAG,"验证成功:"+response);
                                Intent mIntent = new Intent(FindBackPwdActivity.this, UpdatePwdActivity.class);
                                mIntent.putExtra("phoneNum",phoneNumStr);
                                startActivity(mIntent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (messageCode.equals(NET_STATE_FAILED)){
                            if (errorCode.equals("E100202")){
                                ToastUtil.showLongToastCenter("错误提示："+errorInfo);
                            }
                        }
                    }
                });
                break;
        }
    }

    private void getVerifyCode(String phonenumstr) {
        String URL;
        URL = GET_VERIFY_CODE_URL+"?user_tel="+phonenumstr;
        if (!TextUtils.isEmpty(phonenumstr) && phonenumstr.length() == 11){
            mOkHttpManager.getNet(URL, new OkHttpManager.ResultCallback() {
                @Override
                public void onFailed(Request request, IOException e) {
                    ToastUtil.showLongToast("网络原因，获得验证码失败");
                }

                @Override
                public void onSuccess(String response) {
                    Log.e("获得验证码", "onSuccess: "+response);
                    ToastUtil.showLongToast("获得验证码成功");
                    btnSetNewPwd.setVisibility(View.VISIBLE);
                }
            });
        }else {
            ToastUtil.showLongToast("手机号码格式不对");
        }


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
            btnGetVerifyCode.setClickable(false);
            btnGetVerifyCode.setText("剩余" + time / 1000 + "秒");
        }

        @Override
        public void onFinish() {//计时完毕时触发
            btnGetVerifyCode.setText("重新获取");
            btnGetVerifyCode.setEnabled(true);
            btnGetVerifyCode.setClickable(true);
        }
    }

    /**
     * 获取验证码请求
     */
    private void getVerifyMessage() {
        //验证码获取成功后
        btnGetVerifyCode.setEnabled(false);
    }

    /**
     * 开始计时
     */
    private void startRockOn() {
        time.start();//开始计时
    }

    //这里通过使用一个flag来进行判断目前的密码框是可见还是不可见，然后通过这个flag来对editText进行操作
//    private boolean flag = false;
//    public void lookPassword() {
//        if(flag){
//            //不可见
//            edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            //首先得到密码的字段，然后通过得到其长度将光标定位在最后一位
//            // （因为点了按钮之后光标会移动到已输入密码的最前端）
//            String password = edit_password.getText().toString();
//            edit_password.setSelection(password.length());
//            flag = ! flag;
//        }else{
//            //可见
//            edit_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//            String password = edit_password.getText().toString();
//            edit_password.setSelection(password.length());
//            flag = !flag;
//        }
//    }
}
