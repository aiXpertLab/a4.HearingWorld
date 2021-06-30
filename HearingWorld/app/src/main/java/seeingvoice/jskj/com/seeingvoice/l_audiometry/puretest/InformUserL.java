package seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.base.OnMultiClickListener;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;

public class InformUserL extends MyTopBar {

    private Button mBtnWhichEarTest;

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_inform_user;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("注意");
        setToolbarBack(true);

        setToolBarMenuOne("", R.mipmap.share_icon, null);

        setToolBarMenuTwo("", R.mipmap.jiaocheng, null);

        mBtnWhichEarTest = findViewById(R.id.btn_continue);
        mBtnWhichEarTest.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                toNextActivity(null, InformUserL.this,whichTestFirst.class);
            }
        });
    }
}
