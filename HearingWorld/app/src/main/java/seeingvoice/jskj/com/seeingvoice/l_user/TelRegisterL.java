package seeingvoice.jskj.com.seeingvoice.l_user;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.okhttpUtil.OkHttpManager;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

public class TelRegisterL extends MyTopBar implements View.OnClickListener {

    private Button btnGetVerifyCode,btnVerifySubbmit;
    private TimeCount time;
    private OkHttpManager mOkHttpManager;
    private EditText mEd_TelNum,mEd_VerifyNum;
    private String phomeNum,verifyCode;

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_tel_register;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("注册手机账号");
        setToolbarBack(true);
        setToolBarMenuOne("分享",R.mipmap.share_icon,null);
        setToolBarMenuOne("分享",R.mipmap.share_icon,null);

        initViewData();
    }

    private void initViewData() {
//        HideIMEUtil.wrap(this);
        mEd_TelNum = findViewById(R.id.ed_tel_number);
        mEd_VerifyNum = findViewById(R.id.ed_verify_code);
        btnGetVerifyCode = findViewById(R.id.get_verfiy_code);
        btnVerifySubbmit = findViewById(R.id.user_verfiy_submit);
        btnGetVerifyCode.setOnClickListener(this);
        btnVerifySubbmit.setOnClickListener(this);
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
        mOkHttpManager = OkHttpManager.getInstence();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.get_verfiy_code:
                boolean isPhoneNum = judgePhone(mEd_TelNum);
                if (isPhoneNum){
                    startRockOn();//开始倒计时
                    Log.e("phoneNull","手机号码：get_verfiy_code"+phomeNum);
                    getNetVerifyCode(phomeNum,"");//向服务端发送手机号，获得验证码
                }else {
                    ToastUtil.showLongToastCenter("手机号码格式不对");
                    return;
                }
//                getVerifyMessage();
                break;
            case R.id.user_verfiy_submit:
                verifyCode = mEd_VerifyNum.getText().toString().trim();
                phomeNum = mEd_TelNum.getText().toString().trim();
                if (TextUtils.isEmpty(verifyCode)){
                    ToastUtil.showLongToastCenter("请输入验证码！");
                    return;
                }
                if (TextUtils.isEmpty(phomeNum)){
                    ToastUtil.showLongToastCenter("请输入手机号！");
                    return;
                }
                getNetVerifyCode(phomeNum,verifyCode);//向服务端发送手机号，获得验证码

//                Intent intent = new Intent(TelRegisterActivity.this,TelRegisterUserInfo.class);
//                startActivity(intent);
//                getNetVerifyCode(phomeNum,verifyCode);
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
                phomeNum = mEd_TelNum.getText().toString().trim();
                return true;
            }else {
                ToastUtil.showLongToast("请输入正确的手机号码");
                return false;
            }
        }
    }

    private void getNetVerifyCode(final String phoneNumstr, String verifyCodeStr) {
            if (verifyCodeStr.isEmpty()){
            }
    }

    private void startRockOn() {
        time.start();//开始计时
    }

    /**
     * 验证码倒计时
     */
    class TimeCount extends CountDownTimer {
        TimeCount(long millisInFuture, long countDownInterval) {
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
}
