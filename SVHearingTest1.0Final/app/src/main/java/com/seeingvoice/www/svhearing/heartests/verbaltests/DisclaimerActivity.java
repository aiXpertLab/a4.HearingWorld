package com.seeingvoice.www.svhearing.heartests.verbaltests;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.heartests.verbaltests.viewpager.VP_FG_Activity;

/**
 * Date:2019/2/20
 * Time:14:32
 * auther:zyy
 */
public class DisclaimerActivity extends TopBarBaseActivity implements View.OnClickListener {

    private ImageButton btn_accept,btn_reject;
    public static DisclaimerActivity instance;
    private TextView mContent;

    @Override
    protected int getConentView() {
        return R.layout.activity_disclaimer;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("言语测试-免责声明");
        setTitleBack(true);
        instance = this;

        btn_accept = findViewById(R.id.btn_accept_disclaimer);
        btn_reject = findViewById(R.id.btn_reject_disclaimer);
        mContent = findViewById(R.id.tv_disclaimer_content);
        mContent.setMovementMethod(ScrollingMovementMethod.getInstance());

        btn_accept.setOnClickListener(this);
        btn_reject.setOnClickListener(this);
    }

//    private OnMultiClickListener listener = new OnMultiClickListener() {
//        @Override
//        public void onMultiClick(View v) {
//
//        }
//    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
//        if (AntiShakeUtils.isInvalidClick(v)){
            switch (v.getId()){
                case R.id.btn_accept_disclaimer:
                    toNextActivity(null,DisclaimerActivity.this,VP_FG_Activity.class);
                    Log.e("1111111", "被点了");
                    finish();
                    break;
                case R.id.btn_reject_disclaimer:
                    Log.e("1111111", "取消了");
                    finish();
                    break;
            }
//        }
    }
}
