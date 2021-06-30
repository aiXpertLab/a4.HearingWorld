package seeingvoice.jskj.com.seeingvoice.l_audiometry.verbaltests;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import seeingvoice.jskj.com.seeingvoice.l_drawer.L_AboutUs;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.base.OnMenuClickListener;
import seeingvoice.jskj.com.seeingvoice.base.OnMultiClickListener;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;

public class VerbalIndexL extends MyTopBar {

    private Button Btn_VerBel;
    private Button Btn_NumKey;
    public static VerbalIndexL instance = null;

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_verbal_index;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("言语测试");
        setToolbarBack(true);
        setToolBarMenuOne("", R.mipmap.share_icon, null);
//        new OnMenuClickListener() {
//            @Override
//            public void onMultiClick(MenuItem v) {
//                ShareUtil.getInstance().shareFunction(VerbalIndexActivity.this);
//            }
//        }

        setToolBarMenuTwo("", R.mipmap.jiaocheng, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                toNextActivity(null, VerbalIndexL.this, L_AboutUs.class);
            }
        });

        instance = this;
        Btn_VerBel = findViewById(R.id.btn_verbel_test);
        Btn_VerBel.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                toNextActivity(null, VerbalIndexL.this, DisclaimerL.class);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            VerbalIndexL.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
