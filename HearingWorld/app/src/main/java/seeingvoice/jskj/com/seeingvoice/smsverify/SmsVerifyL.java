package seeingvoice.jskj.com.seeingvoice.smsverify;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;

/**
 * Date:2019/7/10
 * Time:9:05
 * auther:zyy
 */
public class SmsVerifyL extends MyTopBar {
    private Button btn_bound_phone;
    private VerifyPopupWindow mVerifyPopupWindow;
    @Override
    protected int getContentView_sv() {
        return R.layout.activity_sms_verify;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("短信验证");
        setToolbarBack(true);

        setToolBarMenuOne("", R.mipmap.return_icon,null);
        setToolBarMenuTwo("", R.mipmap.return_icon,null);

        btn_bound_phone = findViewById(R.id.phone_bound);
        initData();
    }

    private void initData() {
        mVerifyPopupWindow = new VerifyPopupWindow(SmsVerifyL.this);
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
