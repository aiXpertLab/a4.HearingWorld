package com.seeingvoice.www.svhearing.smsverify;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;

/**
 * Date:2019/7/10
 * Time:9:05
 * auther:zyy
 */
public class SmsVerifyActivity extends TopBarBaseActivity {
    private Button btn_bound_phone;
    private VerifyPopupWindow mVerifyPopupWindow;
    @Override
    protected int getConentView() {
        return R.layout.activity_sms_verify;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("短信验证");
        setTitleBack(true);

        setToolBarMenuOne("", R.mipmap.return_icon,null);
        setToolBarMenuTwo("", R.mipmap.return_icon,null);

        btn_bound_phone = findViewById(R.id.phone_bound);
        initData();
    }

    private void initData() {
        mVerifyPopupWindow = new VerifyPopupWindow(SmsVerifyActivity.this);
        btn_bound_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVerifyPopupWindow.showPopupWindow();
            }
        });
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (mVerifyPopupWindow != null) {
//            return false;
//        }
//        return super.dispatchTouchEvent(ev);
//    }
}
