package seeingvoice.jskj.com.seeingvoice.l_audiometry;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.base.OnMenuClickListener;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.ui.SelfDialog;

/**
 * @author  LeoReny@hypech.com
 * @version 3.0
 * @since   2021-02-13
 */

public class L5_ResultT4 extends MyTopBar {

    private static final String TAG = L5_ResultT4.class.getName();

    TabLayout tabLayout;
    ViewPager viewPager;
    L6_ViewPagerAdapter viewPagerAdapter;

    private int[] leftDbValue, rightDbValue, hzValue;
    private final boolean isSaved = false;     //判断是否已经保存了结果
    private SelfDialog selfDialog;

    @Override
    protected int getContentView_sv() {        return R.layout.a_chart;    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle(getString(R.string.chart_title)+ (new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()))+")");
        setToolbarBack(true);
        setToolBarMenuOne("", R.mipmap.ic_home, null);
        setToolBarMenuTwo("", R.drawable.save_pure_result_selector, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                saveResult();
            }
        });

        Context mContext = getApplicationContext();

        viewPager = findViewById(R.id.l_tab_view_pager);
        tabLayout = findViewById(R.id.l_tabLayout);

        viewPagerAdapter = new L6_ViewPagerAdapter(getSupportFragmentManager(), mContext);
        viewPager.setAdapter(viewPagerAdapter);

        //viewPager.setOffscreenPageLimit(1);
        // It is used to join TabLayout with ViewPager.
        tabLayout.setupWithViewPager(viewPager);

        L6_Chart mChartView = findViewById(R.id.lineChart_pure_result);
        // 接收到左右耳的数据了 int[] 数组类型的*/

        Intent intent = getIntent();
        Bundle bundleGet = intent.getExtras();
        leftDbValue  = bundleGet.getIntArray("leftDb");
        rightDbValue = bundleGet.getIntArray("rightDb");
        hzValue      = bundleGet.getIntArray("frequency");

        int[] lSort = leftDbValue.clone();
        int[] rSort = rightDbValue.clone();
        Arrays.sort(lSort);
        Arrays.sort(rSort);

        int minL = lSort[0];
        int minR = rSort[0];
        int maxL = lSort[lSort.length - 1];
        int maxR = rSort[rSort.length - 1];
        int avgKeyL = (leftDbValue[1]  + leftDbValue[2]  + leftDbValue[4]  + leftDbValue[5]  + leftDbValue[6]  + leftDbValue[3]) / 6;
        int avgKeyR = (rightDbValue[1] + rightDbValue[2] + rightDbValue[4] + rightDbValue[5] + rightDbValue[6] + rightDbValue[3]) / 6;
        int avgHFL = (leftDbValue[8] + leftDbValue[9] + leftDbValue[7]) / 3;
        int avgHFR = (rightDbValue[8] + rightDbValue[9] + rightDbValue[7]) / 3;
        //   int[] lrHz =         {125, 250, 2 500, 3 1000,1500, 5 2000,3000, 7 4000,6000,8000} ;
        int avgAllL = ( lSort[2] + lSort[3] + lSort[5] +  lSort[7]  ) / 4;
        int avgAllR = ( rSort[2] + rSort[3] + rSort[5] +  rSort[7]  ) / 4;
        String sKeyL = sAnalysis(avgKeyL, avgHFL, avgAllL)[0];
        String sKeyR = sAnalysis(avgKeyR, avgHFR, avgAllR)[0];
        String sHFL  = sAnalysis(avgKeyL, avgHFL, avgAllL)[1];
        String sHFR  = sAnalysis(avgKeyR, avgHFR, avgAllR)[1];
        String sAllL = sAnalysis(avgKeyL, avgHFL, avgAllL)[2];
        String sAllR = sAnalysis(avgKeyR, avgHFR, avgAllR)[2];
