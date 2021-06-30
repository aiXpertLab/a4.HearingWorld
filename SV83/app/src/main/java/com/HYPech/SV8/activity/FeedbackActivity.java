package com.HYPech.SV8.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.HYPech.SV8.R;
import com.HYPech.SV8.base.BaseActivity;
import com.HYPech.SV8.base.ServerDataResponse;
import com.HYPech.SV8.callback.JsonCallback;
import com.HYPech.SV8.util.CommonUtil;
import com.HYPech.SV8.util.LogUtils;
import com.HYPech.SV8.util.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import androidx.annotation.Nullable;
//import butterknife.BindView;
//import butterknife.ButterKnife;

public class FeedbackActivity extends BaseActivity {

    private static final String TAG = FeedbackActivity.class.getSimpleName();
    private static final int FEEDBACKSUCCESS = 1;
    //    @BindView(R.id.mimagebutton)
    ImageView mBack;
    TextView mTitle;
    EditText mfeedBackEt;
    EditText mPhoneEt;
    EditText mMailEt;
    EditText mQQEt;
    TextView mSubmitTv;

    String feedbackStr;
    String phoneStr;
    String mailStr;
    String qqStr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_feedback);
        super.onCreate(savedInstanceState);

//        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        mBack = this.findViewById(R.id.backbutton);
        mTitle = this.findViewById(R.id.title_text);
        mfeedBackEt = this.findViewById(R.id.feedback_et);
        mPhoneEt = this.findViewById(R.id.phone_et);
        mMailEt = this.findViewById(R.id.email_et);
        mQQEt = this.findViewById(R.id.qq_et);
        mSubmitTv = this.findViewById(R.id.submit);
    }

    @Override
    public void initDatas() {
        mTitle.setText("意见反馈");
    }

    @Override
    public void installListeners() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSubmitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidate();
            }
        });
    }

    @Override
    public void processHandlerMessage(Message msg) {
        switch (msg.what){

            case FEEDBACKSUCCESS:
                CommonUtil.showToast(FeedbackActivity.this,"意见提交成功。");
                finish();
                break;
            default:
                break;
        }
    }
    public void checkValidate(){
        feedbackStr = mfeedBackEt.getText().toString().trim();
        phoneStr = mPhoneEt.getText().toString().trim();
        mailStr = mMailEt.getText().toString().trim();
        qqStr = mQQEt.getText().toString().trim();

        if(feedbackStr == null || feedbackStr.equals("")){
            CommonUtil.showToast(FeedbackActivity.this,"请输入您宝贵的意见再点击提交。");
            return ;
        }
        feedbackHttp();
    }
    public void feedbackHttp() {
        OkGo.<ServerDataResponse<Object>>post(Urls.URL_POST_FEEDBACK)//
                .tag(this)//
                .params("feedback_text", feedbackStr)
                .params("contact_tel", phoneStr)
                .params("contact_mail", mailStr)
                .params("contact_qq", qqStr)
                .execute(new JsonCallback<ServerDataResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<ServerDataResponse<Object>> response) {
                        if (CommonUtil.isHttpSuccess(response.body().message_code)) {
                            getHandler().obtainMessage(FEEDBACKSUCCESS).sendToTarget();
                            LogUtils.d(TAG, "反馈意见成功");
                        } else {
                            LogUtils.d(TAG, "反馈意见失败");
                            CommonUtil.showToast(getActivity(), response.body().error_info);
                        }
                    }

                    @Override
                    public void onError(Response<ServerDataResponse<Object>> response) {
//                        super.onError(response);
                        LogUtils.d(TAG, "反馈意见 onError");
                        if (response != null) {
                            Throwable throwable = response.getException();
//                            Log.d("waha",throwable.toString()+"1"+throwable.getMessage()+"2"+throwable.getLocalizedMessage()+"3"+throwable.getCause());
                            if (throwable != null && CommonUtil.catchNetException(throwable.toString())) {
//                                Log.d("waha","进入一");
//                                getHandler().obtainMessage(OKGO_ON_ERROR).sendToTarget();
                            } else {
//                                Log.d("waha","进入2");
                            }
                        }
                    }
                });
    }
}