package com.seeingvoice.www.svhearing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.Request;
import com.seeingvoice.www.svhearing.base.OnMultiClickListener;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.beans.QQLoginInfoBean;
import com.seeingvoice.www.svhearing.beans.TelLoginBean;
import com.seeingvoice.www.svhearing.beans.VerifyTelNumBean;
import com.seeingvoice.www.svhearing.beans.WechatLoginInfoBean;
import com.seeingvoice.www.svhearing.okhttpUtil.OkHttpManager;
import com.seeingvoice.www.svhearing.ui.headImgView;
import com.seeingvoice.www.svhearing.util.SpObjectUtil;

import static com.seeingvoice.www.svhearing.AppConstant.CHECK_WECHAT_BOND_PHONE_STATE;
import static com.seeingvoice.www.svhearing.AppConstant.NET_STATE_BONDED;
import static com.seeingvoice.www.svhearing.AppConstant.NET_STATE_FAILED;
import static com.seeingvoice.www.svhearing.AppConstant.NET_STATE_SUCCESS;

public class personal_info_activity extends TopBarBaseActivity {

    private TextView tv_Name, tv_Bond,tv_Age,tv_Gender,tv_Bond_Info,tv_Login_Type;
    private headImgView headImg;
    private WechatLoginInfoBean mWechatLoginInfoBean;
    private QQLoginInfoBean mQQLoginInfoBean;
    private String user_name,url;
    private Integer age,gender;
    private boolean isBondTelFlag = false;

    @Override
    protected int getConentView() {
        return R.layout.activity_personal_info_activity;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("个人信息");
        setTitleBack(true);

        setToolBarMenuOne("", R.mipmap.return_icon, null);
        setToolBarMenuTwo("", R.mipmap.return_icon, null);
        //以上是标题栏

        //初始化控件
        tv_Name = findViewById(R.id.textView);
        tv_Bond = findViewById(R.id.tv_bond);
        headImg = findViewById(R.id.img_header);
        tv_Age = findViewById(R.id.tv_age);
        tv_Gender = findViewById(R.id.tv_gender);
        tv_Bond_Info = findViewById(R.id.tv_bond_info);
        tv_Bond = findViewById(R.id.tv_bond);
        tv_Login_Type = findViewById(R.id.tv_login_type);


        //从登录状态判断用户信息
        if (MyApplication.isLogin){//如果用户处于登录状态
            switch (MyApplication.loginType){//对登录类型判断
                case AppConstant.PHONE_LOGIN://如手机登录
                    MyApplication.mTelLoginBean = SpObjectUtil.getObject(MyApplication.getAppContext(), TelLoginBean.class);
                    tv_Name.setText(MyApplication.mTelLoginBean.getData().getUser_info().get(0).getUser_tel()+"你好！");
                    tv_Bond.setVisibility(View.GONE);
                    if (MyApplication.mTelLoginBean.getData().getUser_info().get(0).getUser_sex() == 1){
                        headImg.setImageResource(R.drawable.ic_woman);
                    }else {
                        headImg.setImageResource(R.drawable.ic_man);
                    }
                    tv_Login_Type.setText("登录类型：手机登录");
                    age = MyApplication.mTelLoginBean.getData().getUser_info().get(0).getUser_age();
                    gender = MyApplication.mTelLoginBean.getData().getUser_info().get(0).getUser_sex();
                    tv_Bond_Info.setVisibility(View.GONE);
                    findViewById(R.id.line5).setVisibility(View.GONE);
                    break;
                case AppConstant.WECHAT_LOGIN://如微信登录
                    mWechatLoginInfoBean = SpObjectUtil.getObject(MyApplication.getAppContext(), WechatLoginInfoBean.class);
                    url = mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_headimgurl();
//                    login_type = MyApplication.loginType;
//                    user_id = MyApplication.userId;
                    user_name = mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_name();
                    tv_Name.setText(user_name+"您好！");
                    headImg.setImageURL(url);
                    tv_Login_Type.setText("登录类型：微信登录");
//                    age = Integer.valueOf(mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_age());
                    gender = mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_sex();
//                    is_Bonded = Integer.valueOf(mWechatLoginInfoBean.getData().getUser_info().get(0).getIs_verify_tel());
                    isBondedTelFunction();
                    break;
                case AppConstant.QQ_LOGIN://如QQ登录
                    mQQLoginInfoBean = SpObjectUtil.getObject(MyApplication.getAppContext(), QQLoginInfoBean.class);
                    url = mQQLoginInfoBean.getData().getUser_info().get(0).getUser_headimgurl();
//                    login_type = MyApplication.loginType;
//                    user_id = MyApplication.userId;
                    user_name = mQQLoginInfoBean.getData().getUser_info().get(0).getUser_name();
                    tv_Name.setText(user_name+"您好！");
                    headImg.setImageURL(url);
                    tv_Login_Type.setText("登录类型：QQ登录");
                    age = Integer.valueOf(mQQLoginInfoBean.getData().getUser_info().get(0).getUser_age());
                    gender = mQQLoginInfoBean.getData().getUser_info().get(0).getUser_sex();
//                    is_Bonded = Integer.valueOf(mQQLoginInfoBean.getData().getUser_info().get(0).getIs_verify_tel());
                    isBondedTelFunction();
                    break;
            }
        }
        tv_Age.setText("年龄："+age);
        if (gender == 2){
            tv_Gender.setText("性别：男");
        }else {
            tv_Gender.setText("性别：女");
        }

        tv_Bond.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                bondPhoneFunction();
            }
        });
    }

    /**
     * 是否绑定手机号
     */
    private void isBondedTelFunction() {
        OkHttpManager.getInstence().postNet(CHECK_WECHAT_BOND_PHONE_STATE, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {

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
                        isBondTelFlag = false;
                        tv_Bond.setVisibility(View.VISIBLE);
                        tv_Bond_Info.setText("是否绑定手机：未绑定，请先绑定！");
                    } else if (messageCode.equals(NET_STATE_FAILED)){
                        if (errorCode.equals(NET_STATE_BONDED)){
                            isBondTelFlag = true;
                            tv_Bond_Info.setText("是否绑定手机：已绑定尾号为"+telNum.substring(7,Integer.valueOf(telNum.length()))+"的手机");
                            tv_Bond.setVisibility(View.INVISIBLE);
                        }else {
                            tv_Bond_Info.setText("是否绑定手机："+errorInfo);
                        }
                    }

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

        }, new OkHttpManager.Param("user_id", String.valueOf(MyApplication.userId)));
    }

    //微信绑定手机号
    private void bondPhoneFunction() {
        Intent mIntent = null;
        mIntent = new Intent(personal_info_activity.this, com.seeingvoice.www.svhearing.bondphone.WECHATbondPhoneActivity.class);
        startActivity(mIntent);
    }


}
