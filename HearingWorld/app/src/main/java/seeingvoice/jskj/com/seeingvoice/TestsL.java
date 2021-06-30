package seeingvoice.jskj.com.seeingvoice;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import seeingvoice.jskj.com.seeingvoice.adapter.IndexIconsAdapter;
import seeingvoice.jskj.com.seeingvoice.base.OnMenuClickListener;
import seeingvoice.jskj.com.seeingvoice.history.HistroryL;
import seeingvoice.jskj.com.seeingvoice.share.ShareWXQQ;
import seeingvoice.jskj.com.seeingvoice.tests.ChooseHeadsetTypeL;

import java.util.ArrayList;
import java.util.List;

/**
 * Date:2019/5/10
 * Time:15:12
 * auther:zyy
 */
public class TestsL extends MyTopBar {

    //gridview 初始值
    private GridView gridView;
    private List<Integer> dataList;
    private IndexIconsAdapter adapter;
    private int icon[] = { R.mipmap.puretest_cover,R.mipmap.hear_age_cover,R.mipmap.loudness_cover,R.mipmap.verbel_cover};
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private Context mContext = TestsL.this;

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("听力测试");
        setToolbarBack(true);
        initMoudles();

        setToolBarMenuOne("", R.mipmap.return_icon, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                ShareWXQQ.getInstance().shareFunction(TestsL.this);
            }
        });

        setToolBarMenuTwo("", R.mipmap.return_icon, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                toNextActivity(null, TestsL.this, HistroryL.class);
            }
        });
    }

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_tests_layout;
    }

    private void initMoudles() {

        dataList = new ArrayList();
        for (int i = 0; i < icon.length; i++) {
            dataList.add(icon[i]);
        }
        gridView = findViewById(R.id.index_gridview);
        adapter = new IndexIconsAdapter(this,dataList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        toNextActivity(null, TestsL.this, ChooseHeadsetTypeL.class);
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
