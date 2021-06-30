package com.reapex.sv.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.reapex.sv.R;
import com.reapex.sv.base.BaseActivity;
import com.reapex.sv.base.ServerDataResponse;
import com.reapex.sv.bean.UserInfoBean;
import com.reapex.sv.bean.UserInfoOutBean;
import com.reapex.sv.callback.JsonCallback;
import com.reapex.sv.util.L_CommonUtil;
import com.reapex.sv.util.L_LogUtils;
import com.reapex.sv.util.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import androidx.annotation.Nullable;

public class RegisterPerfectInfoActivity extends BaseActivity {

    private static final String TAG = RegisterPerfectInfoActivity.class.getSimpleName();

    private ImageButton mBackIb;
    private TextView titleTv;
    private EditText userNameEd;
    private EditText pwdEt  ;
    private EditText pwdCheckEt  ;
    private  TextView registerTv ;

    private String userNameStr;
    private String phoneStr;
    private String pwdStr;
    private String checkPwdStr;

    private UserInfoBean userInfo;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_register_perfectinfo);
        super.onCreate(savedInstanceState);


    }

    @Override
    public void initViews() {
        mBackIb = findViewById(R.id.backbutton);
        titleTv = findViewById(R.id.title_text);
        userNameEd = findViewById(R.id.phone_Et);
        pwdEt   = findViewById(R.id.code_Et);
        pwdCheckEt   = findViewById(R.id.pwd_confirm_Et);
        registerTv  = findViewById(R.id.register_bt);
    }

    @Override
    public void initDatas() {
        phoneStr = getIntent().getStringExtra("phone");
        titleTv.setText("注册");
    }

    @Override
    public void installListeners() {
        mBackIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pwdEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        pwdCheckEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void registerCheck(){
        userNameStr =userNameEd.getText().toString().trim();
        pwdStr = pwdEt.getText().toString().trim();
        checkPwdStr = pwdCheckEt.getText().toString().trim();

        if(userNameStr == null || userNameStr.equals("")){
            L_CommonUtil.showToast(this,"用户名不能为空。");
            return ;
        }else if(pwdStr == null || pwdStr.equals("")){
            L_CommonUtil.showToast(this,"请输入密码。");
            return ;
        }else if(checkPwdStr == null || checkPwdStr.equals("")){
            L_CommonUtil.showToast(this,"确认密码不能为空。");
            return ;
        }else if(!pwdStr.equals(checkPwdStr)){
            L_CommonUtil.showToast(this,"两次输入密码不一致。");
            return ;
        }else{
            perfectInfoFromServer();
        }
    }
    private void perfectInfoFromServer(){
        OkGo.<ServerDataResponse<UserInfoOutBean>>post(Urls.URL_POST_REGISTER)//
                .tag(this)//
                .params("user_tel",phoneStr)
                .params("user_name",userNameStr)
                .params("user_pwd",pwdStr)
                .execute(new JsonCallback<ServerDataResponse<UserInfoOutBean>>() {
                    @Override
                    public void onSuccess(Response<ServerDataResponse<UserInfoOutBean>> response) {
                        if(L_CommonUtil.isHttpSuccess(response.body().message_code)) {
                            if(response.body()!=null && response.body().data!=null) {
                                userInfo = response.body().data.getUser_info();
                                L_LogUtils.d(TAG, "注册成功");
//                                getHandler().obtainMessage(USER_REGISTER_SUCCESS).sendToTarget();
                            }else{
                                L_LogUtils.d(TAG, "注册成功但是返回信息为空");
                            }
                        }else {
                            L_LogUtils.d(TAG, "注册失败失败");
                            L_CommonUtil.showToast(getActivity(), response.body().error_info);
                        }
                    }

                    @Override
                    public void onError(Response<ServerDataResponse<UserInfoOutBean>> response) {
//                        super.onError(response);
                        L_LogUtils.d(TAG,"获取code onError");
                        if(response != null) {
                            Throwable throwable = response.getException();
//                            Log.d("waha",throwable.toString()+"1"+throwable.getMessage()+"2"+throwable.getLocalizedMessage()+"3"+throwable.getCause());
                            if(throwable!= null && L_CommonUtil.catchNetException(throwable.toString())){
//                                Log.d("waha","进入一");
//                                getHandler().obtainMessage(OKGO_ON_ERROR).sendToTarget();
                            }else{
//                                Log.d("waha","进入2");
                            }
                        }
                    }
                });
    }
    @Override
    public void processHandlerMessage(Message msg) {

    }
}
