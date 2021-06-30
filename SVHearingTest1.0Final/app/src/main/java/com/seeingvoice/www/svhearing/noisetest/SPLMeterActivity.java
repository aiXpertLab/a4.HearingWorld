package com.seeingvoice.www.svhearing.noisetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.heartests.puretest.BeforePureTestActivity;
import com.seeingvoice.www.svhearing.noisetest.noiseUI.AudioRecordDemo;
import com.seeingvoice.www.svhearing.noisetest.noiseUI.NoiseboardView;
import com.seeingvoice.www.svhearing.ui.AttentionView;

import java.util.ArrayList;
import java.util.List;

/**
 * Date:2019/3/6
 * Time:13:58
 * auther:zyy
 */
public class SPLMeterActivity extends TopBarBaseActivity implements View.OnClickListener {

    private static final String STOP = "开始测试";
    private static final String START = "结束测试";
//    private static final int REQUEST_CODE = 2,PERMISSION_REQUEST_CODE=3; //权限请求码
    private static final int MY_PERMISSIONS_REQUEST = 1;
    List<String> mPermissionList = new ArrayList<>();    // 声明一个集合，在后面的代码中用来存储用户拒绝授权的权
    private AudioRecordDemo audioRecordDemo;
    private Button start_hearing_meter;
    public NoiseboardView mNoiseboardView;
    public TextView mNoiseHint;
    private Intent mIntent;
    private AttentionView mAttentionView;
    

    @Override
    protected int getConentView() {
        return R.layout.activity_spl_meter;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("噪音检测");
        setTitleBack(true);

        setToolBarMenuOne("", R.mipmap.return_icon, null);

        setToolBarMenuTwo("", R.mipmap.return_icon, null);
        initData();
    }

    private void initData() {
        mIntent = getIntent();
        boolean isFromIndex= false;
        isFromIndex = mIntent.getExtras().getBoolean("fromIndex");

        audioRecordDemo = new AudioRecordDemo(this);
        mNoiseboardView = findViewById(R.id.noiseboardView);
        start_hearing_meter = findViewById(R.id.btn_start_hearing_test_meter);
        mNoiseHint = findViewById(R.id.tv_noise_hint);
        audioRecordDemo.getNoiseLevel();
        start_hearing_meter.setOnClickListener(this);
        if (isFromIndex){
            start_hearing_meter.setVisibility(View.GONE);
        }else {
            start_hearing_meter.setVisibility(View.VISIBLE);
        }

        mAttentionView = findViewById(R.id.attention_view);
        mAttentionView.setTitle(getResources().getString(R.string.notice_noise));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_hearing_test_meter:
                toNextActivity(null,this, BeforePureTestActivity.class);
                finish();
//                Intent mIntent = new Intent(this,whichTestFirst.class);
//                startActivity(mIntent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioRecordDemo.stop();
    }
}
