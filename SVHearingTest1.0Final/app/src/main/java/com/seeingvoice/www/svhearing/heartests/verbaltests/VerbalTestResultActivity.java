package com.seeingvoice.www.svhearing.heartests.verbaltests;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.seeingvoice.www.svhearing.MyApplication;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.OnMenuClickListener;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.history.HistroryActivity;
import com.seeingvoice.www.svhearing.okhttpUtil.OkHttpManager;
import com.seeingvoice.www.svhearing.util.AlertDialogUtil;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;

import static com.seeingvoice.www.svhearing.AppConstant.NET_STATE_SUCCESS;
import static com.seeingvoice.www.svhearing.AppConstant.VERBAL_TEST_RESULT_URL;

public class VerbalTestResultActivity extends TopBarBaseActivity {

    private static final String TAG = VerbalTestResultActivity.class.getName();
    private Intent intent;
    private TextView tv_verbel_test_result;
    private String messageCode;
    private String errorInfo;
    private String errorCode;
    private Long timeStamp;
    private int result;
    @Override
    protected int getConentView() {
        return R.layout.activity_verbal_test_result;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("言语测试结果");
        setTitleBack(true);

        setToolBarMenuOne("", R.mipmap.return_icon, null);
        setToolBarMenuTwo("", R.mipmap.history_icon, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                Intent intent = new Intent(VerbalTestResultActivity.this, HistroryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tv_verbel_test_result = findViewById(R.id.tv_verbel_test_result);
        intent = getIntent();
        int correctNo = intent.getIntExtra("result",0);
        result = (int) ((correctNo/12.0)*100);
        if (result > 50){
            tv_verbel_test_result.setText("正确率："+result+"%!这说明您的听力正常，或者您可能受益于佩戴的助听器");
        }else {
            tv_verbel_test_result.setText("正确率："+result+"%！您需要去专业机构去测试了以便得到最专业的结果！");
        }

        timeStamp = System.currentTimeMillis();
        new AlertDialogUtil(VerbalTestResultActivity.this, "温馨提示：",
                "保存本次结果？", "确定", "取消", 0x964, mDialogListener).show();

    }

    private AlertDialogUtil.OnDialogButtonClickListener mDialogListener =  new AlertDialogUtil.OnDialogButtonClickListener() {
        @Override
        public void onDialogButtonClick(int requestCode, boolean isPositive) {
            if (requestCode == 0x964) {
                if (isPositive) {
                    OkHttpManager.getInstence().getNet(VERBAL_TEST_RESULT_URL + "?user_id=" + MyApplication.userId + "&language_level=" + result + "%" + "&created_at=" + timeStamp, new OkHttpManager.ResultCallback() {
                        @Override
                        public void onFailed(Request request, IOException e) {
                            ToastUtil.showShortToastCenter("网络错误，请稍后再试！");
                        }

                        @Override
                        public void onSuccess(String response) {
                            Log.e(TAG, "onSuccess: 言语测试结果Json:" + response);
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);
                                messageCode = jsonObject.getString("message_code");
                                errorInfo = jsonObject.getString("error_info");
                                errorCode = jsonObject.getString("error_code");
                                if (messageCode.equals(NET_STATE_SUCCESS)) {
                                    ToastUtil.showShortToastCenter("言语测试结果保存成功！");
                                    Log.e(TAG, "onSuccess: 言语测试结果提交成功");
                                } else {
                                    ToastUtil.showShortToastCenter("言语测试结果保存失败！"+errorInfo);
                                    Log.e(TAG, "onSuccess: 言语测试结果提交失败" + errorInfo + errorCode);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    };

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                toVerbalIndex();
//                break;
//        }
//        return true;//拦截系统处理事件
//    }
//
//    private void toVerbalIndex() {
//        finish();
//    }
//
//    @Override
//    public void onBackPressed() {
//        toVerbalIndex();
//    }
}