// -----------------------
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final L6_Frag1 oFrag1   = new L6_Frag1();
        final L6_Frag2 oFrag2   = new L6_Frag2();

        Bundle b = new Bundle();
        b.putString("L221", "L221");
        b.putString("L222", "L222");
        b.putInt("pMinL", minL);
        b.putInt("pMinR", minR);
        b.putInt("pMaxL", maxL);
        b.putInt("pMaxR", maxR);
        b.putInt("pAvgKeyL", avgKeyL);
        b.putInt("pAvgKeyR", avgKeyR);
        b.putInt("pAvgHFL",  avgHFL);
        b.putInt("pAvgHFR",  avgHFR);
        b.putInt("pAvgAllL", avgAllL);
        b.putInt("pAvgAllR", avgAllR);
        b.putString("pKeyL", sKeyL);
        b.putString("pKeyR", sKeyR);
        b.putString("pHFL",  sHFL);
        b.putString("pHFR",  sHFR);
        b.putString("pAllL", sAllL);
        b.putString("pAllR", sAllR);

        oFrag1.setArguments(b);
        oFrag2.setArguments(b);
        fragmentTransaction.add(R.id.l_tab_view_pager, oFrag1);
        fragmentTransaction.add(R.id.l_tab_view_pager, oFrag2);
        fragmentTransaction.commit();
// -----------------------
        mChartView.initLineChartView(leftDbValue, rightDbValue);
    }

    /**
     * 显示左右耳听力等级，数组的索引
     */
    private String[] sAnalysis(float aKey, float aHF, float aAll) {
        int indexKey=0 , indexHF=0, indexAll=0;
        if (aKey <= 25){
            indexKey = 0;
        }else if (aKey >25 && aKey <= 40){
            indexKey = 1;
        }else if (aKey >40 && aKey <= 55){
            indexKey = 2;
        }else if (aKey > 55 && aKey <= 70){
            indexKey = 3;
        }else if (aKey > 70 && aKey <= 90){
            indexKey = 4;
        }else if (aKey > 90){
            indexKey = 5;
        }

        if (aHF <= 25){
            indexHF = 0;
        }else if (aHF >25 && aHF <= 40){
            indexHF = 1;
        }else if (aHF >40 && aHF <= 55){
            indexHF = 2;
        }else if (aHF > 55 && aHF <= 70){
            indexHF = 3;
        }else if (aHF > 70 && aHF <= 90){
            indexHF = 4;
        }else if (aHF > 90){
            indexHF = 5;
        }

        if (aAll <= 25){
            indexAll = 0;
        }else if (aAll >25 && aAll <= 40){
            indexAll = 1;
        }else if (aAll >40 && aAll <= 55){
            indexAll = 2;
        }else if (aAll > 55 && aAll <= 70){
            indexAll = 3;
        }else if (aAll > 70 && aAll <= 90){
            indexAll = 4;
        }else if (aAll > 90){
            indexAll = 5;
        }

        Resources res = getResources();
        String[] arrKey = res.getStringArray(R.array.chart_key_interpretion);
        String[] arrHF  = res.getStringArray(R.array.chart_high_frequency_interpretion);
        String[] arrAll = res.getStringArray(R.array.chart_overall_interpretion);
        return new String[]{ arrKey[indexKey] , arrHF[indexHF], arrAll[indexAll]};
    }

    private void saveResult() {
        allShare(getApplicationContext().getString(R.string.topbar_save));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId()==android.R.id.home) {
            if (isSaved) {           //如果结果已保存则退出当前页面，否则提示是否放弃
                finish();
            } else {
                giveUpSaveResultDialog();
            }
        }else if(item.getItemId()==R.id.menu_item_two){
                    saveResult();
        }
        return true;//拦截系统处理事件
    }

    @Override
    public void onBackPressed() {
//        saveResult();
        if (isSaved){
            finish();
        }else {
            giveUpSaveResultDialog();
        }
    }

    private void giveUpSaveResultDialog() {
        selfDialog = new SelfDialog(L5_ResultT4.this, R.style.dialog, getApplicationContext().getString(R.string.topbar_save_popout),getApplicationContext().getString(R.string.topbar_save_title));
        selfDialog.show();

        selfDialog.setYesOnclickListener(getApplicationContext().getString(R.string.topbar_save_no), () -> {
                saveResult();
            selfDialog.dismiss();
        });

        selfDialog.setNoOnclickListener(getApplicationContext().getString(R.string.topbar_save_yes), () -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != selfDialog){
            selfDialog.dismiss();
            selfDialog = null;
        }
    }
}