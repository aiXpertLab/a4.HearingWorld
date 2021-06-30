package seeingvoice.jskj.com.seeingvoice.history;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.beans.PureHistoryItemBean;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.L5_ResultT4;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.L6_Chart;
import seeingvoice.jskj.com.seeingvoice.util.AlertDialogUtil;

import java.util.List;

/**
 * 需要得到上个页面传递过来的位置信息
 * Date:2019/6/26
 * Time:8:58
 * auther:zyy
 */
public class HistoryItemDetail extends MyTopBar implements View.OnClickListener {

//    //表的信息TextView
    private TextView mtv_l_result,mtv_r_result,add_remark,tv_remark,tv_save_result;

    private static final String TAG = L5_ResultT4.class.getName();
    //左右耳检测数据
    private Integer leftEarDatas[];
    private Integer rightEarDatas[];
    private boolean isOldFlag = false;//为了兼容新旧版本的数据，作为标记，网络请求回数据后，标记
    long timeStamp = System.currentTimeMillis();

    private String ResultExplain[] = {"言语听力良好.","言语听力有轻度缺失.","言语听力中度缺失.","言语听力重度缺失.","言语听力几乎完全丧失.","言语听力已完全丧失."};
    private String HFResultExplain[] = {"高频听力良好.","高频听力有轻度缺失,注意保护听力.","高频听力中度缺失,请注意保护听力.","高频听力重度缺失,您的听力丧失过快.","高频听力几乎完全丧失,请注意用耳.","高频听力已完全丧失."};//高频结果
    private String TotalResultExplain[] = {"您整体听力良好,请继续保持.","请您注意保护听力,减少耳机佩戴.","请您去医院确认您的听力状况.","请您尽快去医院进行听力检测,听取医生意见.","您的听力损失非常严重,请您尽快去医院进行听力检测.","很抱歉您可能已无法听到声音,您可以关注我们的产品."};

    private L6_Chart mLineChartView;
    private Button mBtnLeft,mBtnRight,mBtnLeftRight;
    private ProgressDialog dialog;
    //获得历史列表的Intent
    private Intent mItent;
    private Integer reportId;
    private String remark;
    private PureHistoryItemBean pureHistoryItemBean = null;
    private List<PureHistoryItemBean.DataBean.SimpleDetailBean> datalist = null;
    private int hzNum;
    private Handler mHandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 9:
                case 10:
                    mLineChartView.invalidate();
//sv                    mLineChartView.initLineChartView(Xlabel,Ylabel,leftEarDatas,rightEarDatas,isOldFlag);//新版数据
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected int getContentView_sv() {
        return R.layout.a_chart;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mItent = getIntent();
        reportId = mItent.getIntExtra("report_id",0);
        remark = mItent.getStringExtra("remark");
        showTitle();
        iniView();
        initData();
    }

    private void showTitle() {
        //title bar start
        setToolbarTitle("结果详情");
        setToolbarBack(true);

        setToolBarMenuOne("", R.mipmap.return_icon, null);
        setToolBarMenuTwo("", R.mipmap.return_icon, null);
    }

    private void iniView() {
        mLineChartView = findViewById(R.id.lineChart_pure_result);
        //左右耳控件
        mtv_l_result = findViewById(R.id.text_report_left);
        mtv_r_result = findViewById(R.id.text_report_right);
        /** 添加备注*/
        add_remark = findClickView(R.id.add_remark);
        tv_remark = findViewById(R.id.tv_result_remark);
        mBtnLeft = findViewById(R.id.btn_left);
        mBtnRight = findViewById(R.id.btn_right);
        mBtnLeftRight = findViewById(R.id.btn_left_right);

        /** 接收到左右耳的数据了 int[] 数组类型的*/
        setListener();
    }

    private void setListener() {
        add_remark.setOnClickListener(this);
        mBtnLeft.setOnClickListener(this);
        mBtnLeftRight.setOnClickListener(this);
        mBtnRight.setOnClickListener(this);
    }

    private void initData() {
    }

