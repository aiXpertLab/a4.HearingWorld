package seeingvoice.jskj.com.seeingvoice.base;

import android.view.MenuItem;

import seeingvoice.jskj.com.seeingvoice.MyTopBar;

/**
 * Date:2019/9/9
 * Time:8:24
 * auther:zyy
 */
public abstract class OnMenuClickListener implements MyTopBar.OnClickRightListener {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 2000;
    private static long lastClickTime;

    public abstract void onMultiClick(MenuItem v);
    @Override
    public void onClick(MenuItem v) {
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            // 超过点击间隔后再将lastClickTime重置为当前点击时间
            lastClickTime = curClickTime;
            onMultiClick(v);
        }
    }


}