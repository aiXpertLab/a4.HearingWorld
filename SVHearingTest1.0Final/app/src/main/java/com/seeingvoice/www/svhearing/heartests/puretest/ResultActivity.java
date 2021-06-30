package com.seeingvoice.www.svhearing.heartests.puretest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Request;
import com.seeingvoice.www.svhearing.MyApplication;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.OnMenuClickListener;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.base.util.ActivityStackManager;
import com.seeingvoice.www.svhearing.beans.PureTestResultBean;
import com.seeingvoice.www.svhearing.beans.SubmitPureResultBean;
import com.seeingvoice.www.svhearing.history.HistroryActivity;
import com.seeingvoice.www.svhearing.okhttpUtil.OkHttpManager;
import com.seeingvoice.www.svhearing.ui.LineChartView;
import com.seeingvoice.www.svhearing.ui.SelfDialog;
import com.seeingvoice.www.svhearing.util.AlertDialogUtil;
import com.seeingvoice.www.svhearing.util.ListUtil;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import static com.seeingvoice.www.svhearing.AppConstant.NET_STATE_SUCCESS;
import static com.seeingvoice.www.svhearing.AppConstant.NEW_PURE_TEST_RESULT_SUBBMIT;
import static com.seeingvoice.www.svhearing.AppConstant.Xlabel;
import static com.seeingvoice.www.svhearing.AppConstant.Ylabel;

/**
 * Date:2019/3/13
 * Time:14:32
 * auther:zyy
 */
public class ResultActivity extends TopBarBaseActivity {

    private static final String TAG = ResultActivity.class.getName();
    private TextView mtv_l_result,mtv_r_result,add_remark,tv_remark;
    //左右耳检测数据
    private Integer leftEarDatas[];
    private Integer rightEarDatas[];
    //时间戳
    long timeStamp = System.currentTimeMillis();
    //左右耳的数据集
    private List<String> leftDataList,rightDataList;
    private String avgLValue,avgRValue;

    /** 历史记录*/
    private String ResultExplain[] = {"言语听力良好.","言语听力有轻度缺失.","言语听力中度缺失.","言语听力重度缺失.","言语听力几乎完全丧失.","言语听力已完全丧失."};
    private String HFResultExplain[] = {"高频听力良好.","高频听力有轻度缺失,注意保护听力.","高频听力中度缺失,请注意保护听力.","高频听力重度缺失,您的听力丧失过快.","高频听力几乎完全丧失,请注意用耳.","高频听力已完全丧失."};//高频结果
    private String TotalResultExplain[] = {"您整体听力良好,请继续保持.","请您注意保护听力,减少耳机佩戴.","请您去医院确认您的听力状况.","请您尽快去医院进行听力检测,听取医生意见.","您的听力损失非常严重,请您尽快去医院进行听力检测.","很抱歉您可能已无法听到声音,您可以关注我们的产品."};
    private PureTestResultBean mPureTestResultBean;// = new PureTestResultBean(MyApplication.userId,"", "","","",null,null)
    private LineChartView mLineChartView;
    private RadioButton mBtnLeft,mBtnRight,mBtnLeftRight;
    private ProgressDialog dialog;
    private SelfDialog selfDialog;
    private boolean isSaved = false,isLeftHasData = true,isRightHasData = true;//判断是否已经保存了结果
    private int numIndex;