    private void showEvaluateResult(int num) {
        float averageLeftValue = -20;
        float averageRightValue = -20;
        String LeftExplain;
        String RightExplain;
        boolean isRightHasData = true,isLeftHasData = true;
        float sumL = 0,sumR = 0;
        for (int i = 0; i < num; i++) {
            sumL += leftEarDatas[i];
            sumR += rightEarDatas[i];
        }
        if (num == 9){
            averageLeftValue = (leftEarDatas[2]+leftEarDatas[3]+leftEarDatas[4]+leftEarDatas[6])/4;//500,1K,2K,4K
            averageRightValue = (rightEarDatas[2]+rightEarDatas[3]+rightEarDatas[4]+rightEarDatas[6])/4;
            LeftExplain  = ShowLeftRightResult(averageLeftValue,(leftEarDatas[8]+leftEarDatas[7])/2,sumL/num);
            RightExplain = ShowLeftRightResult(averageRightValue,(rightEarDatas[8]+rightEarDatas[7])/2,sumR/num);
            mtv_l_result.setText("左耳听力："+averageLeftValue+LeftExplain);
            mtv_r_result.setText("右耳听力："+averageRightValue+RightExplain);
        }

        if (num == 10){
            averageLeftValue = (leftEarDatas[3]+leftEarDatas[2]+leftEarDatas[5]+leftEarDatas[8])/4;
            averageRightValue = (rightEarDatas[3]+rightEarDatas[2]+rightEarDatas[5]+rightEarDatas[8])/4;
            LeftExplain  = ShowLeftRightResult(averageLeftValue,(leftEarDatas[8]+leftEarDatas[9])/2,sumL/num);
            RightExplain = ShowLeftRightResult(averageRightValue,(rightEarDatas[8]+rightEarDatas[9])/2,sumR/num);
            if (leftEarDatas[0] == 121){
                isLeftHasData = false;
                isRightHasData = true;
            }
            if (rightEarDatas[0] == 121){
                isRightHasData = false;
                isLeftHasData = true;
            }
            if (isRightHasData && isLeftHasData){//双耳都有数据
                mtv_l_result.setText("左耳听力："+averageLeftValue+LeftExplain);
                mtv_r_result.setText("右耳听力："+averageRightValue+RightExplain);
            }

            if (isRightHasData && !isLeftHasData){//左耳没数据，右耳有数据
                mtv_l_result.setText("左耳听力：您放弃测试左耳，无结果！");
                mtv_r_result.setText("右耳听力："+averageRightValue+RightExplain);
            }

            if (!isRightHasData && isLeftHasData){//左耳有数据，右耳没数据
                mtv_l_result.setText("左耳听力："+averageLeftValue+LeftExplain);
                mtv_r_result.setText("右耳听力：您放弃测试右耳，无结果！");
            }
        }
    }

