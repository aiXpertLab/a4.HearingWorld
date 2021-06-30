package seeingvoice.jskj.com.seeingvoice.l_audiometry.verbaltests.viewpager;

import android.content.Intent;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.verbaltests.BroadCastManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Date:2019/2/21
 * Time:9:58
 * auther:zyy
 */
public class PageIndicator implements ViewPager.OnPageChangeListener {

    private int mPageCount;//页数
    private List<ImageView> mImgList;//保存img总个数
    private int img_select;
    private int img_unSelect;
    private AppCompatActivity mActivityContext;

    public PageIndicator(AppCompatActivity context, LinearLayout linearLayout, int pageCount) {

        this.mPageCount = pageCount;
        this.mActivityContext = context;
        mImgList = new ArrayList<>();
        img_select = R.drawable.dot_select;
        img_unSelect = R.drawable.dot_unselect;
        final int imgSize = 25;

        for (int i = 0; i < mPageCount; i++) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //为小圆点左右添加间距
            params.leftMargin = 10;
            params.rightMargin = 10;
            //给小圆点一个默认大小
            params.height = imgSize;
            params.width = imgSize;
            if (i == 0) {
                imageView.setBackgroundResource(img_select);
            } else {
                imageView.setBackgroundResource(img_unSelect);
            }
            //为LinearLayout添加ImageView
            linearLayout.addView(imageView, params);
            mImgList.add(imageView);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        if (position == 1){
            Intent mIntent = new Intent();
            mIntent.setAction("SKIPTO-VOLUMN-SET-ON");//当音量控制页面被选中时，发送广播通知Activity
            BroadCastManager.getInstance().sendBroadCast(mActivityContext,mIntent);
        }else {
            Intent mIntent = new Intent();
            mIntent.setAction("SKIPTO-VOLUMN-SET-OFF");//当离开音量控制页面时，发送广播通知Activity
            BroadCastManager.getInstance().sendBroadCast(mActivityContext,mIntent);
        }
        for (int i = 0; i < mPageCount; i++) {
            //选中的页面改变小圆点为选中状态，反之为未选中
            if ((position % mPageCount) == i) {
                (mImgList.get(i)).setBackgroundResource(img_select);
            } else {
                (mImgList.get(i)).setBackgroundResource(img_unSelect);
            }
        }
    }

    @Override

    public void onPageScrollStateChanged(int state) {}

}


