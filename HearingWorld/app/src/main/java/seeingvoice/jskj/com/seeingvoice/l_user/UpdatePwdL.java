package seeingvoice.jskj.com.seeingvoice.l_user;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.okhttpUtil.OkHttpManager;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdatePwdL extends MyTopBar implements View.OnClickListener {
    private Intent mIntent;
    private int bondType,userId;
    private EditText edPsw,edRepeatPsw;
    private String StrPsw,phoneNumStr;
    private Button btnComfirm;
    private OkHttpManager mOkHttpManager;
    private final static String TAG = UpdatePwdL.class.getName();
    private String URL,userPswStr,messageCode,errorInfo,errorCode;
    private TextView mPwdHint;

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_update_pwd;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("密码找回-重置密码");
        setToolbarBack(true);
        setToolBarMenuOne(null,R.mipmap.share_icon,null);
        setToolBarMenuTwo(null,R.mipmap.share_icon,null);
        edPsw = findViewById(R.id.ed_password);
        edRepeatPsw = findViewById(R.id.ed_repeat_password);
        setEditTextInhibitInputSpace(edPsw);
        setEditTextInhibitInputSpace(edRepeatPsw);
        mPwdHint = findViewById(R.id.pwd_hint);
        btnComfirm = findViewById(R.id.btn_comfirm);
        btnComfirm.setOnClickListener(this);
        if (edPsw.getText().toString().trim().equals(edRepeatPsw.getText().toString().trim())){
            userPswStr = edRepeatPsw.getText().toString().trim();
        }
        mIntent = getIntent();
        phoneNumStr = mIntent.getStringExtra("phoneNum");

//        if (MyApplication.loginType == WECHAT_LOGIN) {
//            phoneNumStr = mIntent.getStringExtra("phoneNum");
//            userId = MyApplication.userId;
////            URL = WECHAT_BOND_PHONE + "?user_tel=" + phoneNumStr + "&user_id=" + MyApplication.userId + "&user_pwd=" + userPswStr;
//            URL = COMFIRM_FINDBACK_PWS_URL + "?user_tel=" + phoneNumStr + "&user_new_pwd=" +userPswStr;
//        }
        mOkHttpManager = OkHttpManager.getInstence();
    }

    /**
     * 禁止EditText输入空格
     * @param editText
     */
    public void setEditTextInhibitInputSpace(EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(" ")){
                    UpdatePwdL.this.mPwdHint.setText("密码不允许输入空格！");
                  ToastUtil.showLongToastCenter("密码不允许输入空格！");
                    return "";
                } else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /**
     * 禁止EditText输入特殊字符
     * @param editText
     */
    public static void setEditTextInhibitInputSpeChat(EditText editText){

        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if(matcher.find())return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_comfirm:
                int length = edPsw.getText().toString().trim().length();
                if (length < 32 && length >= 8 ){
                    if (edPsw.getText().toString().trim().equals(edRepeatPsw.getText().toString().trim()) && !edPsw.getText().toString().trim().isEmpty()){
                        userPswStr = edPsw.getText().toString().trim();
                    }
                }else {
                    mPwdHint.setText("密码长度应该不少于8位！");
                }
                break;

        }
    }
}
