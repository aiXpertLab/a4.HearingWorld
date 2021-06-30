package com.seeingvoice.www.svhearing.heartests.puretest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.OnMultiClickListener;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;

public class InformUserActivity extends TopBarBaseActivity {

    private Button mBtnWhichEarTest;

    @Override
    protected int getConentView() {
        return R.layout.activity_inform_user;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("注意");
        setTitleBack(true);

        setToolBarMenuOne("", R.mipmap.share_icon, null);

        setToolBarMenuTwo("", R.mipmap.jiaocheng, null);

        mBtnWhichEarTest = findViewById(R.id.btn_continue);
        mBtnWhichEarTest.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                toNextActivity(null,InformUserActivity.this,whichTestFirst.class);
            }
        });
    }
}
