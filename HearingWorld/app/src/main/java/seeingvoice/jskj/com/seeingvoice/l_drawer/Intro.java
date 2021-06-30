package seeingvoice.jskj.com.seeingvoice.l_drawer;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.MyBaseActivity;
import seeingvoice.jskj.com.seeingvoice.R;

/**
 * @author  LeoReny@hypech.com
 * @version 1.0
 * @since   2021-05-07
 */
public class Intro extends MyBaseActivity {

    ImageView         iv;
    Animation ani1, ani2, ani3, ani4, ani5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_intro);

        TextView mTitleTv= findViewById(R.id.text_view_title);
        mTitleTv.setText(R.string.intro);
        mTitleTv.getPaint().setFakeBoldText(true);
        ((ImageView)findViewById(R.id.iv_avatar)).setImageResource(R.mipmap.customerservicefemale);

        TextView mBelowName= findViewById(R.id.text_view_below_company);
        TextView mBelowQR  = findViewById(R.id.text_view_below_qr_code);

        ani1 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_from_left);
        ani2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_from_right);
        ani3 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_out_to_left);
        ani4 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_out_to_right);
        ani5 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.push_bottom_in);

        iv = findViewById(R.id.image_view_intro);

        String src = getIntent().getStringExtra("from");

        if (src.equals("intro")){
            mBelowName.setText(getString(R.string.app_name));
            mBelowQR.setText(getString(R.string.click_to_continue));
        }else if (src.equals("help")){
            mBelowName.setText(getString(R.string.app_name));
            mBelowQR.setText(getString(R.string.scan_qr_code_and_add_friends));
        }else if (src.equals("Frag3MeProfile")){
            mBelowName.setText(getString(R.string.app_name));
            mBelowQR.setText(getString(R.string.scan_qr_code_and_add_friends));
        }else if (src.equals("complain")){
            mBelowName.setText(getString(R.string.app_name));
            mBelowQR.setText(getString(R.string.scan_qr_code_and_add_friends));
        }
    }

    public void introClick(View view){
        int ki = (int) (Math.random() * 6);
        if (ki == 0) {
            iv.setImageResource(R.mipmap.hw1);
            iv.startAnimation(ani1);
        }else if(ki == 2) {
            iv.setImageResource(R.mipmap.hw2);
            iv.startAnimation(ani2);
        }else if(ki == 3) {
            iv.setImageResource(R.mipmap.hw3);
            iv.startAnimation(ani3);
        }else if(ki == 4) {
            iv.setImageResource(R.mipmap.hw4);
            iv.startAnimation(ani4);
        }else if(ki == 5) {
            iv.setImageResource(R.mipmap.hw5);
            iv.startAnimation(ani5);
        }else if(ki == 1) {
            iv.setImageResource(R.mipmap.hw6);
            iv.startAnimation(ani1);
        }
    }

    public void back(View view) {        finish();    }
}