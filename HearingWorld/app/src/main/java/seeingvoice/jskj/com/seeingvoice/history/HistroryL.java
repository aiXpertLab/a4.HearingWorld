package seeingvoice.jskj.com.seeingvoice.history;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.base.OnMenuClickListener;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.beans.AgeHistoryBean;
import seeingvoice.jskj.com.seeingvoice.beans.PureHistoryBean;
import seeingvoice.jskj.com.seeingvoice.beans.VerbalHistoryBean;
import seeingvoice.jskj.com.seeingvoice.history.adapter.MyAdapter;
import seeingvoice.jskj.com.seeingvoice.history.fragment.HearAgeListFragment;
import seeingvoice.jskj.com.seeingvoice.history.fragment.PureHistoryFragment;
import seeingvoice.jskj.com.seeingvoice.share.ShareWXQQ;

public class HistroryL extends MyTopBar {

    private static final String TAG = HistroryL.class.getName();
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private TabLayout mTabLayout;
    private MyAdapter myAdapter;
//    private Handler mHandler = new Handler();
    private static List<PureHistoryBean.DataBean.AllListPureBean> pureDataList = null;//纯音历史从服务端得到网络结果
    private PureHistoryBean pureHistoryBean = null;
    private List<VerbalHistoryBean.DataBean.LanguageListBean> verbalDataList = null;//言语历史从服务端得到网络结果
    private VerbalHistoryBean verbalHistoryBean = null;
    private List<AgeHistoryBean.DataBean.AgeListBean> ageDataList = null;//听力年龄从服务端得到网络结果
    private AgeHistoryBean ageHistoryBean = null;


    @Override
    protected int getContentView_sv() {
        return R.layout.activity_histrory;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("历史记录");
        setToolbarBack(true);

        setToolBarMenuTwo("", R.mipmap.return_icon, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                ShareWXQQ.getInstance().shareFunction(HistroryL.this);
            }
        });

        setToolBarMenuTwo("", R.mipmap.return_icon, null);


        viewPager = findViewById(R.id.in_viewpager);
        mTabLayout = findViewById(R.id.tl_tab);

        fragmentList = new ArrayList<>();
        fragmentList.add(new PureHistoryFragment());
        fragmentList.add(new HearAgeListFragment());
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

//    private void sendMsg(int i) {
//        Message msg = new Message();
//        msg.what = i;
//        mHandler.sendMessage(msg);
//    }

    public void setHandler(Handler handler){
//        mHandler = handler;
    }
}
