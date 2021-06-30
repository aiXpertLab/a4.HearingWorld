package com.reapex.sv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.reapex.sv.adapter.L_ViewPagerAdapter;

public class Home extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_home);

        // 1.locate the ViewPager in activity_main layout
        ViewPager mViewPager = findViewById(R.id.view_pager);
        // 2.inject the adapter into the viewpager.
        L_ViewPagerAdapter mViewPagerAdapter = new L_ViewPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        mViewPager.setAdapter(mViewPagerAdapter);
        // 3. find tablayout in main layout
        TabLayout mTabLayout = findViewById(R.id.tab_layout);
        // 4.It is used to join TabLayout with ViewPager.
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setIcon(R.drawable.btn_home_selection);
        mTabLayout.getTabAt(1).setIcon(R.drawable.btn_glasses_selection);
        mTabLayout.getTabAt(2).setIcon(R.drawable.btn_my_selection);
    }

    private int getPrompt(int errorCode) {
        switch (errorCode) {
            case MLAsrConstants.ERR_NO_NETWORK:
                return R.string.error_no_network;
            case MLAsrConstants.ERR_NO_UNDERSTAND:
                return R.string.error_no_understand;
            case MLAsrConstants.ERR_SERVICE_UNAVAILABLE:
                return R.string.error_service_unavailable;
            default:
                return errorCode;
        }
    }


}



