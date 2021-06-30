package seeingvoice.jskj.com.seeingvoice.history;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.beans.PureHistoryBean;
import seeingvoice.jskj.com.seeingvoice.beans.PureHistoryItemBean;
import seeingvoice.jskj.com.seeingvoice.util.DateUtil;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import static seeingvoice.jskj.com.seeingvoice.MyData.REQUEST_AUTO_SETTING;

/**
 * 供助听器自动设置的
 * Date:2019/6/13
 * Time:15:06
 * auther:zyy
 */
public class PureTestHistoryListL extends MyTopBar {

    private static final String TAG = PureTestHistoryListL.class.getName();
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;
    private Button mBtnChoose;
//    List<PureTestResult> listPureResult = new ArrayList();
    private int listSize,selectedID;
    private String[] LeftEarStr,RightEarStr;
    private PureHistoryBean pureHistoryBean;
    private static List<PureHistoryBean.DataBean.AllListPureBean> dataList = null;//从服务端得到网络结果
    private PureHistoryItemBean pureHistoryItemBean = null;
    private List<PureHistoryItemBean.DataBean.SimpleDetailBean> detailBeanList = null;
    private List<String[]> resultArrayListTemp = new ArrayList<>();
    private static List<String[]> resultArrayList = new ArrayList<>();
    private String leftEarDatas[] = new String[9];
    private String rightEarDatas[] = new String[9];
    private TextView mTvHint;
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                addDataRadioGroup();
            }
            if (msg.what == 2){
                stopDialog();
                if (null != resultArrayListTemp && resultArrayListTemp.size() == 2){
                    LeftEarStr = resultArrayListTemp.get(0);
                    RightEarStr = resultArrayListTemp.get(1);
                    Intent intent = new Intent();//数据是使用Intent返回
                    intent.putExtra("leftear",LeftEarStr);
                    intent.putExtra("rightear",RightEarStr);
                    PureTestHistoryListL.this.setResult(REQUEST_AUTO_SETTING,intent);
                    PureTestHistoryListL.this.finish();
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
                mRadioButton.setText("纯音测试结果："+ DateUtil.getDateToString(Long.valueOf(dataList.get(i).getCreat_time()+"000"),"yyyy-MM-dd HH:mm:ss"));
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
    }

    @Override
    protected int getContentView_sv() {
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
        setToolbarTitle("请选择");
        setToolbarBack(true);

        setToolBarMenuOne("", R.mipmap.return_icon, null);
        setToolBarMenuTwo("", R.mipmap.return_icon, null);
    }

    private void showDialog(String showMessageStr) {
        if(PureTestHistoryListL.this.isFinishing()){
            //show dialog
            dialog = ProgressDialog.show(PureTestHistoryListL.this,"数据设置中",showMessageStr,true,true);
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
