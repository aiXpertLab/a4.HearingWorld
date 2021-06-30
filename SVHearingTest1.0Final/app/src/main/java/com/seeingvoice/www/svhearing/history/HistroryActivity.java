package com.seeingvoice.www.svhearing.history;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.OnMenuClickListener;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.beans.AgeHistoryBean;
import com.seeingvoice.www.svhearing.beans.PureHistoryBean;
import com.seeingvoice.www.svhearing.beans.VerbalHistoryBean;
import com.seeingvoice.www.svhearing.history.adapter.MyAdapter;
import com.seeingvoice.www.svhearing.history.fragment.HearAgeListFragment;
import com.seeingvoice.www.svhearing.history.fragment.PureHistoryFragment;
import com.seeingvoice.www.svhearing.share.ShareUtil;

public class HistroryActivity extends TopBarBaseActivity {

    private static final String TAG = HistroryActivity.class.getName();
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private TabLayout mTabLayout;
    private MyAdapter myAdapter;
    private Handler mHandler = new Handler();
    private static List<PureHistoryBean.DataBean.AllListPureBean> pureDataList = null;//纯音历史从服务端得到网络结果
    private PureHistoryBean pureHistoryBean = null;
    private List<VerbalHistoryBean.DataBean.LanguageListBean> verbalDataList = null;//言语历史从服务端得到网络结果
    private VerbalHistoryBean verbalHistoryBean = null;
    private List<AgeHistoryBean.DataBean.AgeListBean> ageDataList = null;//听力年龄从服务端得到网络结果
    private AgeHistoryBean ageHistoryBean = null;


    @Override
    protected int getConentView() {
        return R.layout.activity_histrory;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("历史记录");
        setTitleBack(true);

        setToolBarMenuTwo("", R.mipmap.return_icon, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                ShareUtil.getInstance().shareFunction(HistroryActivity.this);
            }
        });

        setToolBarMenuTwo("", R.mipmap.return_icon, null);


        viewPager = findViewById(R.id.in_viewpager);
        mTabLayout = findViewById(R.id.tl_tab);

        fragmentList = new ArrayList<>();
        fragmentList.add(new PureHistoryFragment());
        fragmentList.add(new HearAgeListFragment());
//        fragmentList.add(new HearAgeHistoryFragment());
//        fragmentList.add(new VerbalTestHistoryFragment());
        myAdapter = new MyAdapter(fragmentList,getSupportFragmentManager());
        viewPager.setAdapter(myAdapter);

        viewPager.setPageTransformer(true,new DepthPageTransformer());
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
//                Log.e("viewpage","onPageScrolled:"+i);
            }

            @Override
            public void onPageSelected(int i) {
                Log.e("viewpage","onPageSelected"+i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
//                sendMsg(i);
//                Log.e("viewpage","onPageScrollStateChanged"+i);
            }
        });
        mTabLayout.setupWithViewPager(viewPager);
//        mTabLayout.addTab(mTabLayout.newTab().setText("选项卡一").setIcon(R.mipmap.ic_launcher));
//        mTabLayout.addTab(mTabLayout.newTab().setText("选项卡二").setIcon(R.mipmap.ic_launcher));
//        mTabLayout.addTab(mTabLayout.newTab().setText("选项卡三").setIcon(R.mipmap.ic_launcher));
//        mTabLayout.addTab(mTabLayout.newTab().setText("选项卡四").setIcon(R.mipmap.ic_launcher));
    }

    private void sendMsg(int i) {
        Message msg = new Message();
        msg.what = i;
        mHandler.sendMessage(msg);
    }

    public void setHandler(Handler handler){
        mHandler = handler;
    }
}
