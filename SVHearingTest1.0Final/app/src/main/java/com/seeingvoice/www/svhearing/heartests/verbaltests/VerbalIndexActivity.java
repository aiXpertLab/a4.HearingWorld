package com.seeingvoice.www.svhearing.heartests.verbaltests;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.seeingvoice.www.svhearing.AboutUsActivity;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.OnMenuClickListener;
import com.seeingvoice.www.svhearing.base.OnMultiClickListener;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;

public class VerbalIndexActivity extends TopBarBaseActivity {

    private Button Btn_VerBel;
    private Button Btn_NumKey;
    public static VerbalIndexActivity instance = null;

    @Override
    protected int getConentView() {
        return R.layout.activity_verbal_index;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("言语测试");
        setTitleBack(true);
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
                toNextActivity(null,VerbalIndexActivity.this, AboutUsActivity.class);
            }
        });

        instance = this;
        Btn_VerBel = findViewById(R.id.btn_verbel_test);
        Btn_VerBel.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                toNextActivity(null,VerbalIndexActivity.this,DisclaimerActivity.class);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            VerbalIndexActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
