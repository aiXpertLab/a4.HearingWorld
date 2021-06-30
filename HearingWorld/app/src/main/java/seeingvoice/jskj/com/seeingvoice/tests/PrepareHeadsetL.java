package seeingvoice.jskj.com.seeingvoice.tests;

import android.os.Bundle;
import android.widget.RadioGroup;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.bluetooth.StartPareEarbudsL;

/**
 * Date:2019/5/13
 * Time:11:32
 * auther:zyy
 */
public class PrepareHeadsetL extends MyTopBar {

    private RadioGroup mRadioGroup;
    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("准备耳机");
        setToolbarBack(true);

        mRadioGroup = findViewById(R.id.radioGroup_choose);
        mRadioGroup.clearCheck();
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_sv_earbuds:
                        skipAnotherActivity(PrepareHeadsetL.this, StartPareEarbudsL.class);
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
    protected int getContentView_sv() {
        return R.layout.activity_prepare_headset;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
