package com.reapex.sv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.reapex.sv.R;
import com.reapex.sv.base.BaseActivity;
import com.reapex.sv.base.ServerDataResponse;
import com.reapex.sv.callback.JsonCallback;
import com.reapex.sv.util.L_CommonUtil;
import com.reapex.sv.util.L_LogUtils;
import com.reapex.sv.util.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import androidx.annotation.Nullable;

public class Register1Activity extends BaseActivity {

    private static final String TAG = Register1Activity.class.getSimpleName();

    private EditText mphoneEt ;
    private EditText mcheckCodeEt ;
    private TextView mgetCodeTv ;
    private TextView mregisterTv ;
    private TextView mloginTv ;


    private static final int COUNTDOWN = 0;
    private static final int REGISTERSUCCESS = 1;
    private Runnable mRunnable;
    private int mPhone_CountDown = 0;

    private String mStrPhone;
    private String mStrCode;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_register1);
        super.onCreate(savedInstanceState);


    }

    @Override
    public void initViews() {
        mphoneEt = findViewById(R.id.phone_Et);
        mcheckCodeEt = findViewById(R.id.code_Et);
        mgetCodeTv = findViewById(R.id.get_code_tv);
        mregisterTv = findViewById(R.id.register_bt);
        mloginTv = findViewById(R.id.pwd_login);
    }

    @Override
    public void initDatas() {
        mRunnable = new Runnable() {

            @Override
            public void run() {
                try{
                    Message message = getHandler().obtainMessage(COUNTDOWN, null);
                    message.sendToTarget();
                }catch (Exception e) {
                }
            }
        };
    }

    @Override
    public void installListeners() {
        mphoneEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mcheckCodeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mgetCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(L_CommonUtil.hasNet()){
                    mStrPhone = mphoneEt.getText().toString().trim();
                    if(mStrPhone.equals("")){
                        L_CommonUtil.showToast(getActivity(), getResources().getString(R.string.notice_phone_empty));
                        return;
                    }
//                    if(VerificationHelper.checkMobile(mStrPhone)){
                    if (mPhone_CountDown == 0) {
                        getCheckCode();
                    }
                }else{
                    L_CommonUtil.showToast(getActivity(),R.string.notice_internet_fail);
                }
            }
        });
        mregisterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mStrPhone = mphoneEt.getText().toString().trim();
                if(mStrPhone.equals("")){
                    L_CommonUtil.showToast(getActivity(), getResources().getString(R.string.notice_phone_empty));
                    return;
                }
                mStrCode = mcheckCodeEt.getText().toString().trim();
                if(mStrCode.equals("")){
                    L_CommonUtil.showToast(getActivity(), R.string.notice_code_empty);
                    return;
                }
                registerStepOne();
            }

        });
        mloginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void processHandlerMessage(Message msg) {
        switch (msg.what){
            case COUNTDOWN:
                mPhone_CountDown--;
                if (mPhone_CountDown > 0) {
                    setRequestEnable(false);
                } else {
                    setRequestEnable(true);
                }
                break;
            case REGISTERSUCCESS:
//                String token = (String) msg.obj;
                Intent intent = new Intent(getActivity(),RegisterPerfectInfoActivity.class);
                intent.putExtra("phone",mStrPhone);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }

    public void getCheckCode(){
        OkGo.<ServerDataResponse<Object>>post(Urls.URL_GET_MESSAGE_CODE)//
                .tag(this)//
                .params("user_tel",mStrPhone)
                .params("message_type","1")
                .execute(new JsonCallback<ServerDataResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<ServerDataResponse<Object>> response) {
                        if(L_CommonUtil.isHttpSuccess(response.body().message_code)) {
                            L_CommonUtil.showToast(getActivity(), "手机验证码发送中..");
                            mPhone_CountDown = 60;
                            setRequestEnable(false);
                            L_LogUtils.d(TAG, "获取code成功");
                        }else {
                            L_LogUtils.d(TAG, "获取code失败");
                            L_CommonUtil.showToast(getActivity(), response.body().error_info);
                            setRequestEnable(true);
                        }
                    }

                    @Override
                    public void onError(Response<ServerDataResponse<Object>> response) {
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

    private void setRequestEnable(boolean value){
        if(value){
            mPhone_CountDown = 0;
            mgetCodeTv.setText(getResources().getString(R.string.requestcode));
        }else{
            mgetCodeTv.setText(mPhone_CountDown + "秒");
            getHandler().postDelayed(mRunnable, 1000);
        }
    }
    public void registerStepOne(){
        OkGo.<ServerDataResponse<Object>>post(Urls.URL_GET_MESSAGE_CODE_VERFY)//
                .tag(this)//
                .params("user_tel",mStrPhone)
                .params("verify_code",mcheckCodeEt.getText().toString().trim())
                .execute(new JsonCallback<ServerDataResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<ServerDataResponse<Object>> response) {
                        if(L_CommonUtil.isHttpSuccess(response.body().message_code)) {
                            getHandler().obtainMessage(REGISTERSUCCESS).sendToTarget();
                            L_LogUtils.d(TAG, "验证code成功");
                        }else {
                            L_LogUtils.d(TAG, "验证code失败");
                            L_CommonUtil.showToast(getActivity(), response.body().error_info);
                        }
                    }

                    @Override
                    public void onError(Response<ServerDataResponse<Object>> response) {
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
}
