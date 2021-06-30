package seeingvoice.jskj.com.seeingvoice.bondphone;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.MyApp;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.okhttpUtil.OkHttpManager;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

import static seeingvoice.jskj.com.seeingvoice.MyData.QQ_LOGIN;
import static seeingvoice.jskj.com.seeingvoice.MyData.WECHAT_LOGIN;

public class WECHATbondPhoneL extends MyTopBar implements View.OnClickListener {

    private static final String TAG = WECHATbondPhoneL.class.getName();
    private WECHATbondPhoneL.TimeCount time;//验证码倒计时
    private Button btnSetNewPwd,btnVerifyPhone;
    private String phoneNumStr, verifyCodeStr;//账户ID，和电话号码字符串
    private EditText edPhoneNum, edVerifyCode;
    private OkHttpManager mOkHttpManager;
    private TextView tvBondPhoneNum;
    private String bondingType;

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_wechatbond_phone;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initTitleBar();//设置标题栏
        initViewData();//初始化控件，和计时器
    }

    /**
     * 设置标题栏
     */
    private void initTitleBar() {
        switch (MyApp.loginType) {
            case WECHAT_LOGIN:
                setToolbarTitle("微信绑定手机");
                break;
            case QQ_LOGIN:
                setToolbarTitle("QQ绑定手机");
                break;
        }
        setToolbarBack(true);
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
        time = new WECHATbondPhoneL.TimeCount(60000, 1000);//构造CountDownTimer对象
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

