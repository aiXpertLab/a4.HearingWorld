package seeingvoice.jskj.com.seeingvoice.tests;

import android.os.Bundle;
import android.widget.RadioGroup;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;

/**
 * Date:2019/5/13
 * Time:9:49
 * auther:zyy
 */
public class ChooseHeadsetTypeL extends MyTopBar {

    private RadioGroup mRadioGroup;

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_choose_headset_type;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("选择耳机");
        setToolbarBack(true);
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
                        skipAnotherActivity(ChooseHeadsetTypeL.this, PrepareHeadsetL.class);
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
