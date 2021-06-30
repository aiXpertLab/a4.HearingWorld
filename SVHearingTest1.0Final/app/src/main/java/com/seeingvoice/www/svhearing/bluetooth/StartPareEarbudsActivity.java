package com.seeingvoice.www.svhearing.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;

/**
 * Date:2019/5/15
 * Time:14:17
 * auther:zyy
 */
public class StartPareEarbudsActivity extends TopBarBaseActivity implements View.OnClickListener  {

    private Button btnStartPair;
    private Intent mIntent;


    @Override
    protected int getConentView() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("见声蓝牙耳机配对");
        setTitleBack(true);

        btnStartPair = findViewById(R.id.btn_start_pair);
        btnStartPair.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_pair:
                skipAnotherActivity(StartPareEarbudsActivity.this,BeforePairedBluetooth.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
