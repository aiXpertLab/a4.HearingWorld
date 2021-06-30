package com.seeingvoice.www.svhearing.login;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.okhttpUtil.OkHttpManager;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Request;
import com.seeingvoice.www.svhearing.AppConstant;

public class TelRegisterUserInfo extends TopBarBaseActivity implements DatePickerDialog.OnDateSetListener {

    private TextView mTvAge;
    private Intent mIntent;
    private String verify_code,phoneNumStr = "13800000000";
    private EditText mEd_user_name,mEd_user_psw,mEd_user_repeat_psw;
    private RadioGroup mRG_user_sex;
    private String user_age_str = "1000";
    private OkHttpManager mOkHttpManager;

    @Override
    protected int getConentView() {
        return R.layout.activity_tel_register_user_info;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mIntent = getIntent();
        phoneNumStr = mIntent.getStringExtra("phone_num");
        Log.e("phoneNull","手机号码：getIntent"+phoneNumStr);
        setTitle("注册资料填写");
        setTitleBack(true);
        setToolBarMenuOne("",R.mipmap.share_icon,null);
        setToolBarMenuTwo("",R.mipmap.share_icon,null);
        mTvAge = findViewById(R.id.tv_age);//用户年龄
        mEd_user_name = findViewById(R.id.et_user_name);
        mEd_user_psw = findViewById(R.id.ed_user_psw);
        mEd_user_repeat_psw = findViewById(R.id.ed_user_repeat_psw);
        mRG_user_sex = findViewById(R.id.rg_user_sex);
        try {
            mOkHttpManager = OkHttpManager.getInstence();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SelectAge(View v) {
        // 这设置默认显示的日期    我选的2000年1月1日                           月这是从0开始    0 是一月
        new DatePickerDialog(TelRegisterUserInfo.this, DatePickerDialog.THEME_HOLO_LIGHT,this, 1990, 0, 1).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//        选择1月会是0   要把月份+1
//        Toast.makeText(TelRegisterUserInfo.this, "你选择的是" + year + "年" + (monthOfYear+1) + "月" + dayOfMonth + "日",
//                Toast.LENGTH_LONG).show();
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        int NowYear = Integer.parseInt(format.format(new Date()));
        mTvAge.setText("芳龄：" + (NowYear - year) + "岁");
        user_age_str = String.valueOf(NowYear-year);
    }

    public void SubmitRegisterInfo(View v){
        if (TextUtils.isEmpty(mEd_user_name.getText().toString())
                || TextUtils.isEmpty(mEd_user_psw.getText().toString())
                || TextUtils.isEmpty(mEd_user_repeat_psw.getText().toString())){
            ToastUtil.showLongToast("您输入的用户名，或密码为空");
            return;
        }

        if (mRG_user_sex.getCheckedRadioButtonId() == -1){
            ToastUtil.showLongToast("请选择性别");
            return;
        }

        if (user_age_str.equals("1000")){
            ToastUtil.showLongToast("请选择年龄");
            return;
        }

        if (!mEd_user_psw.getText().toString().equals(mEd_user_repeat_psw.getText().toString())){
            ToastUtil.showLongToast("两次输入的密码不同");
            return;
        }

//        String url = USER_REGISTER_INFO_SUBBMIT_URL + "?user_name=" + 888 + "&user_tel=" + "18103519437" + "&user_sex=" + 1 + "&user_age=" + 18 + "&user_pwd=" + 123;
//        Log.e("111111111",url);
//        mOkHttpManager.getNet(url, new OkHttpManager.ResultCallback() {
//            @Override
//            public void onFailed(Request request, IOException e) {
//                ToastUtil.showLongToast("失败");
//            }
//
//            @Override
//            public void onSuccess(String response) {
//                ToastUtil.showLongToast("成功");
//            }
//        });


        mOkHttpManager.getNet(AppConstant.USER_REGISTER_INFO_SUBBMIT_URL+"?user_name="+mEd_user_name.getText().toString().trim()+"&user_tel="+phoneNumStr
                +"&user_sex="+mRG_user_sex.getCheckedRadioButtonId()+"&user_age="+user_age_str+"&user_pwd="+mEd_user_psw.getText().toString().trim()
                , new OkHttpManager.ResultCallback() {
                    @Override
            public void onFailed(Request request, IOException e) {
                ToastUtil.showLongToast("网络错误，请稍后再试！");
            }

            @Override
            public void onSuccess(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("message_code");
                    if (status.equals(AppConstant.NET_STATE_SUCCESS)){
                        Log.e("phoneNull","手机号码："+phoneNumStr);
                        ToastUtil.showLongToast("恭喜您！注册成功");
                        toNextActivity(null,TelRegisterUserInfo.this,LoginActivity.class);
                        finish();
                    }else {
                        String message = jsonObject.getString("error_info");
                        if (message!=null){
                            ToastUtil.showLongToast(message);

                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


}
