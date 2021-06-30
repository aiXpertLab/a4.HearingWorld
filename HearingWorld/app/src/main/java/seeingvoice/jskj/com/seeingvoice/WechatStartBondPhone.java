package seeingvoice.jskj.com.seeingvoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import seeingvoice.jskj.com.seeingvoice.l_user.UpdatePwdL;
import seeingvoice.jskj.com.seeingvoice.okhttpUtil.OkHttpManager;

@SuppressWarnings("FieldCanBeLocal")
public class WechatStartBondPhone extends MyTopBar implements View.OnClickListener {

    private Intent mIntent;
    private EditText edPsw,edRepeatPsw;
    private String phoneNumStr,bondType;//bondType 1 是手机号注册了  2 是手机号没注册
    private Button btnComfirm;
    private OkHttpManager mOkHttpManager;
    private final static String TAG = UpdatePwdL.class.getName();
    private String userPswStr,messageCode,errorInfo,errorCode;

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_wechat_start_bond_phone;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("微信绑定手机-设置密码");
        setToolbarBack(true);
        setToolBarMenuOne(null,R.mipmap.share_icon,null);
        setToolBarMenuTwo(null,R.mipmap.share_icon,null);
        edPsw = findViewById(R.id.ed_password);
        edRepeatPsw = findViewById(R.id.ed_repeat_password);
        btnComfirm = findViewById(R.id.btn_comfirm);
        btnComfirm.setOnClickListener(this);

        mIntent = getIntent();
        phoneNumStr = mIntent.getStringExtra("phoneNum");
        bondType = mIntent.getStringExtra("binding_type");
        mOkHttpManager = OkHttpManager.getInstence();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_comfirm:
                switch (MyApp.loginType){
                    case MyData.WECHAT_LOGIN:
                        break;
                }
                if (edPsw.getText().toString().trim().equals(edRepeatPsw.getText().toString().trim()) && !edPsw.getText().toString().trim().isEmpty()){
                    userPswStr = edPsw.getText().toString().trim();

                }
                break;

        }
    }
}
