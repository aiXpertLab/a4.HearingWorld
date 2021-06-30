package com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.widget;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class MyViewPagerAdapter extends PagerAdapter {

    private ArrayList<View> listViews;// content
    private int size;// 页数

    public MyViewPagerAdapter(ArrayList<View> listViews) {
        this.listViews = listViews;
        size = listViews == null ? 0 : listViews.size();
    }

    // 自己写的一个方法用来添加数据  这个可是重点啊
    public void setListViews(ArrayList<View> listViews) {
        this.listViews = listViews;
        size = listViews == null ? 0 : listViews.size();
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(listViews.get(position % size));
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        try {
            container.addView(listViews.get(position % size), 0);
        } catch (Exception e) {
            Log.e("zhou", "exception：" + e.getMessage());
        }
        return listViews.get(position % size);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
