package seeingvoice.jskj.com.seeingvoice.smsverify;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import seeingvoice.jskj.com.seeingvoice.MainActivity;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.okhttpUtil.OkHttpManager;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Date:2019/7/10
 * Time:11:36
 * auther:zyy
 */
public class VerifyPopupWindow {
    /**
     * 手机验证popupwindow
     */
    private EditText phone_number;
    private EditText input_image_code;
    private ImageView image_code;
    private EditText input_code;
    private Button sure;
    private String inputcode;
    private String realCode;
    private CustomPopWindow mPopWindow;
    private Button get_verfiy_code;
    private Context mContext;
    private OkHttpManager mOkHttpManager;
    private TextView no_verify;

    public VerifyPopupWindow(Context context){
        mOkHttpManager = OkHttpManager.getInstence();
        this.mContext=context;
    }

    public void showPopupWindow(){

        final View contentView = LayoutInflater.from(mContext).inflate(R.layout.user_phone_number, null);
        phone_number = contentView.findViewById(R.id.phone_number);    //填入手机号
        input_image_code = contentView.findViewById(R.id.input_image_code);//填入图片验证码
        image_code = contentView.findViewById(R.id.image_code);       //图片验证码
        input_code = contentView.findViewById(R.id.input_code);       //填入手机验证码
        sure = contentView.findViewById(R.id.user_verfiy_submit);        //确定绑定
        no_verify = contentView.findViewById(R.id.ignore_verfiy_submit); //暂不验证
        get_verfiy_code = contentView.findViewById(R.id.get_verfiy_code);//获取手机验证码

        /*使用倒计时*/
        final CountDownTimer timer = new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                get_verfiy_code.setText(millisUntilFinished/1000 + "秒");
            }

            @Override
            public void onFinish() {
                get_verfiy_code.setEnabled(true);
                get_verfiy_code.setText("获取验证码");
            }
        };

        image_code.setImageBitmap(Code.getInstance().createBitmap());     //初始化验证码
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.image_code:
                        image_code.setImageBitmap(Code.getInstance().createBitmap());
                        break;
                    case R.id.user_verfiy_submit:
                        if (TextUtils.isEmpty(phone_number.getText().toString())){
                            Toast.makeText(mContext,"请您输入手机号码", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(input_code.getText().toString())){
                            Toast.makeText(mContext,"请您输入手机验证码", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    case R.id.ignore_verfiy_submit:
                        Intent intent = new Intent(mContext, MainActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case R.id.get_verfiy_code:
                        //  MyCountDownTimer.start();
                        inputcode = input_image_code.getText().toString();
                        realCode  = Code.getInstance().getCode();
                        if (TextUtils.isEmpty(phone_number.getText().toString())){
                            Toast.makeText(mContext,"请您输入手机号码", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (realCode.equalsIgnoreCase(inputcode)){
                            //发送手机信息到服务器，通知服务端发验证码，并获取验证码。
                            getVerifyCode(phone_number.getText().toString());
                        }else{
                            Toast.makeText(mContext,"您输入的验证码有误，请重新输入", Toast.LENGTH_SHORT).show();
                        }
                        get_verfiy_code.setEnabled(false);
                        //倒计时开始
                        timer.start();
                        break;
                }
            }
        };
        image_code.setOnClickListener(listener);
        sure.setOnClickListener(listener);
        no_verify.setOnClickListener(listener);
        get_verfiy_code.setOnClickListener(listener);

        mPopWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                .setView(contentView)
                .setFocusable(true)
                .setOutsideTouchable(false)// 设置点击PopupWindow之外的地方，popWindow不关闭，如果不设置这个属性或者为true，则关闭
                .setTouchIntercepter(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
//                        if (!isOutsideTouchable()) {
//                            View mView = getContentView();
//                            if (null != mView)
//                                mView.dispatchTouchEvent(event);
//                        }
//                        return isFocusable() && !isOutsideTouchable();
                        if (null != contentView){//点击外部不消失，点击contentView可以打开软键盘
                            contentView.dispatchTouchEvent(event);
                        }
                        return true;
                    }
                })
                .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        Log.e("TAG","onDismiss");
                    }
                })
                .create();

        mPopWindow.showAtLocation(contentView, Gravity.CENTER, 0, 0);
    }

    //验证手机号
    private boolean judPhone() {
        if (TextUtils.isEmpty(phone_number.getText().toString().trim())) {
            ToastUtil.showLongToast("请输入您的电话号码");
            phone_number.requestFocus();
            return false;
        } else if (phone_number.getText().toString().trim().length() != 11) {
            ToastUtil.showLongToast("您的电话号码位数不正确");
            phone_number.requestFocus();
            return false;
        } else {
            String number = phone_number.getText().toString().trim();
            String num = "[1][358]\\d{9}";
            if (number.matches(num))
                return true;
            else {
                ToastUtil.showLongToast("请输入正确的手机号码");
                return false;
            }
        }
    }

    /**
     * 判断手机号是否符合规范
     * @param phoneNo 输入的手机号
     * @return
     */
    public static boolean isPhoneNumber(String phoneNo) {
        if (TextUtils.isEmpty(phoneNo)) {
            return false;
        }
        if (phoneNo.length() == 11) {
            for (int i = 0; i < 11; i++) {
                if (!PhoneNumberUtils.isISODigit(phoneNo.charAt(i))) {
                    return false;
                }
            }
            Pattern p = Pattern.compile("^((13[^4,\\D])" + "|(134[^9,\\D])" +
                    "|(14[5,7])" +
                    "|(15[^4,\\D])" +
                    "|(17[3,6-8])" +
                    "|(18[0-9]))\\d{8}$");
            Matcher m = p.matcher(phoneNo);
            return m.matches();
        }
        return false;
    }

    //把手机后台获取手机验证码
    public void getVerifyCode(String phoneNumber){

        judPhone();
    }
}
