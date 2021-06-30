package seeingvoice.jskj.com.seeingvoice.l_audiometry;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import seeingvoice.jskj.com.seeingvoice.R;

//import static seeingvoice.jskj.com.seeingvoice.l_audiometry.L_FragmentLeft.oFragmentL;

public class L6_ViewPagerAdapter extends FragmentPagerAdapter {
    Context mContext;

    public L6_ViewPagerAdapter(@NonNull FragmentManager fm, Context context)    {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position)    {
        Fragment fragment = null;
        if (position == 0)
            fragment = new L6_Frag1();
        else if (position == 1)
            fragment = new L6_Frag2();
        else if (position == 2)
            fragment = new L6_Frag3();
        return fragment;
    }

    @Override
    public int getCount()
    {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position)    {
        String title = null;
        if (position == 0)
            title = mContext.getResources().getString(R.string.global_leftear);
        else if (position == 1)
            title = mContext.getResources().getString(R.string.global_rightear);
        else if (position == 2)
            title = mContext.getResources().getString(R.string.chart_tab3);
        return title;

    }
}
