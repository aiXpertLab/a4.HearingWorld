package seeingvoice.jskj.com.seeingvoice.l_audiometry.verbaltests;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.verbaltests.viewpager.VP_FG_L;

/**
 * Date:2019/2/20
 * Time:14:32
 * auther:zyy
 */
public class DisclaimerL extends MyTopBar implements View.OnClickListener {

    private ImageButton btn_accept,btn_reject;
    public static DisclaimerL instance;
    private TextView mContent;

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_disclaimer;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("言语测试-免责声明");
        setToolbarBack(true);
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
                    toNextActivity(null, DisclaimerL.this, VP_FG_L.class);
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
