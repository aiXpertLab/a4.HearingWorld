package seeingvoice.jskj.com.seeingvoice.l_audiometry.verbaltests;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.base.OnMenuClickListener;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.history.HistroryL;
import seeingvoice.jskj.com.seeingvoice.util.AlertDialogUtil;

public class VerbalTestResultL extends MyTopBar {

    private static final String TAG = VerbalTestResultL.class.getName();
    private Intent intent;
    private TextView tv_verbel_test_result;
    private String messageCode;
    private String errorInfo;
    private String errorCode;
    private Long timeStamp;
    private int result;
    @Override
    protected int getContentView_sv() {
        return R.layout.activity_verbal_test_result;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("言语测试结果");
        setToolbarBack(true);

        setToolBarMenuOne("", R.mipmap.return_icon, null);
        setToolBarMenuTwo("", R.mipmap.ic_history_icon, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                Intent intent = new Intent(VerbalTestResultL.this, HistroryL.class);
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
        new AlertDialogUtil(VerbalTestResultL.this, "温馨提示：",
                "保存本次结果？", "确定", "取消", 0x964, mDialogListener).show();

    }

    private AlertDialogUtil.OnDialogButtonClickListener mDialogListener =  new AlertDialogUtil.OnDialogButtonClickListener() {
        @Override
        public void onDialogButtonClick(int requestCode, boolean isPositive) {
            if (requestCode == 0x964) {
                if (isPositive) {
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
