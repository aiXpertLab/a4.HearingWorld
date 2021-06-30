package com.seeingvoice.www.svhearing.history.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Date:2019/6/24
 * Time:16:32
 * auther:zyy
 */
public class MyPager extends PagerAdapter {

    private List<View> myViewList;

    public MyPager(List<View> myViewList){
        this.myViewList = myViewList;
    }

    @Override
    public int getCount() {
        return myViewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(myViewList.get(position));
        return myViewList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(myViewList.get(position));
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "纯音测试";
            case 1:
                return "言语测试";
            default:
                return "听龄测试";
        }
    }
}
