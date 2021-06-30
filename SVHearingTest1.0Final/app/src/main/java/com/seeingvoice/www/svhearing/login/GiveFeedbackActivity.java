package com.seeingvoice.www.svhearing.login;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.seeingvoice.www.svhearing.MyApplication;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.beans.FeedbackBean;
import com.seeingvoice.www.svhearing.okhttpUtil.OkHttpManager;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import java.io.IOException;

import okhttp3.Request;

import static android.widget.AdapterView.OnClickListener;
import static android.widget.AdapterView.OnItemSelectedListener;
import static com.seeingvoice.www.svhearing.AppConstant.FEEDBACK_URL;
import static com.seeingvoice.www.svhearing.AppConstant.NET_STATE_SUCCESS;

/**
 * Date:2019/7/30
 * Time:14:04
 * auther:zyy
 */
public class GiveFeedbackActivity  extends TopBarBaseActivity {
    private static final String TAG = GiveFeedbackActivity.class.getName();
    private Spinner mSpinner;
    private EditText mContent,mContact;
    private Button mSubbmit;
    boolean isSpinnerFirst = true;
    private String[] feedbackTypeAyyay;
    private int SpinnerPosition = 0;
    private String content;
    private String contact;


    @Override
    protected int getConentView() {
        return R.layout.activity_give_feedback;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        setTitle("用户反馈");
        setTitleBack(true);

        setToolBarMenuOne("", R.mipmap.share_icon, null);

        setToolBarMenuTwo("", R.mipmap.return_icon, null);

        initComponents();

        //控件监听事件
        initEnvent();
    }

    private void initComponents() {
        //绑定控件
        mSpinner = findViewById(R.id.feedback_type_spinner);
        mContent = findViewById(R.id.feedback_content);
        mContact = findViewById(R.id.feedback_contact);
        mSubbmit = findViewById(R.id.feedback_submit);
        Resources res = getResources();
        feedbackTypeAyyay = res.getStringArray(R.array.feedback_type);
    }

    private void initEnvent() {
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerPosition = position;//选择了哪种反馈类型

                if (isSpinnerFirst) {
                    //第一次初始化spinner时，不显示默认被选择的第一项即可
                    //view.setVisibility(View.INVISIBLE);
                }
                isSpinnerFirst = false ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mContact.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (temp.length() > 500)
                    ToastUtil.showLongToast("已经超过五百字了！");
                content = temp.toString().trim();
            }
        });

        mContact.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                contact = temp.toString().trim();
            }
        });

        mSubbmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SpinnerPosition == 0 || mContent.getText().toString().trim().isEmpty() || mContact.getText().toString().trim().isEmpty()) {
                    ToastUtil.showLongToastCenter("您有未填项，请检查！");
                    return;
                }
                //把数据上传给服务端；
                OkHttpManager.getInstence().getNet(FEEDBACK_URL + "?user_id="+ MyApplication.userId+"&feedback_text="+mContent.getText().toString().trim() + "&feedback_tel=" + mContact.getText().toString().trim(), new OkHttpManager.ResultCallback() {
                    @Override
                    public void onFailed(Request request, IOException e) {
                        ToastUtil.showLongToast("网络错误，请联系管理员！");
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "onSuccess" + response);
                        FeedbackBean feedbackBean = null;
                        try {
                            Gson gson = new Gson();
                            feedbackBean = gson.fromJson(response, FeedbackBean.class);
                            if (feedbackBean == null) {
                                return;
                            }
                            if (NET_STATE_SUCCESS.equals(feedbackBean.getMessage_code())) {
                                ToastUtil.showLongToast("已经反馈成功，请耐心等待技术人员与您联系");
                            } else {
                                ToastUtil.showLongToast("网络故障，请联系工作人员");
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }
}