    @Override
    protected int getConentView() {
        return R.layout.activity_result;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        showTitle();
        //左右耳控件
        mtv_l_result = findViewById(R.id.tv_left_result);
        mtv_r_result = findViewById(R.id.tv_right_result);
        /** 添加备注*/
        add_remark = findViewById(R.id.add_remark);
        tv_remark = findViewById(R.id.tv_result_remark);
        mBtnLeft = findViewById(R.id.btn_left);
        mBtnRight = findViewById(R.id.btn_right);
        mBtnLeftRight = findViewById(R.id.btn_left_right);
        tv_remark.setText("");

        mLineChartView = findViewById(R.id.lineChart_pure_result);
        /** 接收到左右耳的数据了 int[] 数组类型的*/

        Bundle bundle = getIntent().getExtras();
        numIndex = bundle.getIntArray("left").length;
        numIndex = numIndex + 3;
        leftEarDatas = new Integer[numIndex];
        rightEarDatas = new Integer[numIndex];
        Integer[] bundleArrLeft = toIntegerArray(Objects.requireNonNull(bundle.getIntArray("left")));
        Integer[] bundleArrRight = toIntegerArray(Objects.requireNonNull(bundle.getIntArray("right")));
        if (bundleArrLeft[0] == 121){//左耳没数据，则数组第一位值是121
            isLeftHasData = false;
            for (int i = 0; i < numIndex; i++) {
                leftEarDatas[i] = 121;
            }
//            leftEarDatas = bundleArrLeft;
//            for (int i = 0; i < bundleArrLeft.length; i++) {
//                leftEarDatas[i] = bundleArrLeft[i];
//            }
        }else {
            isLeftHasData = true;
            leftEarDatas[0] = bundleArrLeft[6];//125
            leftEarDatas[1] = bundleArrLeft[5];//250
            leftEarDatas[2] = bundleArrLeft[4];//500
            leftEarDatas[3] = bundleArrLeft[0];//1k
            leftEarDatas[4] = (bundleArrLeft[0]+bundleArrLeft[1])/2;//1.5k
            leftEarDatas[5] = bundleArrLeft[1];//2k
            leftEarDatas[6] = (bundleArrLeft[1]+bundleArrLeft[2])/2;//3k
            leftEarDatas[7] = bundleArrLeft[2];//4k
            leftEarDatas[8] = (bundleArrLeft[2]+bundleArrLeft[3])/2;//6k
            leftEarDatas[9] = bundleArrLeft[3];//8k
        }

        if (bundleArrRight[0] == 121){//右耳没数据，则数组第一位值是121
            isRightHasData = false;
            for (int i = 0; i < numIndex; i++) {
                rightEarDatas[i] = 121;
            }
//            rightEarDatas = bundleArrRight;
//            for (int i = 0; i < bundleArrRight.length; i++) {
//                rightEarDatas[i] = bundleArrRight[i];
//            }
        }else {
            isRightHasData = true;
            rightEarDatas[0] = bundleArrRight[6];//125
            rightEarDatas[1] = bundleArrRight[5];//250
            rightEarDatas[2] = bundleArrRight[4];//500
            rightEarDatas[3] = bundleArrRight[0];//1k
            rightEarDatas[4] = (bundleArrRight[0]+bundleArrRight[1])/2;//1.5k
            rightEarDatas[5] = bundleArrRight[1];//2k
            rightEarDatas[6] = (bundleArrRight[1]+bundleArrRight[2])/2;//3k
            rightEarDatas[7] = bundleArrRight[2];//4k
            rightEarDatas[8] = (bundleArrRight[2]+bundleArrRight[3])/2;//6k
            rightEarDatas[9] = bundleArrRight[3];//8k
        }

        //        {1000,2000,4000,8000,500,250,125
        // 0     1    2    3    4   5   6

        //字符串数组转换成整型数组
        leftDataList = new ArrayList<>();
        rightDataList = new ArrayList<>();
        leftDataList = ListUtil.ArrayToList(StrToIntArray(leftEarDatas));
        rightDataList = ListUtil.ArrayToList(StrToIntArray(rightEarDatas));
//        avgRValue = ListUtil.ListAverage(rightDataList);

        for (int i = 0; i < leftDataList.size(); i++) {
            Log.e(TAG, "init: leftDataList.size"+leftDataList.get(i)+"平均");
        }


        //上传数据到server
        setListener();

        showEvaluateResult(numIndex);//显示左右耳评估结果

        new showChartTask(ResultActivity.this).execute();
    }

    public static Integer[] toIntegerArray(int[] arr){
        int n=arr.length;
        Integer[] iarr=new Integer[n];
        for(int i=0;i<n;i++){
            iarr[i]=new Integer(arr[i]);
        }
        return iarr;
    }

