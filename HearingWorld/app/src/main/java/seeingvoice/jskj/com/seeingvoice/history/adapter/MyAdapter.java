package seeingvoice.jskj.com.seeingvoice.history.adapter;

import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class MyAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;

    public MyAdapter(List<Fragment> fragmentList, FragmentManager fm) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

//    @NonNull
//    @Override
//    public Object instantiateItem(@NonNull ViewGroup container, int position) {
////        container.addView(fragmentList.get(position));
//        return fragmentList.get(position);
//    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 1) {
            return "听龄测试";
        }
//        if (position == 2) {
//            return "言语测试";
//        }
        if (position == 0){
            return "纯音测试";
        }
        return "";
    }
}