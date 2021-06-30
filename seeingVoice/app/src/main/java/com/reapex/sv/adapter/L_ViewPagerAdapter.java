package com.reapex.sv.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.reapex.sv.L_Frag1;
import com.reapex.sv.L_Frag2;
import com.reapex.sv.L_Frag3;
import com.reapex.sv.R;

public class L_ViewPagerAdapter extends FragmentPagerAdapter {
    Context mContext;

    public L_ViewPagerAdapter(@NonNull FragmentManager fm, Context context)    {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position)    {
        Fragment fragment = null;
        if (position == 0)
            fragment = new L_Frag1();
        else if (position == 1)
            fragment = new L_Frag2();
        else if (position == 2)
            fragment = new L_Frag3();
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
            title = mContext.getResources().getString(R.string.tab1);
        else if (position == 1)
            title = mContext.getResources().getString(R.string.tab2);
        else if (position == 2)
            title = mContext.getResources().getString(R.string.tab3);
        return title;

    }
}
