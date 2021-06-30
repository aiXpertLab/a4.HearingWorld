package seeingvoice.jskj.com.seeingvoice.l_user;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.okhttpUtil.OkHttpManager;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

/**
 * Date:2019/8/9
 * Time:9:05
 * auther:zyy
 */
public class FindBackPwdL extends MyTopBar implements View.OnClickListener {

    private static final String TAG = FindBackPwdL.class.getName();
    private TimeCount time;//验证码倒计时
    private Button btnSetNewPwd,btnGetVerifyCode;
    private String phoneNumStr,verifyCodeStr;//账户ID，和电话号码字符串
    private EditText edPhoneNum,edVerifyCode;
    private OkHttpManager mOkHttpManager;
    private TextView tvBondPhoneNum;
    private String messageCode,errorInfo,errorCode;

    @Override
    protected int getContentView_sv() {
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
        setToolbarTitle("找回密码");
        setToolbarBack(true);
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
        }
    }

    private void getVerifyCode(String phonenumstr) {
        String URL;
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