    private void showEvaluateResult(int num) {
        float averageLeftValue,averageRightValue;
        averageLeftValue = (leftEarDatas[3]+leftEarDatas[2]+leftEarDatas[5]+leftEarDatas[8])/4;
        averageRightValue = (rightEarDatas[3]+rightEarDatas[2]+rightEarDatas[5]+rightEarDatas[8])/4;

        float sumL = 0,sumR = 0;
        for (int i = 0; i < num; i++) {
            sumL += leftEarDatas[i];
            sumR += rightEarDatas[i];
        }
        String LeftExplain = ShowLeftRightResult(averageLeftValue,leftEarDatas[8]+leftEarDatas[9],sumL/num);
        String RightExplain = ShowLeftRightResult(averageRightValue,rightEarDatas[8]+rightEarDatas[9],sumR/num);

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

    /**
     * 整型数组转换成字符串数组
     * @param intArr
     * @return
     */
    private String[] StrToIntArray(Integer[] intArr) {
        String [] str= new String[intArr.length];
        for(int i=0;i<intArr.length;i++){
            str[i] = String.valueOf(intArr[i]);
        }
        return str;
    }

    /**
     * 保存结果
     */
    private void saveResult() {
        ToastUtil.showShortToast("结果保存中...");
        Log.e(TAG, "onOptionsItemSelected: 未保存，去保存");
        String remarkText = tv_remark.getText().toString().trim();
        mPureTestResultBean = new PureTestResultBean(MyApplication.userId,"",
        String.valueOf(timeStamp), ListUtil.ListAverage(leftDataList),ListUtil.ListAverage(rightDataList),remarkText,leftDataList,rightDataList);
        postJsonToServer(mPureTestResultBean);
        if(!(ResultActivity.this).isFinishing()){
            dialog = ProgressDialog.show(ResultActivity.this,"状态：","结果正在保存中...",true,true);
        }
    }

    //显示图表的异步任务
    class showChartTask extends AsyncTask<Void,Boolean,Boolean>{

        private static final String TAG = "showChartTask";
        private Context mContext;
        showChartTask(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {//执行前要做的事情
            ToastUtil.showShortToastCenter("视图加载中");
            mLineChartView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {//后台做的事情，返回的是执行结果
            try {
                Thread.sleep(2000);
                int leftNum = getArrayNum();
                if (leftNum == numIndex){
                    publishProgress(true);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            if (values[0]){
                mLineChartView.setVisibility(View.VISIBLE);
//                mLineChartView.requestLayout();
                Log.e(TAG, "onProgressUpdate: onProgressUpdate  initLineChartView");
                mLineChartView.initLineChartView(Xlabel,Ylabel,leftEarDatas,rightEarDatas,false);//兼容新版
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                ToastUtil.showLongToast("加载完毕");
            }
        }

        private int getArrayNum() {
            if (leftEarDatas.length == rightEarDatas.length && rightEarDatas.length == numIndex){
                return leftEarDatas.length;
            }
            return 0;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 显示标题栏
     */
    private void showTitle() {
        setTitle("测试结果");
        setTitleBack(true);

        setToolBarMenuOne("", R.mipmap.ic_home, null);

        setToolBarMenuTwo("", R.drawable.save_pure_result_selector, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                if (isSaved){
                    ToastUtil.showShortToastCenter("此次结果已经保存，无需再保存！");
                }else {
                    saveResult();
                }
            }
        });

        /** 取出数据库中左右耳数据显示在图表中*/
//        mlineChart = findViewById(R.id.lineChart);
//        mlineChartManager = new LineChartManager(mlineChart);

//        //左耳图表，右耳图表，左右耳图表绑定
//        mtv_left_chart = findViewById(R.id.tv_left_chart);
//        mtv_right_chart = findViewById(R.id.tv_right_chart);
//        mtv_left_right_chart = findViewById(R.id.tv_left_right_chart);
//
//        /** 初始化结果折线图*/
//        mtv_left_right_chart.setSelected(true);
//        mtv_right_chart.setSelected(false);
//        mtv_left_chart.setSelected(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                Toast.makeText(this, "嘿 ！不知道点击返回就退出应用吗？", Toast.LENGTH_SHORT).show();
//                finish();
                if (isSaved){//如果结果已保存则退出当前页面，否则提示是否放弃
                    finish();
                }else {
                    giveUpSaveResultDialog();
                }
                break;
            case R.id.menu_item_one:
//                ShareUtil.getInstance().shareFunction(ResultActivity.this);
                break;
            case R.id.menu_item_two:
//                toNextActivity(null,ResultActivity.this, HistroryActivity.class);
                if (isSaved){
                    Log.e(TAG, "onOptionsItemSelected: 已保存");
                    ToastUtil.showShortToastCenter("此次结果已经保存，无需再保存！");
                }else {
                    saveResult();
                }
                break;
        }
        return true;//拦截系统处理事件
    }




    /**
     * 把结果Json串上传到服务器
     * @param
     */
    private void postJsonToServer(final PureTestResultBean pureTestResultBean) {
        String pureTestResultJsonStr = "";
        try {
            Gson gson = new Gson();
            pureTestResultJsonStr = gson.toJson(pureTestResultBean);
            Log.e(TAG, "postJsonToServer: json 串"+pureTestResultJsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstence().postJsonNet(NEW_PURE_TEST_RESULT_SUBBMIT, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                Log.e(TAG,"结果上传失败"+request.toString());
                ToastUtil.showLongToast("网络问题！");
                showDialog(false,"失败,请检查网络",0x964);
            }
            @Override
            public void onSuccess(String response) {
                SubmitPureResultBean submitPureResultBean = null;
                try {
                    Gson gson = new Gson();
                    submitPureResultBean = gson.fromJson(response, SubmitPureResultBean.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                String messageCode,errorInfo,errorCode;
                if (null != submitPureResultBean){
                     messageCode = submitPureResultBean.getMessage_code();
                     errorInfo = submitPureResultBean.getError_info();
                     errorCode = submitPureResultBean.getError_code();
                    if (messageCode.equals(NET_STATE_SUCCESS)){
                        Log.e(TAG,"结果上传成功"+messageCode);
                        isSaved = true;
                        showDialog(true,"保存成功,查看结果？",0x963);
                    }else {
                        Log.e(TAG,"服务端失败"+errorInfo+errorCode);
                        showDialog(false,"保存失败,错误："+errorInfo,0x964);
                    }
                }else {
                    showDialog(false,"网络错误，保存失败，请稍后再试！",0x964);
                }
            }
        },pureTestResultJsonStr);
    }

    private void showDialog(boolean yes,String showMessageStr, int resuestCode) {
        if (null != dialog){
            dialog.dismiss();
            dialog = null;
        }
        if (yes){
            new AlertDialogUtil(ResultActivity.this, "结果提示：",
                    showMessageStr, "确定", "取消", resuestCode, mDialogListener).show();
        }else {
            new AlertDialogUtil(ResultActivity.this, "结果提示：",
                    showMessageStr, "重试", "取消", resuestCode, mDialogListener).show();
        }
    }
    private AlertDialogUtil.OnDialogButtonClickListener mDialogListener =  new AlertDialogUtil.OnDialogButtonClickListener() {
        @Override
        public void onDialogButtonClick(int requestCode, boolean isPositive) {
            switch (requestCode){
                case 0x963:
                    if (isPositive){
                        toNextActivity(null,ResultActivity.this,HistroryActivity.class);
                        ActivityStackManager.getActivityStackManager().popActivity(ResultActivity.this);
                    }else {
                        ToastUtil.showLongToast("留在此页...");
                    }
                    break;
                case 0x964:
                    if (isPositive){
                        postJsonToServer(mPureTestResultBean);
                    }
                    break;
            }
        }
    };

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

    protected void setListener() {
        //三个表设置监听事件
        add_remark.setOnClickListener(listener);
        mBtnLeft.setOnClickListener(listener);
        mBtnLeftRight.setOnClickListener(listener);
        mBtnRight.setOnClickListener(listener);
    }
    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.add_remark:
                    addRemark();
                    break;
                case R.id.btn_left:
                    mLineChartView.invalidate();
                    mLineChartView.initLineChartView(Xlabel,Ylabel,"左耳结果",leftEarDatas);
                    break;
                case R.id.btn_right:
                    mLineChartView.invalidate();
                    mLineChartView.initLineChartView(Xlabel,Ylabel,"右耳结果",rightEarDatas);
                    break;
                case R.id.btn_left_right:
                    mLineChartView.invalidate();
                    mLineChartView.initLineChartView(Xlabel,Ylabel,leftEarDatas,rightEarDatas,false);
                    break;
                default:
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
        AlertDialog.Builder builder= new AlertDialog.Builder(ResultActivity.this);
        View view= LayoutInflater.from(ResultActivity.this).inflate(R.layout.dialog_remark, null);
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
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_remark.setText(edittext.getText().toString()+ "");
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        saveResult();
        if (isSaved){
            finish();
        }else {
            giveUpSaveResultDialog();
        }
    }

    /**
     * 放弃保存本次测试结果
     */
    private void giveUpSaveResultDialog() {
        selfDialog = new SelfDialog(ResultActivity.this, R.style.dialog, "确定放弃本次测试结果？","放弃此次测试结果");
        selfDialog.show();
        selfDialog.setYesOnclickListener("保存", new SelfDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                if (isSaved){
                    ToastUtil.showShortToastCenter("此次结果已经保存，无需再保存！");
                }else {
                    saveResult();
                }
                selfDialog.dismiss();
            }
        });

        selfDialog.setNoOnclickListener("放弃", new SelfDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != selfDialog){
            selfDialog.dismiss();
            selfDialog = null;
        }
    }
}
