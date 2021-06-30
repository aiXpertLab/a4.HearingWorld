package seeingvoice.jskj.com.seeingvoice.l_drawer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;


import seeingvoice.jskj.com.seeingvoice.MyBaseActivity;
import seeingvoice.jskj.com.seeingvoice.MySP;
import seeingvoice.jskj.com.seeingvoice.R;

public class MeMore extends MyBaseActivity implements View.OnClickListener {

    TextView mTitleTv;
    RelativeLayout mSexRl, mRegionRl, mSignRl;
    TextView mSexTv, mRegionTv, mSignTv;

    MySP user = MySP.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_frag3_me_more);

        mTitleTv= findViewById(R.id.text_view_title);

        mSexRl= findViewById(R.id.rl_sex);
        mRegionRl= findViewById(R.id.rl_region);
        mSignRl= findViewById(R.id.rl_sign);
        mSexTv= findViewById(R.id.tv_sex);
        mRegionTv= findViewById(R.id.text_view_below_company);
        mSignTv= findViewById(R.id.tv_sign);

        mRegionRl.setOnClickListener(this);
        mRegionTv.setOnClickListener(this);
        mSexRl.setOnClickListener(this);
        mSexTv.setOnClickListener(this);
        mSignRl.setOnClickListener(this);
        mSignTv.setOnClickListener(this);

        MySP.getInstance().init(this);
        initView();
    }

    private void initView() {
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);
        mSexTv.setText(MySP.getInstance().getUGender());
        mRegionTv.setText(MySP.getInstance().getURegion());
        mSignTv.setText(MySP.getInstance().getUSignature());
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_sex:
                startActivity(new Intent(this, MeMoreGender.class));
                break;
            case R.id.rl_region:
                startActivity(new Intent(this, MeMoreRegion.class));
                break;
            case R.id.rl_sign:
                // 签名
                startActivity(new Intent(this, MeMoreSignature.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSexTv.setText(user.getUGender());
        mRegionTv.setText(user.getURegion());
        mSignTv.setText(user.getUSignature());
    }
}
