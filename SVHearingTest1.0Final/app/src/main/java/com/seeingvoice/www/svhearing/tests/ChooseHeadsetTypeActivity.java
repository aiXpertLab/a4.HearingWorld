package com.seeingvoice.www.svhearing.tests;

import android.os.Bundle;
import android.widget.RadioGroup;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;

/**
 * Date:2019/5/13
 * Time:9:49
 * auther:zyy
 */
public class ChooseHeadsetTypeActivity extends TopBarBaseActivity {

    private RadioGroup mRadioGroup;

    @Override
    protected int getConentView() {
        return R.layout.activity_choose_headset_type;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("选择耳机");
        setTitleBack(true);
        //设置右侧菜单选项即事件处理
//        setToolBarMenuOne("", R.mipmap.return_icon, new OnClickRightListener() {
//            @Override
//            public void onClick() {
//                Toast.makeText(mContext, "第一个按钮触发效果！厉害了！", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        setToolBarMenuTwo("", R.mipmap.return_icon, new OnClickRightListener() {
//            @Override
//            public void onClick() {
//                Toast.makeText(mContext, "嘿 ！不错哦~", Toast.LENGTH_SHORT).show();
//            }
//        });

        mRadioGroup = findViewById(R.id.radioGroup_choose);
        mRadioGroup.check(R.id.radioButton_in_ear);
        mRadioGroup.clearCheck();
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_in_ear:
                        skipAnotherActivity(ChooseHeadsetTypeActivity.this,PrepareHeadsetActivity.class);
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

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        mRadioGroup.clearCheck();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
