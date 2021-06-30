package com.seeingvoice.www.svhearing.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Request;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.okhttpUtil.OkHttpManager;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import static com.seeingvoice.www.svhearing.AppConstant.COMFIRM_FINDBACK_PWS_URL;
import static com.seeingvoice.www.svhearing.AppConstant.NET_STATE_SUCCESS;

public class UpdatePwdActivity extends TopBarBaseActivity implements View.OnClickListener {
    private Intent mIntent;
    private int bondType,userId;
    private EditText edPsw,edRepeatPsw;
    private String StrPsw,phoneNumStr;
    private Button btnComfirm;
    private OkHttpManager mOkHttpManager;
    private final static String TAG = UpdatePwdActivity.class.getName();
    private String URL,userPswStr,messageCode,errorInfo,errorCode;
    private TextView mPwdHint;

    @Override
    protected int getConentView() {
        return R.layout.activity_update_pwd;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("密码找回-重置密码");
        setTitleBack(true);
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
                    UpdatePwdActivity.this.mPwdHint.setText("密码不允许输入空格！");
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
                    if (!edPsw.getText().toString().trim().isEmpty() && !edRepeatPsw.getText().toString().trim().isEmpty()){
                        if (edPsw.getText().toString().trim().equals(edRepeatPsw.getText().toString().trim())){
                            userPswStr = edPsw.getText().toString().trim();
                            mOkHttpManager.postNet(COMFIRM_FINDBACK_PWS_URL,new OkHttpManager.ResultCallback() {
                                @Override
                                public void onFailed(Request request, IOException e) {
                                    Log.e(TAG,"微信绑定手机号码失败");
                                    mPwdHint.setText("网络错误，请稍后再试！");
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
                                        ToastUtil.showLongToastCenter("密码重置成功！");
                                        finish();
                                    }else {
                                        mPwdHint.setText("错误提示："+errorInfo);
                                    }
                                }
                            },new OkHttpManager.Param("user_tel",phoneNumStr),new OkHttpManager.Param("user_new_pwd",userPswStr));
                        }else {
                            mPwdHint.setText("两次密码输入不同！");
                        }
                    }else {
                        mPwdHint.setText("密码不能设置为空！");
                    }
                }else {
                    mPwdHint.setText("密码长度应该不少于8位！");
                }
                break;

        }
    }
}
