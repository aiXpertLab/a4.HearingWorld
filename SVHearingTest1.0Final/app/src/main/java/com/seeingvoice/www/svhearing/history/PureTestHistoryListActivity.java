package com.seeingvoice.www.svhearing.history;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.seeingvoice.www.svhearing.MyApplication;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.beans.PureHistoryBean;
import com.seeingvoice.www.svhearing.beans.PureHistoryItemBean;
import com.seeingvoice.www.svhearing.okhttpUtil.OkHttpManager;
import com.seeingvoice.www.svhearing.util.DateUtil;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

import static com.seeingvoice.www.svhearing.AppConstant.NET_STATE_SUCCESS;
import static com.seeingvoice.www.svhearing.AppConstant.PURE_HISTORY_ITEM_URL;
import static com.seeingvoice.www.svhearing.AppConstant.PURE_TEST_HISTORY_URL;
import static com.seeingvoice.www.svhearing.AppConstant.REQUEST_AUTO_SETTING;

/**
 * 供助听器自动设置的
 * Date:2019/6/13
 * Time:15:06
 * auther:zyy
 */
public class PureTestHistoryListActivity extends TopBarBaseActivity {

    private static final String TAG = PureTestHistoryListActivity.class.getName();
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;
    private PureHistoryBean pureHistoryBean;
    private static List<PureHistoryBean.DataBean.AllListPureBean> dataList = null;//从服务端得到网络结果
    private Button mBtnChoose;
//    List<PureTestResult> listPureResult = new ArrayList();
    private int listSize,selectedID;
    private String[] LeftEarStr,RightEarStr;
    private PureHistoryItemBean pureHistoryItemBean = null;
    private List<PureHistoryItemBean.DataBean.SimpleDetailBean> detailBeanList = null;
    private List<String[]> resultArrayListTemp = new ArrayList<>();
    private static List<String[]> resultArrayList = new ArrayList<>();
    private String leftEarDatas[],rightEarDatas[];
    private TextView mTvHint;
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                addDataRadioGroup();
            }
            if (msg.what == 2){
                stopDialog();
                if (null != resultArrayListTemp && resultArrayListTemp.size() == 2){//得到纯音测试结果
                    LeftEarStr = resultArrayListTemp.get(0);
                    RightEarStr = resultArrayListTemp.get(1);
                    Intent intent = new Intent();//数据是使用Intent返回
                    intent.putExtra("leftear",LeftEarStr);
                    intent.putExtra("rightear",RightEarStr);
                    Log.e("测试测试测试", "setEQsetting: RightEarStr size:"+RightEarStr.length);
                    PureTestHistoryListActivity.this.setResult(REQUEST_AUTO_SETTING,intent);
                    PureTestHistoryListActivity.this.finish();
                }else {
                    ToastUtil.showLongToast("网络原因请重试");
                }
            }
        }
    };
    private ProgressDialog dialog;

    private void addDataRadioGroup() {
        if (listSize > 0 && null != dataList && !dataList.isEmpty()){
            for (int i = 0; i < listSize; i++) {
                mRadioButton = new RadioButton(this);
                mRadioButton.setTextColor(Color.BLACK);
                mRadioButton.setId(i);
                mRadioButton.setText(DateUtil.getDateToString(Long.valueOf(dataList.get(i).getCreat_time()+"000"),"yyyy-MM-dd HH:mm:ss")+"-备注："+dataList.get(i).getRemark());
                mRadioGroup.addView(mRadioButton,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            mTvHint.setVisibility(View.VISIBLE);
            mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    mBtnChoose.setVisibility(View.VISIBLE);
                    selectedID = checkedId;
                    mBtnChoose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            LeftEarStr = ListToArray(listPureResult.get(selectedID).getMLeftResult());
//                            RightEarStr = ListToArray(listPureResult.get(selectedID).getMRightResult());
                            try {
                                Integer reportId = dataList.get(selectedID).getReport_id();
                                getResultArray(reportId);
                                showDialog("正在设置助听器参数，请稍等");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }else {
            findViewById(R.id.tv_hint).setVisibility(View.GONE);
            findViewById(R.id.tv_attention_no_result).setVisibility(View.VISIBLE);
        }
    }

    /**
     * 请求纯音历史记录
     */
    private void requestPureNetData() {
        //网络请求历史记录
        OkHttpManager.getInstence().postNet(PURE_TEST_HISTORY_URL, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                ToastUtil.showShortToastCenter("网络错误，请稍后再试！");
                Log.e(TAG,"助听器请求纯音列表失败:"+request.toString());
            }

            @Override
            public void onSuccess(String response) {
                pureHistoryBean = null;
                try {
                    Gson gson = new Gson();
                    pureHistoryBean = gson.fromJson(response,PureHistoryBean.class);
                    if (null != pureHistoryBean){
                        Log.e(TAG,"pureHistoryBean 得到数据:"+pureHistoryBean.getMessage_code());
                        if (pureHistoryBean.getMessage_code().equals(NET_STATE_SUCCESS)){
                            try {
                                dataList = pureHistoryBean.getData().getAll_list();
                                Log.e(TAG,"获得dataList长度为： "+dataList.size());
                                if (null != dataList && !dataList.isEmpty()){
                                    //通过report_id 得到该用户所有的纯音测试结果
                                    listSize = dataList.size();
                                    Message msg = new Message();
                                    msg.what = 1;
                                    mHandler.sendMessage(msg);
                                }else {
                                    Log.e(TAG,pureHistoryBean.getError_info()+pureHistoryBean.getError_code());
                                    findViewById(R.id.tv_hint).setVisibility(View.GONE);
                                    findViewById(R.id.tv_attention_no_result).setVisibility(View.VISIBLE);
//                                            isNoReult(dataList);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }else {
                        ToastUtil.showLongToast("网络错误,请重新访问");
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }

            }
        },new OkHttpManager.Param("user_id",String.valueOf(MyApplication.userId)));
    }

    @Override
    protected int getConentView() {
        return R.layout.activity_history_pure_test;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initTile();//titlebar部分
        /* 查找数据库*/
        //查询到所有存储到数据库中的数据对象的集合
        //listPureResult = MyApplication.getAppInstance().getmDaoSession().queryBuilder(PureTestResult.class).list();
        //listSize = listPureResult.size();
        mBtnChoose = findViewById(R.id.btn_choose);
        mRadioGroup = findViewById(R.id.RG_choose_auto_settings);
        mTvHint = findViewById(R.id.tv_hint);
        requestPureNetData();
    }

    /**
     * 初始化标题栏
     */
    private void initTile() {
        setTitle("请选择");
        setTitleBack(true);

        setToolBarMenuOne("", R.mipmap.return_icon, null);
        setToolBarMenuTwo("", R.mipmap.return_icon, null);
    }

    private List<String[]> getResultArray(Integer reportId) {
        OkHttpManager.getInstence().getNet(PURE_HISTORY_ITEM_URL +"?report_id=" + reportId, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                ToastUtil.showLongToast("网络错误，稍后再试！");
            }
            @Override
            public void onSuccess(String response) {
                try {
                    Gson gson = new Gson();
                    pureHistoryItemBean = gson.fromJson(response,PureHistoryItemBean.class);
                    if (null != pureHistoryItemBean){
                        detailBeanList = pureHistoryItemBean.getData().getSimple_detail();
                        if (!detailBeanList.isEmpty() && detailBeanList != null){
                            int size = detailBeanList.size();
                            leftEarDatas = new String[size];
                            rightEarDatas = new String[size];
                            for (int i = 0; i < detailBeanList.size(); i++) {
                                leftEarDatas[i] = String.valueOf(Integer.valueOf(detailBeanList.get(i).getLeft_result()));
                                rightEarDatas[i] = String.valueOf(Integer.valueOf(detailBeanList.get(i).getRight_result()));
                            }
                            try {
                                resultArrayListTemp.add(leftEarDatas);
                                resultArrayListTemp.add(rightEarDatas);
                                if (resultArrayListTemp.size() == 2){
                                    Message msg = new Message();
                                    msg.what = 2;
                                    mHandler.sendMessage(msg);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            ToastUtil.showLongToast("网络错误，稍后再试！");
                        }
                    }else {
                        ToastUtil.showLongToast("网络错误，稍后再试！");
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }

            }
        });
        return resultArrayListTemp;
    }

    private void showDialog(String showMessageStr) {
        if(PureTestHistoryListActivity.this.isFinishing()){
            //show dialog
            dialog = ProgressDialog.show(PureTestHistoryListActivity.this,"数据设置中",showMessageStr,true,true);
        }
    }

    private void stopDialog() {
        if (dialog != null){
            dialog.dismiss();
        }
    }

    private String[] ListToArray(List list){
        String[] strings = new String[list.size()];
        list.toArray(strings);
        return strings;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRadioGroup = null;
        stopDialog();
    }
}
