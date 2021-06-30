package seeingvoice.jskj.com.seeingvoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.base.OnMultiClickListener;
import seeingvoice.jskj.com.seeingvoice.beans.QQLoginInfoBean;
import seeingvoice.jskj.com.seeingvoice.beans.TelLoginBean;
import seeingvoice.jskj.com.seeingvoice.beans.WechatLoginInfoBean;
import seeingvoice.jskj.com.seeingvoice.bondphone.WECHATbondPhoneL;
import seeingvoice.jskj.com.seeingvoice.ui.headImgView;
import seeingvoice.jskj.com.seeingvoice.util.SpObjectUtil;

public class Personal_info_L extends MyTopBar {

    private TextView tv_Name, tv_Bond,tv_Age,tv_Gender,tv_Bond_Info,tv_Login_Type;
    private headImgView headImg;
    private WechatLoginInfoBean mWechatLoginInfoBean;
    private QQLoginInfoBean mQQLoginInfoBean;
    private String user_name,url;
    private Integer age,gender;
    private boolean isBondTelFlag = false;

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_personal_info_activity;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("个人信息");
        setToolbarBack(true);

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


        //从登陆状态判断用户信息
        if (MyApp.isLogin){//如果用户处于登陆状态
            switch (MyApp.loginType){//对登陆类型判断
                case MyData.PHONE_LOGIN://如手机登陆
                    MyApp.mTelLoginBean = SpObjectUtil.getObject(MyApp.getAppContext(), TelLoginBean.class);
                    tv_Name.setText(MyApp.mTelLoginBean.getData().getUser_info().get(0).getUser_tel()+"你好！");
                    tv_Bond.setVisibility(View.GONE);
                    if (MyApp.mTelLoginBean.getData().getUser_info().get(0).getUser_sex() == 1){
                        headImg.setImageResource(R.drawable.ic_woman);
                    }else {
                        headImg.setImageResource(R.drawable.ic_man);
                    }
                    tv_Login_Type.setText("登陆类型：手机登陆");
                    age = MyApp.mTelLoginBean.getData().getUser_info().get(0).getUser_age();
                    gender = MyApp.mTelLoginBean.getData().getUser_info().get(0).getUser_sex();
                    tv_Bond_Info.setVisibility(View.GONE);
                    findViewById(R.id.line5).setVisibility(View.GONE);
                    break;
                case MyData.WECHAT_LOGIN://如微信登陆
                    mWechatLoginInfoBean = SpObjectUtil.getObject(MyApp.getAppContext(), WechatLoginInfoBean.class);
                    url = mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_headimgurl();
//                    login_type = MyApplication.loginType;
//                    user_id = MyApplication.userId;
                    user_name = mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_name();
                    tv_Name.setText(user_name+"您好！");
                    headImg.setImageURL(url);
                    tv_Login_Type.setText("登陆类型：微信登陆");
//                    age = Integer.valueOf(mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_age());
                    gender = mWechatLoginInfoBean.getData().getUser_info().get(0).getUser_sex();
//                    is_Bonded = Integer.valueOf(mWechatLoginInfoBean.getData().getUser_info().get(0).getIs_verify_tel());
                    isBondedTelFunction();
                    break;
                case MyData.QQ_LOGIN://如QQ登陆
                    mQQLoginInfoBean = SpObjectUtil.getObject(MyApp.getAppContext(), QQLoginInfoBean.class);
                    url = mQQLoginInfoBean.getData().getUser_info().get(0).getUser_headimgurl();
//                    login_type = MyApplication.loginType;
//                    user_id = MyApplication.userId;
                    user_name = mQQLoginInfoBean.getData().getUser_info().get(0).getUser_name();
                    tv_Name.setText(user_name+"您好！");
                    headImg.setImageURL(url);
                    tv_Login_Type.setText("登陆类型：QQ登陆");
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
    }

    //微信绑定手机号
    private void bondPhoneFunction() {
        Intent mIntent = null;
        mIntent = new Intent(Personal_info_L.this, WECHATbondPhoneL.class);
        startActivity(mIntent);
    }


}
