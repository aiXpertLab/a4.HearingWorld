package com.seeingvoice.www.svhearing.tests;

import android.os.Bundle;
import android.widget.RadioGroup;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.bluetooth.StartPareEarbudsActivity;

/**
 * Date:2019/5/13
 * Time:11:32
 * auther:zyy
 */
public class PrepareHeadsetActivity extends TopBarBaseActivity {

    private RadioGroup mRadioGroup;
    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("准备耳机");
        setTitleBack(true);

        mRadioGroup = findViewById(R.id.radioGroup_choose);
        mRadioGroup.clearCheck();
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_sv_earbuds:
                        skipAnotherActivity(PrepareHeadsetActivity.this, StartPareEarbudsActivity.class);
                        break;
                    case R.id.radioButton_cover_ear:
                        break;
                    case R.id.radioButton_stick_ear:
                        break;
                    case R.id.radioButton_unkown_type:
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    protected int getConentView() {
        return R.layout.activity_prepare_headset;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
