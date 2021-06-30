package seeingvoice.jskj.com.seeingvoice.l_audiometry.verbaltests.viewpager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Date:2019/2/11
 * Time:15:39
 * auther:zyy
 */
public class MyFragmentAdapter extends FragmentPagerAdapter {

    List<Fragment> list;
    public MyFragmentAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    /**
     *  根据位置从list里获得Fragment
     * */
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
