package seeingvoice.jskj.com.seeingvoice.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;

/**
 * Date:2019/5/15
 * Time:14:17
 * auther:zyy
 */
public class StartPareEarbudsL extends MyTopBar implements View.OnClickListener  {

    private Button btnStartPair;
    private Intent mIntent;


    @Override
    protected int getContentView_sv() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("见声蓝牙耳机配对");
        setToolbarBack(true);

        btnStartPair = findViewById(R.id.btn_start_pair);
        btnStartPair.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_pair:
                skipAnotherActivity(StartPareEarbudsL.this,BeforePairedBluetooth.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