    /**
     * 显示左右耳听力等级，数组的索引
     * @param avg
     * @return
     */
    private String ShowLeftRightResult(float avg,float HFavg,float TotalAvg) {
        int resultIndex = 0,HFresultIndex = 0,TotalResultIndex = 0;
        if (avg>= -10 && avg <= 25){
            resultIndex = 0;
        }else if (avg >25 && avg <= 40){
            resultIndex = 1;
        }else if (avg >40 && avg <= 55){
            resultIndex = 2;
        }else if (avg > 55 && avg <= 70){
            resultIndex = 3;
        }else if (avg > 70 && avg <= 90){
            resultIndex = 4;
        }else if (avg > 90){
            resultIndex = 5;
        }

        if (HFavg>= -10 && HFavg <= 25){
            HFresultIndex = 0;
        }else if (HFavg >25 && HFavg <= 40){
            HFresultIndex = 1;
        }else if (HFavg >40 && HFavg <= 55){
            HFresultIndex = 2;
        }else if (HFavg > 55 && HFavg <= 70){
            HFresultIndex = 3;
        }else if (HFavg > 70 && HFavg <= 90){
            HFresultIndex = 4;
        }else if (HFavg > 90){
            HFresultIndex = 5;
        }

        if (TotalAvg>= -10 && TotalAvg <= 25){
            TotalResultIndex = 0;
        }else if (HFavg >25 && HFavg <= 40){
            TotalResultIndex = 1;
        }else if (HFavg >40 && HFavg <= 55){
            TotalResultIndex = 2;
        }else if (HFavg > 55 && HFavg <= 70){
            TotalResultIndex = 3;
        }else if (HFavg > 70 && HFavg <= 90){
            TotalResultIndex = 4;
        }else if (HFavg > 90){
            TotalResultIndex = 5;
        }
        String str = ResultExplain[resultIndex]+HFResultExplain[HFresultIndex]+TotalResultExplain[TotalResultIndex];
        return str;
    }
    /**
     * 计算左右耳平均值，并且返回结果字符串
     * @return
     */
//    private String calAvg(Integer[] EarDatas) {
//        Float average = 0.0f;
//        average = Float.valueOf(String.valueOf((EarDatas[1]+EarDatas[2]+EarDatas[4]+EarDatas[6])/4.0));
//        int index = ShowLeftRightResult(average);
//        String explainStr = ResultExplain[index];
//        return average+"--"+explainStr;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_remark:
                addRemark();
                break;
            case R.id.btn_left:
                mLineChartView.invalidate();
//                mLineChartView.initLineChartView(Xlabel,Ylabel,"左耳结果",leftEarDatas);
                break;
            case R.id.btn_right:
                mLineChartView.invalidate();
  //              mLineChartView.initLineChartView(Xlabel,Ylabel,"右耳结果",rightEarDatas);
                break;
            case R.id.btn_left_right:
                mLineChartView.invalidate();
//sv                mLineChartView.initLineChartView(Xlabel,Ylabel,leftEarDatas,rightEarDatas,isOldFlag);
                break;
            default:
                break;
        }
    }

    private void showDialog(boolean yes,String showMessageStr, int resuestCode) {
        if (null != dialog){
            dialog.dismiss();
            dialog = null;
        }
        if (yes){
            new AlertDialogUtil(HistoryItemDetail.this, "结果提示：", showMessageStr, false,
                    "确定", resuestCode, mDialogListener).show();
//            new AlertDialogUtil(HistoryItemDetail.this, "结果提示：",
//                    showMessageStr, "确定", "取消", resuestCode, mDialogListener).show();
        }else {
            new AlertDialogUtil(HistoryItemDetail.this, "结果提示：",
                    showMessageStr, "重试", "取消", resuestCode, mDialogListener).show();
        }
    }
    private AlertDialogUtil.OnDialogButtonClickListener mDialogListener =  new AlertDialogUtil.OnDialogButtonClickListener() {
        @Override
        public void onDialogButtonClick(int requestCode, boolean isPositive) {
            switch (requestCode){
                case 0x963:
                    if (isPositive){
//                        toNextActivity(null,HistoryItemDetail.this,HistroryActivity.class);
                        finish();
                    }
                    break;
                case 0x964:
                    if (isPositive){
                    }
                    break;
            }
        }
    };

    /**
     * 添加备注之后的操作
     * 1 弹出对话框带有editText
     * 2 字数限制功能 （edittext.addTextChangedListener(new TextWatcher() {）
     * 3 显示在结果页面的 tv_remark 上
     */
    private void addRemark() {
        final int num = 20;
        AlertDialog.Builder builder= new AlertDialog.Builder(HistoryItemDetail.this);
        View view= LayoutInflater.from(HistoryItemDetail.this).inflate(R.layout.dialog_remark, null);
        final TextView text_num_attention = view.findViewById(R.id.text_num_attention);
        TextView cancel =view.findViewById(R.id.choosepage_cancel);
        TextView sure =view.findViewById(R.id.choosepage_sure);
        final EditText edittext =view.findViewById(R.id.choosepage_edittext);
        //字数限制提示
        edittext.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = num - s.length();
                text_num_attention.setText("剩余" + "" + number + "个字");
                selectionStart = edittext.getSelectionStart();
                selectionEnd = edittext.getSelectionEnd();
                if (temp.length() > num) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    edittext.setText(s);
                    edittext.setSelection(tempSelection);// 设置光标在最后
                }
            }
        });

        final Dialog dialog= builder.create();
        dialog.show();
        dialog.getWindow().setContentView(view);
        //使editext可以唤起软键盘
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String remarkStr = edittext.getText().toString()+ "";
                tv_remark.setText(remarkStr);
                postRemark(remarkStr);
                dialog.dismiss();
            }
        });
    }

    private void postRemark(String text) {
    }

    private int ShowLeftRightResult(float avg) {
        int resultIndex = 0;
        if (avg>= -10 && avg <= 25){
            resultIndex = 0;
        }else if (avg >25 && avg <= 40){
            resultIndex = 1;
        }else if (avg >40 && avg <= 55){
            resultIndex = 2;
        }else if (avg > 55 && avg <= 70){
            resultIndex = 3;
        }else if (avg > 70 && avg <= 90){
            resultIndex = 4;
        }else if (avg > 90){
            resultIndex = 5;
        }
        return resultIndex;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}